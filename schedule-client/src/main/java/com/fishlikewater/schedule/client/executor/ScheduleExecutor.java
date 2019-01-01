package com.fishlikewater.schedule.client.executor;

import com.fishlikewater.schedule.client.kit.ScheduleJobContext;
import com.fishlikewater.schedule.common.kit.TaskQueue;
import com.fishlikewater.schedule.common.ScheduleJob;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.NamedThreadFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
public class ScheduleExecutor {

    private AtomicInteger stat = new AtomicInteger(0);

    private Thread thread;

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
                scheduleJob.run();
                /** 通知执行完成*/
                //channel.writeAndFlush("");
            }catch (Exception e){
                log.warn("excutor job 【{}】 fail", taskDetail.getDesc());
                /** 通知执行失败*/
                channel.writeAndFlush("");
            }

        });
    }


    /**
     * 重置队列
     */
    public void resetQueue(){
        TaskQueue.getQueue().clear();
        List<TaskDetail> taskDetailList = ScheduleJobContext.getInstance().getCurrentJobList();
        /** 任务添加到队列中*/
        long currentTimeMillis = System.currentTimeMillis();
        for (TaskDetail taskDetail : taskDetailList) {
            long next = taskDetail.getCronSequenceGenerator().next(currentTimeMillis);
            taskDetail.setNextTime(next);
            TaskQueue.add(taskDetail);
        }
    }

    /**
     * 客户端队列执行器
     */
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
                            /** 从队列中取出任务 放到线程池中去 执行*/
                            TaskDetail taskDetail = TaskQueue.peek();
                            if (taskDetail != null) {
                                if (curTimeMillis >= taskDetail.getNextTime()) {
                                    ScheduleExecutor.executor.submit(() -> {
                                        log.info("begin task:{}", taskDetail.getDesc());
                                        taskDetail.getScheduleJob().run();
                                        log.info("end task:{}", taskDetail.getDesc());
                                        /** 执行完成后 添加下次任务到队列*/
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
    }


}
