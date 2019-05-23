package com.fishlikewater.schedule.common;

import java.util.Map;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName Schedule
 * @Description
 * @date 2018年12月25日 15:36
 **/
public interface ScheduleJob {

    void run(Map map);
}
