package com.fishlikewater.schedule.server.context;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import lombok.NonNull;

import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ServerContext
 * @Description
 * @date 2018年12月30日 15:35
 **/
public interface ServerContext {

    void addTask(String appName, List<TaskDetail> list);

    List<TaskDetail> getTaskList(@NonNull String appName);

    void updateTaskList(@NonNull String appName, List<TaskDetail> list);

    List<TaskDetail> getTaskList();

    List<TaskDetail> getTaskDetail();

    boolean updateIsUse(String appName, int num, boolean isUse);
}
