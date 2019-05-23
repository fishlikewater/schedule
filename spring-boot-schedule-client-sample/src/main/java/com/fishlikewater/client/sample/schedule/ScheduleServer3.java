package com.fishlikewater.client.sample.schedule;

import com.fishlikewater.schedule.client.annotion.Schedule;
import com.fishlikewater.schedule.common.ScheduleJob;

import java.util.Map;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleServer1
 * @Description
 * @date 2018年12月28日 12:56
 **/
@Schedule(value = "0/30 * * * * *", desc = "测试定时任务30秒")
public class ScheduleServer3 implements ScheduleJob {
    @Override
    public void run(Map map) {
        if(map != null){
            System.out.println(map.toString());
        }
        System.out.println("测试定时任务30秒");
    }
}
