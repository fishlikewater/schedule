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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ClusterScheduleExecutor
 * @Description
 * @date 2019年01月02日 17:29
 **/
public class ClusterScheduleExecutor implements Executor {

    private AtomicInteger stat = new AtomicInteger(0);

    private Thread thread;

    @Override
    public void beginJob(ServerContext serverContext) {
        if (stat.get() == 0) {
            RedisAsyncCommands commands = RedisConfig.getInstance().getRedisAsyncCommands();
            thread = new Thread(()->{
                while (true){
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
                       reStartThred(thread);
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
            stat.set(1);
        }
    }
}
