package com.fishlikewater.schedule.server.context;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import com.fishlikewater.schedule.server.manage.redis.RedisConfig;
import com.lambdaworks.redis.Range;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.ScriptOutputType;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName DefaultServerContext
 * @Description
 * @date 2018年12月27日 17:36
 **/
@Slf4j
public class RedisServerContext implements ServerContext{

    private RedisAsyncCommands<String, String> asyncCommands;

    private static final String REDIS_QUEUE_KEY = "REDIS_QUEUE_KEY";

    /** 保存客户端提交的任务列表*/
    private Map<String, List<TaskDetail>> map = new ConcurrentHashMap<>();

    private static final String[] LOCK_NAME = {"SCHEDULE_LOCK"};

    private static final String SCRIPT_LOCK = "return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])";
    private static final String SCRIPT_UNLOCK = "if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])==1) else return 'false' end";
    private List<TaskDetail> cacheList = new ArrayList<>();
    /**
     * 添加任务到redis列表
     * @param appName 应用名称
     * @param list 任务列表
     */
    public void addTask(String appName, List<TaskDetail> list){
        List<TaskDetail> taskDetails = map.get(appName);
        if(taskDetails == null){
            taskDetails = new ArrayList<>();
            for (TaskDetail detail : list) {
                CronSequenceGenerator generator = new CronSequenceGenerator(detail.getCorn());
                detail.setCronSequenceGenerator(generator);
                taskDetails.add(detail);
            }
            map.put(appName, taskDetails);
        }
        ChannelGroup group = ChanneGrouplManager.getGroup(appName);
        if(group.size()<=0){
            asyncCommands().zremrangebyrank(REDIS_QUEUE_KEY, 0, -1).thenAccept(l->{
                for (TaskDetail taskDetail : list) {
                    CronSequenceGenerator generator = new CronSequenceGenerator(taskDetail.getCorn());
                    taskDetail.setNextTime(generator.next(System.currentTimeMillis()));
                    asyncCommands().zadd(REDIS_QUEUE_KEY, taskDetail.getNextTime(),taskDetail.getSerialNumber()+":"+appName);
                }
            });
        }
    }

    public List<TaskDetail> getTaskList(@NonNull String appName){

        return map.get(appName);

    }

    public void updateTaskList(@NonNull String appName, List<TaskDetail> list){
        List<TaskDetail> taskDetails = map.get(appName);
        taskDetails.clear();
        taskDetails.addAll(list);
    }

