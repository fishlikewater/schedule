package com.fishlikewater.schedule.server.executor;


import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
import com.fishlikewater.schedule.common.kit.JsonFilter;
import com.fishlikewater.schedule.server.context.ServerContext;
import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleExecutor
 * @Description
 * @date 2018年12月26日 14:32
 **/
@Slf4j
public class ScheduleExecutor implements Executor{

    private AtomicInteger stat = new AtomicInteger(0);

    private Thread thread;

   /* public static ScheduleExecutor getInstance() {
        return ScheduleExecutorBuild.scheduleExecutor;
    }

    private static class ScheduleExecutorBuild {
        private static ScheduleExecutor scheduleExecutor = new ScheduleExecutor();
    }*/

    /**
     * 重置队列
     */
    public void init(List<TaskDetail> taskDetailList) {
        long currentTimeMillis = System.currentTimeMillis();
        for (TaskDetail taskDetail : taskDetailList) {
            CronSequenceGenerator generator = new CronSequenceGenerator(taskDetail.getCorn());
            long next = generator.next(currentTimeMillis);
            taskDetail.setNextTime(next);
            taskDetail.setCronSequenceGenerator(generator);
        }
    }

    /**
     * 客户端队列执行器
     */
    @Override
    public void beginJob(ServerContext serverContext) {
        if (stat.get() == 0) {
            List<TaskDetail> taskList = serverContext.getTaskList();
            init(taskList);
            log.info("actuator starts execution...");
            thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            long curTimeMillis = System.currentTimeMillis();
                            /** 从队列中取出任务 放到线程池中去 执行*/
                            //TaskDetail taskDetail = TaskQueue.peek();
                            taskList.stream().filter(t -> t.isUse() && t.getNextTime() <= curTimeMillis).forEach(t->{
                                if(StringUtil.isNullOrEmpty(t.getActionAdress())){
                                    /** 发送到一个随机实例执行*/
                                    Channel channel = ChanneGrouplManager.getRandomChannel(t.getAppName());
                                    channel.writeAndFlush(MessageProbuf.Message.newBuilder()
                                            .setType(MessageProbuf.MessageType.EXCUTOR)
                                            .setBody(JSON.toJSONString(t, JsonFilter.sendClientFilter))
                                            .build());
                                }else{
                                    boolean isFound = ChanneGrouplManager.sendByAddress(t);
                                    if (!isFound){
                                        log.warn("not found address:{}", t.getActionAdress());
                                    }
                                }
                                long next = t.getCronSequenceGenerator().next(System.currentTimeMillis());
                                t.setNextTime(next);
                            });
                            Thread.sleep(1000l);
                        } catch (Exception e) {
                            reStartThred(thread);
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
            stat.set(1);
        }
    }


}
