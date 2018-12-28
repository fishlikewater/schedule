package com.fishlikewater.client.sample.schedule;

import com.fishlikewater.schedule.client.annotion.Schedule;
import com.fishlikewater.schedule.common.ScheduleJob;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleServer1
 * @Description
 * @date 2018年12月28日 12:56
 **/
@Schedule(value = "0/5 * * * * *", desc = "测试定时任务2")
public class ScheduleServer2 implements ScheduleJob {
    @Override
    public void run() {
        System.out.println("ScheduleServer2");
    }
}
