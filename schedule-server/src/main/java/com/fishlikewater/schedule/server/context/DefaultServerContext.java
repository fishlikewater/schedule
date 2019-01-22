package com.fishlikewater.schedule.server.context;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import com.fishlikewater.schedule.server.manage.sqlite.Sql2oConfig;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
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

    private List<TaskDetail> list = new ArrayList<>();//所有任务

    private boolean isSync = true;

    public void addTask(String appName, List<TaskDetail> list){
        List<TaskDetail> taskDetails = map.get(appName);
        if(taskDetails == null){
            List<TaskDetail> taskWithDB = Sql2oConfig.getTaskWithDB(appName);
            for (TaskDetail taskDetail : list) {
                int serialNumber = taskDetail.getSerialNumber();
                for (TaskDetail detail : taskWithDB) {
                    if(serialNumber == detail.getSerialNumber()){
                        taskDetail.setUse(detail.isUse());
                        taskDetail.setCorn(detail.getCorn());
                        taskDetail.setActionAdress(detail.getActionAdress());
                        break;
                    }
                }
            }
            map.put(appName, list);
            this.list.clear(); //有新的任务 清空缓存数据
            Sql2oConfig.resetTaskWithDB(list, appName);
        }
    }

    public List<TaskDetail> getTaskList(@NonNull String appName){
        List<TaskDetail> list = map.get(appName);
        if(list == null || list.size() == 0){
            list = Sql2oConfig.getTaskWithDB(appName);
        }
        return list;
    }

    @Deprecated
    public void updateTaskList(@NonNull String appName, List<TaskDetail> list){
        List<TaskDetail> taskDetails = map.get(appName);
        taskDetails.clear();
        taskDetails.addAll(list);
    }

    @Override
    public List<TaskDetail> getTaskList() {
        if(list.size() == 0){
            for (Map.Entry<String, List<TaskDetail>> entry : map.entrySet()) {
                list.addAll(entry.getValue());
            }
            if(list.size() == 0){
                list = Sql2oConfig.getTaskWithDB();
            }
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
        if(list != null){
            for (TaskDetail taskDetail : list) {
                if (taskDetail.getSerialNumber() == num) {
                    if (taskDetail.isUse() != isUse) {
                        taskDetail.setUse(isUse);
                        Sql2oConfig.updateTask(taskDetail);
                    }
                }
            }
        }else{
            return false;
        }

        return true;
    }

    /**
     * 获取所有的客户端
     * @param appName
     * @return
     */
    @Override
    public Set<String> getAllClient(String appName) {
        ChannelGroup group = ChanneGrouplManager.getGroup(appName);
        if(group != null){
            Set<String> set = new HashSet<>();
            for (Channel channel : group) {
                InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
                set.add(remoteAddress.getHostString()+":"+remoteAddress.getPort());
            }
            return set;
        }
        return null;
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
                Sql2oConfig.updateTask(taskDetail);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSync() {
        return isSync;
    }
}
