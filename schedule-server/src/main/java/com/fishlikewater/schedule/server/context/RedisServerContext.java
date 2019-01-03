package com.fishlikewater.schedule.server.context;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
import com.fishlikewater.schedule.server.manage.redis.RedisConfig;
import com.lambdaworks.redis.Range;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.ScriptOutputType;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private String REDIS_QUEUE_KEY = "REDIS_QUEUE_KEY";

    //private Map<Integer, CronSequenceGenerator> cornMap = new HashMap<>();

    /** 保存客户端提交的任务列表*/
    private Map<String, List<TaskDetail>> map = new ConcurrentHashMap<>();

    private String[] LOCK_NAME = {"SCHEDULE_LOCK"};

    private String SCRIPT_LOCK = "return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])";
    private String SCRIPT_UNLOCK = "if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])==1) else return 'false' end";

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
        RedisFuture<List<String>> redisFuture = asyncCommands().zrange(appName, 0, -1);
        redisFuture.thenAccept(list1 -> {
            if(list!=null && list1.size() == list.size()){
                log.info("task info had upload redis");
            }else {
                asyncCommands().zremrangebyrank(REDIS_QUEUE_KEY, 0, -1).thenAccept(l->{
                    for (TaskDetail taskDetail : list) {
                        CronSequenceGenerator generator = new CronSequenceGenerator(taskDetail.getCorn());
                        taskDetail.setNextTime(generator.next(System.currentTimeMillis()));
                        asyncCommands().zadd(REDIS_QUEUE_KEY, taskDetail.getNextTime(),taskDetail.getSerialNumber()+":"+appName);
                    }
                });
            }
        });
    }

    public List<TaskDetail> getTaskList(@NonNull String appName){

        return null;

    }

    public void updateTaskList(@NonNull String appName, List<TaskDetail> list){

    }

    @Override
    public List<TaskDetail> getTaskList() {

        return null;
    }

    /**
     * 从redis里面获取一个任务(多服务端 通过分布式锁防止重复获取)
     * @return
     */
    @Override
    public TaskDetail getTaskDetail() {
        RedisFuture<List<String>> redisFuture = asyncCommands().zrangebyscore(REDIS_QUEUE_KEY, Range.create(0, System.currentTimeMillis()));
        try {
            /** 分布式锁*/
            RedisFuture<Boolean> eval = asyncCommands().eval(SCRIPT_LOCK, ScriptOutputType.BOOLEAN, LOCK_NAME,  "1", "3000");
            Boolean isLock = eval.get(2, TimeUnit.SECONDS);
            if(isLock){
                List<String> list = redisFuture.get(2, TimeUnit.SECONDS);
                if(list != null && list.size()>0){
                    String jobInfo = list.get(0);
                    String[] split = jobInfo.split(":");
                    int num = Integer.valueOf(split[0]);
                    String appName = split[1];
                    for(TaskDetail taskDetail:map.get(appName)){
                        if(taskDetail.getSerialNumber() == num){
                            asyncCommands().zrem(REDIS_QUEUE_KEY, jobInfo);
                            long next = taskDetail.getCronSequenceGenerator().next(System.currentTimeMillis());
                            taskDetail.setNextTime(next);
                            asyncCommands().zadd(REDIS_QUEUE_KEY, next,jobInfo);
                            return taskDetail;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("redis get value error", e);
        }finally {
            /** 释放锁*/
            asyncCommands.eval(SCRIPT_UNLOCK, ScriptOutputType.BOOLEAN, LOCK_NAME,  "1");
        }
        return null;
    }

    private RedisAsyncCommands<String, String> asyncCommands(){
        if(this.asyncCommands == null){
            this.asyncCommands = RedisConfig.getInstance().getRedisAsyncCommands();
        }
        return this.asyncCommands;
    }
}
