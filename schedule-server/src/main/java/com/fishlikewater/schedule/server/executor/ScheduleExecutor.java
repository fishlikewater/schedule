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
import java.util.Optional;
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
public class ScheduleExecutor implements Executor {

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
            init(serverContext.getTaskList());
            log.info("actuator starts execution...");
            thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            long curTimeMillis = System.currentTimeMillis();
                            serverContext
                                    .getTaskList().stream()
                                    .parallel()
                                    .sorted()
                                    .limit(50)
                                    .filter(t -> t.isUse() && t.getNextTime() <= curTimeMillis)
                                    .forEach(t -> {
                                        if (StringUtil.isNullOrEmpty(t.getActionAdress())) { //是否指定执行job的实例
                                            /** 发送到一个随机实例执行*/
                                            Channel channel = ChanneGrouplManager.getRandomChannel(t.getAppName());
                                            if(channel != null){
                                                channel.writeAndFlush(MessageProbuf.Message.newBuilder()
                                                        .setType(MessageProbuf.MessageType.EXCUTOR)
                                                        .setBody(JSON.toJSONString(t, JsonFilter.sendClientFilter))
                                                        .build());
                                            }else{
                                                log.warn("not found client connection");
                                            }
                                        } else {
                                            boolean isFound = ChanneGrouplManager.sendByAddress(t);
                                            if (!isFound) {
                                                log.warn("not found address:{}", t.getActionAdress());
                                            }
                                        }
                                        long next = t.getCronSequenceGenerator().next(System.currentTimeMillis());
                                        t.setNextTime(next);
                                    });
                            /** 优化线程睡眠机制，若长时间无任务，避免频繁循环刷新*/
                            Optional<TaskDetail> first = serverContext.getTaskList().stream().parallel().sorted().findFirst();
                            if(first.isPresent()){
                                long timeMillis = System.currentTimeMillis();
                                long nextTime = first.get().getNextTime();
                                if ((nextTime-timeMillis) >5*60*1000){ //长时间无job 保证5分钟刷一次
                                    Thread.sleep(5*60*1000l);
                                }else if ((nextTime-timeMillis) > 1000 && (nextTime-timeMillis)<=5*60*1000){
                                    Thread.sleep(nextTime-timeMillis);
                                }else {
                                    Thread.sleep(1000l);
                                }
                            }else {
                                Thread.sleep(5*60*1000);//任务为空的时候线程5分钟刷一次
                            }
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
