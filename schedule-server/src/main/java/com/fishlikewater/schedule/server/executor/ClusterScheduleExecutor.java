package com.fishlikewater.schedule.server.executor;

import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.JsonFilter;
import com.fishlikewater.schedule.server.context.ServerContext;
import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import com.fishlikewater.schedule.server.manage.redis.RedisConfig;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ClusterScheduleExecutor
 * @Description
 * @date 2019年01月02日 17:29
 **/
@Slf4j
public class ClusterScheduleExecutor implements Executor {

    private AtomicInteger stat = new AtomicInteger(0);

    private Thread scheduleThread;

    private volatile boolean scheduleThreadToStop = false;

    @Override
    public void beginJob(ServerContext serverContext) {
        if (stat.get() == 0) {
            RedisAsyncCommands commands = RedisConfig.getInstance().getRedisAsyncCommands();
            scheduleThread = new Thread(()->{
                while (!scheduleThreadToStop){
                    try {
                        List<TaskDetail> taskDetail = serverContext.getTaskDetail();
                        if(taskDetail != null && taskDetail.size()>0){
                            for (TaskDetail detail : taskDetail) {
                                if(StringUtil.isNullOrEmpty(detail.getActionAdress())){
                                    /** 发送到一个随机实例执行*/
                                    Channel channel = ChanneGrouplManager.getRandomChannel(detail.getAppName());
                                    if(channel != null){
                                        channel.writeAndFlush(MessageProbuf.Message.newBuilder()
                                                .setType(MessageProbuf.MessageType.EXCUTOR)
                                                .setBody(JSON.toJSONString(detail, JsonFilter.sendClientFilter))
                                                .build());
                                    }
                                }else{
                                    boolean isFound = ChanneGrouplManager.sendByAddress(detail);
                                    if(!isFound){
                                        RedisConfig.getInstance().pulish(JSON.toJSONString(detail, JsonFilter.sendClientFilter));
                                    }
                                }
                            }
                        }
                        serverContext.getTaskDetail();
                        Thread.sleep(1000l);
                    }catch (Exception e){
                        if (!scheduleThreadToStop) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            });
            scheduleThread.setDaemon(true);
            scheduleThread.start();
            stat.set(1);
        }
    }

    @Override
    public void toStop() {
        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);  // wait
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        if (scheduleThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            scheduleThread.interrupt();
            try {
                scheduleThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
