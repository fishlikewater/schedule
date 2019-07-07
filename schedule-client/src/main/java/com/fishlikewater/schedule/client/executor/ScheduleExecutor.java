package com.fishlikewater.schedule.client.executor;

import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.client.kit.ScheduleJobContext;
import com.fishlikewater.schedule.common.ScheduleJob;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.JsonFilter;
import com.fishlikewater.schedule.common.kit.NamedThreadFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleExecutor
 * @Description
 * @date 2018年12月26日 14:32
 **/
@Slf4j
public class ScheduleExecutor {

    //private AtomicInteger stat = new AtomicInteger(0);

    private Thread scheduleThread;

    private volatile boolean scheduleThreadToStop = false;

    public static ScheduleExecutor getInstance(){
        return ScheduleExecutorBuild.scheduleExecutor;
    }

    private static class ScheduleExecutorBuild{
        private static ScheduleExecutor scheduleExecutor = new ScheduleExecutor();
    }

    private static final int cpuNum = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = new ThreadPoolExecutor(cpuNum*4, cpuNum*10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000), new NamedThreadFactory("schedule-executor@"));//CPU核数4-10倍

    /**
     * 客户端任务执行器
     * @param channel
     * @param taskDetail
     */
    public void excutor(Channel channel, TaskDetail taskDetail){
        ScheduleExecutor.executor.submit(() ->{
            try {
                ScheduleJob scheduleJob = taskDetail.getScheduleJob();
                taskDetail.setExecutorTime(System.currentTimeMillis());
                scheduleJob.run(null);
                /** 通知执行完成*/
                taskDetail.setExecutorResult(true);
                MessageProbuf.Message message = MessageProbuf.Message.newBuilder()
                        .setBody(JSON.toJSONString(taskDetail, JsonFilter.sendServerResultFilter))
                        .setType(MessageProbuf.MessageType.RESULT)
                        .setExtend(ScheduleJobContext.getInstance().getAppName())
                        .build();
                channel.writeAndFlush(message);
            }catch (Exception e){
                log.warn("excutor job 【{}】 fail", taskDetail.getDesc());
                /** 通知执行失败*/
                taskDetail.setExecutorResult(false);
                MessageProbuf.Message message = MessageProbuf.Message.newBuilder()
                        .setBody(JSON.toJSONString(taskDetail, JsonFilter.sendServerResultFilter))
                        .setType(MessageProbuf.MessageType.RESULT)
                        .setExtend(ScheduleJobContext.getInstance().getAppName())
                        .build();
                channel.writeAndFlush("");
            }

        });
    }


    /**
     * 设置执行时间
     */
    public void setCorn(){
        List<TaskDetail> taskDetailList = ScheduleJobContext.getInstance().getCurrentJobList();
        /** 任务添加到队列中*/
        long currentTimeMillis = System.currentTimeMillis();
        for (TaskDetail taskDetail : taskDetailList) {
            long next = taskDetail.getCronSequenceGenerator().next(currentTimeMillis);
            taskDetail.setNextTime(next);
        }
    }

    /**
     * 客户端队列执行器
     */
    public void clientExcutor(){
        setCorn();
        /** 单线程寻找待执行job*/
        scheduleThread = new Thread() {
            @Override
            public void run() {
                List<TaskDetail> taskDetailList = null;
                while (!scheduleThreadToStop){
                    try {
                        taskDetailList = ScheduleJobContext.getInstance().getCurrentJobList();
                        long curTimeMillis = System.currentTimeMillis();
                        taskDetailList.stream().parallel().filter(t -> t.isUse() && t.getNextTime() <= curTimeMillis)
                                .forEach(t -> {
                                    ScheduleExecutor.executor.submit(() -> {
                                        try {
                                            t.getScheduleJob().run(t.getParamMap());
                                        }catch (Exception e){
                                            log.warn("excutor job 【{}】 fail", t.getDesc());
                                        }
                                    });
                                    long next = t.getCronSequenceGenerator().next(System.currentTimeMillis());
                                    t.setNextTime(next);
                                });
                        /** 优化线程睡眠机制，若长时间无任务，避免频繁循环刷新*/
                        Optional<TaskDetail> first = taskDetailList.parallelStream().sorted().findFirst();
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
                    }catch (Exception e){
                        if (!scheduleThreadToStop) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        };
        scheduleThread.setDaemon(true);
        scheduleThread.start();
    }

    public void toStop(){
        log.info("停止任务轮询线程");
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
        log.info("停止任务执行线程池");
        executor.shutdown();
    }

    /**
     * 客户端队列执行器
     *//*
    @Deprecated
    public void beginJob(){
        resetQueue();
        if(stat.get() == 0){
            log.info("actuator starts execution...");
            long sleepTime = ScheduleJobContext.getInstance().getSleepTime();
            thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            long curTimeMillis = System.currentTimeMillis();
                            *//** 从队列中取出任务 放到线程池中去 执行*//*
                            TaskDetail taskDetail = TaskQueue.peek();
                            if (taskDetail != null) {
                                if (curTimeMillis >= taskDetail.getNextTime()) {
                                    ScheduleExecutor.executor.submit(() -> {
                                        log.info("begin task:{}", taskDetail.getDesc());
                                        taskDetail.getScheduleJob().run();
                                        log.info("end task:{}", taskDetail.getDesc());
                                        *//** 执行完成后 添加下次任务到队列*//*
                                        long cTimeMillis = System.currentTimeMillis();
                                        long next = taskDetail.getCronSequenceGenerator().next(cTimeMillis);
                                        taskDetail.setNextTime(next);
                                        TaskQueue.remove();
                                        TaskQueue.add(taskDetail);
                                    });
                                }
                            }
                            Thread.sleep(sleepTime);
                        }catch (Exception e){
                            if(thread.isAlive()){
                                boolean interrupted = thread.isInterrupted();
                                if(interrupted){
                                    thread.start();
                                }
                            }else {
                                thread.start();
                            }
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
            stat.set(1);
        }
    }*/


}
