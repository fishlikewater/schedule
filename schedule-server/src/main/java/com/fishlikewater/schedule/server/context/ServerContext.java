package com.fishlikewater.schedule.server.context;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

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

    boolean registerClient(String appName, Channel channel);

    Set<String> getAllClient(String appName);

    boolean removeClient(Channel channel);

    boolean updateActionAddress(String appName, int num, String actionAddress);

    boolean isSync();
}
