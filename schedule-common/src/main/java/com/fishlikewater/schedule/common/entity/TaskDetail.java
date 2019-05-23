package com.fishlikewater.schedule.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fishlikewater.schedule.common.ScheduleJob;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName TaskDetail
 * @Description
 * @date 2018年12月27日 10:20
 **/
@Data
@Accessors(chain = true)
public class TaskDetail implements Comparable<TaskDetail>{

    private int serialNumber;

    private String corn;

    private long nextTime;

    private String appName;

    @JSONField(serialize=false)
    private ScheduleJob scheduleJob;

    @JSONField(serialize=false)
    private CronSequenceGenerator cronSequenceGenerator;

    private String desc;

    private String actionAdress;

    private boolean executorResult;//执行结果

    private long executorTime;//执行时间

    //@JSONField(serialize=false)
    private boolean isUse = true;//是否使用

    /** 参数传递 暂时只用于单机版**/
    @JSONField(serialize=false)
    private Map<String, Object> paramMap;
    /** 动态编号 暂时只用于单机版**/
    @JSONField(serialize=false)
    private String taskNo;
    /** 执行类名 暂时只用于单机版**/
    @JSONField(serialize=false)
    private String className;
    @Override
    public int compareTo(TaskDetail o) {
        return (int) (this.getNextTime()-o.getNextTime());
    }
}
