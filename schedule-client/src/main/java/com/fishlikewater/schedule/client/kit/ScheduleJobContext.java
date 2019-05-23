package com.fishlikewater.schedule.client.kit;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
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
    private long retryInterval = 60l;
    @Setter
    @Getter
    private String address;//e.g 127.0.0.1:8080,127.0.0.1:8081


    /**
     *  动态添加任务
     * @param taskDetail 添加任务详情
     */
    public boolean addJob(TaskDetail taskDetail){
        boolean hasClass = false;
        for (TaskDetail detail : allJobList) {
            if(detail.getClassName().equals(taskDetail.getClassName())){
                hasClass = true;
                taskDetail.setScheduleJob(detail.getScheduleJob());
                break;
            }
        }
        if(!hasClass){
            return false;
        }

        allJobList.add(taskDetail);
        taskDetail.setCronSequenceGenerator(new CronSequenceGenerator(taskDetail.getCorn()));
        long currentTimeMillis = System.currentTimeMillis();
        long next = taskDetail.getCronSequenceGenerator().next(currentTimeMillis);
        taskDetail.setNextTime(next);
        jobList.add(taskDetail);
        return true;
    }
    /**
     *  动态删除任务
     * @param num 任务编号
     */
    public void delJob(int num){
        jobList.removeIf(t->{return t.getSerialNumber() == num;});
        allJobList.removeIf(t->{return t.getSerialNumber() == num;});
    }
    /**
     *  动态删除任务
     * @param taskNo 任务编号
     */
    public void delJob(String taskNo){
        jobList.removeIf(t->{return t.getTaskNo().equals(taskNo);});
        allJobList.removeIf(t->{return t.getTaskNo().equals(taskNo);});
    }
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

    public TaskDetail getTaskDetail(TaskDetail taskDetail){
        for (TaskDetail detail : allJobList) {
            if(detail.getSerialNumber() == taskDetail.getSerialNumber()){
                return detail;
            }
        }
        return null;
    }

}
