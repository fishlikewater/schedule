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

    void addTask(String appName, List<TaskDetail> list);//添加任务

    List<TaskDetail> getTaskList(@NonNull String appName);//获取某个应用下任务

    void updateTaskList(@NonNull String appName, List<TaskDetail> list); //更新某个应用下任务

    List<TaskDetail> getTaskList();//获取所有的任务

    List<TaskDetail> getTaskDetail(); //获取一些任务（redis 获取满足条件的任务集合）

    boolean updateIsUse(String appName, int num, boolean isUse); //更新是否开启任务

    Set<String> getAllClient(String appName); //获取所有的客户端（redis）

    boolean updateActionAddress(String appName, int num, String actionAddress); //更新任务执行客户端

    boolean updateCorn(String appName, int num, String corn);//更新任务corn

    boolean isSync(); //是否已同步数据

    //注册客户端（redis）
    default boolean registerClient(String appName, Channel channel){return true;}

    //删除客户端（redis）
    default boolean removeClient(Channel channel){return true;}

    //获取任务列表中下一个任务
    default TaskDetail getNextTask(){
        return null;
    }
}
