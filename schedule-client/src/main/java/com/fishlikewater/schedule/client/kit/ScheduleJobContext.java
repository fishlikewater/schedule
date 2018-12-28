package com.fishlikewater.schedule.client.kit;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleJobContext
 * @Description
 * @date 2018年12月25日 16:23
 **/
public class ScheduleJobContext {

    private AtomicInteger stat = new AtomicInteger(0);

    private List<TaskDetail> jobList = new ArrayList<>();

    private List<TaskDetail> allJobList = new ArrayList<>();

    private String[] basePath = new String[0];

    private ScheduleJobContext(){}

    public static ScheduleJobContext getInstance(){return ScheduleJobContextBuild.scheduleJobContext;}

    private static class ScheduleJobContextBuild{
        private static ScheduleJobContext scheduleJobContext = new ScheduleJobContext();
    }

    @Setter
    @Getter
    private int PORT = 8080;
    @Setter
    @Getter
    private String HOST = "127.0.0.1";
    @Setter
    @Getter
    private String appName = "default";
    @Setter
    @Getter
    private long healthBeat = 60l;
    @Setter
    @Getter
    private long sleepTime = 1000l;
    @Setter
    @Getter
    private long retryInterval = 60l;

    /**
     * 获取扫描到的所有 实例
     * @return
     */
    public List<TaskDetail> getAllJobList(){
        if(stat.get() == 0){
            List<TaskDetail> taskDetailList = ScannerScheduleKit.loadJobClass(basePath);
            allJobList = taskDetailList;
            stat.compareAndSet(0, 1);
        }
        return allJobList;
    }

    /**
     * 更新当前应执行的任务实例
     * @param list
     * @return
     */
    public List<TaskDetail> updateCurrentJobList(List<TaskDetail> list){
        jobList.clear();
        for (TaskDetail detail : allJobList) {
            for (TaskDetail taskDetail : list) {
                if(taskDetail.getSerialNumber() == detail.getSerialNumber()){
                    jobList.add(detail);
                }
            }
        }
        return jobList;
    }

    /**
     * 获取当前实例 能过执行的任务实例
     * @return
     */
    public List<TaskDetail> getCurrentJobList(){
        return jobList;
    }

    /**
     * 设置扫描路径
     * @param scannerPath
     * @return
     */
    public ScheduleJobContext setBasePath(String scannerPath){
        String[] scannerPathArr = scannerPath.split(",");
        this.basePath = scannerPathArr;
        return this;
    }

}
