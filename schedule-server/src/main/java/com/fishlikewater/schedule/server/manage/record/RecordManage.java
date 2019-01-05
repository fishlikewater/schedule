package com.fishlikewater.schedule.server.manage.record;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.manage.redis.RedisConfig;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName RecordManage
 * @Description
 * @date 2019年01月04日 10:33
 **/
public class RecordManage {

    private static final String SCHEDULE_RECORD = "SCHEDULE_RECORD";
    /** 执行记录缓存*/
    private Cache<Long,TaskDetail> fifoCache = CacheUtil.newFIFOCache(1000);

    private RecordManage(){};

    public static RecordManage getInstance(){

        return RecordBuild.recordManage;
    }

    private static class RecordBuild{
        private static RecordManage recordManage = new RecordManage();
    }

    /**
     * 添加数据到缓存
     * @param taskDetail
     */
    public void addRecord(TaskDetail taskDetail){
        RedisAsyncCommands<String, String> commands = RedisConfig.getInstance().getRedisAsyncCommands();
        if(commands != null){
            commands.llen(SCHEDULE_RECORD).thenAccept(t ->{
                if(t>1000){
                    commands.rpop(SCHEDULE_RECORD);
                }
                commands.lpush(SCHEDULE_RECORD, JSON.toJSONString(taskDetail));
            });
        }else{
            fifoCache.put(System.currentTimeMillis(), taskDetail);
        }
    }

    /**
     * 获取缓存记录
     * @param begin
     * @param end
     * @return
     */
    public List<TaskDetail> getCacheData(int begin, int end) throws ExecutionException, InterruptedException {
        List<TaskDetail> list = new ArrayList<>();
        RedisAsyncCommands<String, String> commands = RedisConfig.getInstance().getRedisAsyncCommands();
        if(commands != null){
            RedisFuture<List<String>> future = commands.lrange(SCHEDULE_RECORD, begin, end);
            List<String> strings = future.get();
            for (String s : strings) {
                list.add(JSON.parseObject(s, TaskDetail.class));
            }
        }else{
            int c = 0;
            for (TaskDetail taskDetail : fifoCache) {
                if(c > end){
                    break;
                }
                if(c >= begin){
                    list.add(taskDetail);
                }
                c++;
            }
        }
        return list;
    }

    public long getRecordCount()throws Exception{
        int size = 0;
        RedisAsyncCommands<String, String> commands = RedisConfig.getInstance().getRedisAsyncCommands();
        if(commands != null){
            RedisFuture<Long> future = commands.llen(SCHEDULE_RECORD);
            size = Math.toIntExact(future.get());
        }else{
            size = fifoCache.size();
        }
        return size;
    }
}
