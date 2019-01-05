package com.fishlikewater.schedule.server.context;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.TaskQueue;
import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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

    @Override
    public List<TaskDetail> getTaskList() {
        List<TaskDetail> list = new ArrayList<>();
        for (Map.Entry<String, List<TaskDetail>> entry : map.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }

    @Override
    public List<TaskDetail> getTaskDetail() {
        return null;
    }

    /**
     * 开启或关闭job
     * @param appName
     * @param num
     * @param isUse
     * @return
     */
    @Override
    public boolean updateIsUse(String appName, int num, boolean isUse) {
        List<TaskDetail> list = map.get(appName);
        for (TaskDetail taskDetail : list) {
            if (taskDetail.getSerialNumber() == num) {
                if (taskDetail.isUse() != isUse) {
                    taskDetail.setUse(isUse);
                    TaskQueue.getQueue().remove(taskDetail);
                    if(isUse){
                        taskDetail.setNextTime(taskDetail.getCronSequenceGenerator().next(System.currentTimeMillis()));
                        TaskQueue.getQueue().add(taskDetail);
                    }
                }
            }
        }

        return false;
    }

    /**
     * 单服务实例交给GroupChannel 管理
     * @param channel
     * @return
     */
    @Override
    public boolean registerClient(String appName, Channel channel) {

        return true;
    }

    @Override
    public Set<String> getAllClient(String appName) {
        ChannelGroup group = ChanneGrouplManager.getGroup(appName);
        if(group != null){
            Set<String> set = new HashSet<>();
            for (Channel channel : group) {
                set.add(channel.remoteAddress().toString());
            }
            return set;
        }
        return null;
    }
    /**
     * 单服务实例交给GroupChannel 管理
     * @param channel
     * @return
     */
    @Override
    public boolean removeClient(Channel channel) {
        return true;
    }

    /**
     * 更新远程执行服务地址
     * @param appName
     * @param num
     * @param actionAddress
     * @return
     */
    @Override
    public boolean updateActionAddress(String appName, int num, String actionAddress) {
        List<TaskDetail> taskDetails = map.get(appName);
        for (TaskDetail taskDetail : taskDetails) {
            if(taskDetail.getSerialNumber() == num){
                taskDetail.setActionAdress(actionAddress);
                return true;
            }
        }
        return false;
    }
}