    @Override
    public List<TaskDetail> getTaskList() {
        List<TaskDetail> list = new ArrayList<>();
        for (Map.Entry<String, List<TaskDetail>> entry : map.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }

    /**
     * 从redis里面获取任务(多服务端 通过分布式锁防止重复获取)
     * @return
     */
    @Override
    public List<TaskDetail> getTaskDetail() {
        cacheList.clear();
        RedisFuture<List<String>> redisFuture = asyncCommands().zrangebyscore(REDIS_QUEUE_KEY, Range.create(0, System.currentTimeMillis()));
        try {
            /** 分布式锁*/
            RedisFuture<Boolean> eval = asyncCommands().eval(SCRIPT_LOCK, ScriptOutputType.BOOLEAN, LOCK_NAME,  "1", "5000");
            Boolean isLock = eval.get(3, TimeUnit.SECONDS);
            if(isLock){
                List<String> list = redisFuture.get(2, TimeUnit.SECONDS);
                if(list != null && list.size()>0){
                    for (String jobInfo : list) {
                        String[] split = jobInfo.split(":");
                        int num = Integer.valueOf(split[0]);
                        String appName = split[1];
                        for(TaskDetail taskDetail:map.get(appName)){
                            if(taskDetail.getSerialNumber() == num){
                                asyncCommands().zrem(REDIS_QUEUE_KEY, jobInfo);
                                if(taskDetail.isUse()){
                                    long next = taskDetail.getCronSequenceGenerator().next(System.currentTimeMillis());
                                    taskDetail.setNextTime(next);
                                    asyncCommands().zadd(REDIS_QUEUE_KEY, next,jobInfo);
                                    cacheList.add(taskDetail);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            return cacheList;
        } catch (Exception e) {
            log.error("redis get value timeout");
        }finally {
            /** 释放锁*/
            asyncCommands.eval(SCRIPT_UNLOCK, ScriptOutputType.BOOLEAN, LOCK_NAME,  "1");
        }
        return null;
    }

    /**
     * 更新某个job 开启或关闭
     * @param appName
     * @param num
     * @param isUse
     * @return
     */
    @Override
    public boolean updateIsUse(String appName, int num, boolean isUse) {
        try {
            /** 分布式锁*/
            RedisFuture<Boolean> eval = asyncCommands().eval(SCRIPT_LOCK, ScriptOutputType.BOOLEAN, LOCK_NAME,  "1", "5000");
            Boolean isLock = eval.get(3, TimeUnit.SECONDS);
            if(isLock){
                List<TaskDetail> list = map.get(appName);
                for (TaskDetail taskDetail : list) {
                    if(taskDetail.getSerialNumber() == num){
                        if(taskDetail.isUse() != isUse){
                            taskDetail.setUse(isUse);
                            RedisFuture<Long> zrem = asyncCommands().zrem(REDIS_QUEUE_KEY, taskDetail.getSerialNumber() + ":" + appName);
                            zrem.get();
                            if(isUse){
                                log.info("open task 【{}】【{}】", taskDetail.getAppName(), taskDetail.getDesc());
                                asyncCommands().zadd(REDIS_QUEUE_KEY, taskDetail.getNextTime(),taskDetail.getSerialNumber()+":"+appName);
                            }else{
                                log.info("close task 【{}】【{}】", taskDetail.getAppName(), taskDetail.getDesc());
                            }
                        }
                        break;
                    }
                }
            }
        }catch (Exception e){
            log.error("redis get value timeout");
        }finally {
            /** 释放锁*/
            asyncCommands.eval(SCRIPT_UNLOCK, ScriptOutputType.BOOLEAN, LOCK_NAME,  "1");
        }

        return true;
    }

    /**
     * 添加客户端缓存到redis
     * @param appName
     * @param channel
     * @return
     */
    @Override
    public boolean registerClient(String appName, Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        asyncCommands().sadd(appName, remoteAddress.getHostString().toString());
        return true;
    }

    /**
     * 获取应用所有已注册的客户端
     * @param appName
     * @return
     */
    @Override
    public Set<String> getAllClient(String appName) {
        RedisFuture<Set<String>> future = asyncCommands().smembers(appName);
        try {
            Set<String> set = future.get(2, TimeUnit.SECONDS);
            return set;
        } catch (Exception e) {
            log.error("get all client exception", e);
        }
        return null;
    }

    /**
     * 删除redis中的客户端缓存
     * @param channel
     * @return
     */
    @Override
    public boolean removeClient(Channel channel) {
        for (Map.Entry<String, ChannelGroup> entry : ChanneGrouplManager.getGroupMap().entrySet()) {
            boolean contains = entry.getValue().contains(channel);
            if (contains){
                InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
                asyncCommands().srem(entry.getKey(), remoteAddress.getHostString().toString());
                break;
            }
        }
        return true;
    }

    /**
     * 更新远程执行服务地址
     * @param appName
     * @param num
     * @param actionAddress
     * @return
     */
    @Override
    public boolean updateActionAddress(String appName, int num, String actionAddress) {
        List<TaskDetail> taskDetails = map.get(appName);
        for (TaskDetail taskDetail : taskDetails) {
            if(taskDetail.getSerialNumber() == num){
                taskDetail.setActionAdress(actionAddress);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSync() {
        return false;
    }

    private RedisAsyncCommands<String, String> asyncCommands(){
        if(this.asyncCommands == null){
            this.asyncCommands = RedisConfig.getInstance().getRedisAsyncCommands();
        }
        return this.asyncCommands;
    }
}
