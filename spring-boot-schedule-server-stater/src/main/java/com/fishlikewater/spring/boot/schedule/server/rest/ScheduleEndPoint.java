package com.fishlikewater.spring.boot.schedule.server.rest;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.context.ServerContext;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleEndPoint
 * @Description
 * @date 2018年12月28日 17:24
 **/

@Endpoint(id = "schedule")
public class ScheduleEndPoint {

    @ReadOperation(produces = "application/json")
    public List<TaskDetail> getScheduleInfo(@Selector String appName) {
        return ServerContext.getTaskList(appName);
    }

}
