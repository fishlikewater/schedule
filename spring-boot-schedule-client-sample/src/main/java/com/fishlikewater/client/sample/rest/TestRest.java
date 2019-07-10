package com.fishlikewater.client.sample.rest;

import com.fishlikewater.schedule.common.context.ScheduleJobContext;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年05月23日 10:42
 * @since
 **/
@RestController
public class TestRest {

    @GetMapping("add")
    public void addTask(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("test", 1);
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setCorn("0/40 * * * * *");
        taskDetail.setDesc("测试定时任务40秒");
        taskDetail.setClassName("ScheduleServer3");
        taskDetail.setTaskNo("mytask1");
        taskDetail.setParamMap(map);
        ScheduleJobContext.getInstance().addJob(taskDetail);
    }
}
