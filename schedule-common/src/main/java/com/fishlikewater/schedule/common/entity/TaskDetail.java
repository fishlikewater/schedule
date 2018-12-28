package com.fishlikewater.schedule.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fishlikewater.schedule.common.ScheduleJob;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
import lombok.Data;
import lombok.experimental.Accessors;

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
public class TaskDetail {

    private int serialNumber;

    private String corn;

    private long nextTime;

    @JSONField(serialize=false)
    private ScheduleJob scheduleJob;

    @JSONField(serialize=false)
    private CronSequenceGenerator cronSequenceGenerator;

    private String desc;

    @JSONField(serialize=false)
    private String actionAdress;
}
