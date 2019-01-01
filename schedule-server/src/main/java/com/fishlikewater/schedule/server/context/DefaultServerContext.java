package com.fishlikewater.schedule.server.context;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName DefaultServerContext
 * @Description
 * @date 2018年12月27日 17:36
 **/
@Slf4j
public class DefaultServerContext implements ServerContext{

    /** 保存客户端提交的任务列表*/
    private Map<String, List<TaskDetail>> map = new ConcurrentHashMap<>();

    public void addTask(String appName, List<TaskDetail> list){
        List<TaskDetail> taskDetails = map.get(appName);
        if(taskDetails == null){
            map.put(appName, list);
        }
    }

    public List<TaskDetail> getTaskList(@NonNull String appName){

        return map.get(appName);
    }

    public void updateTaskList(@NonNull String appName, List<TaskDetail> list){
        List<TaskDetail> taskDetails = map.get(appName);
        taskDetails.clear();
        taskDetails.addAll(list);
    }
}
