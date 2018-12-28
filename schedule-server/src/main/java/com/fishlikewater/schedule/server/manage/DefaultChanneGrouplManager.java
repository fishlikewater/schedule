package com.fishlikewater.schedule.server.manage;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName DefaultChanneGrouplManager
 * @Description
 * @date 2018年11月25日 10:57
 **/
public class DefaultChanneGrouplManager implements ChanneGrouplManager {


    //private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<String, ChannelGroup> groupMap = new ConcurrentHashMap<>();
    /** 缓存channel*/
    public boolean addChannel(@NonNull String appName, @NonNull Channel channel){
        boolean isSuccess = false;
        ChannelGroup channels = groupMap.get(appName);
        if(channels == null){
            ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            isSuccess = group.add(channel);
            groupMap.put(appName, group);
        }else {
            isSuccess = channels.add(channel);
        }
        return isSuccess;
    };

    /** 删除channel*/
    public boolean removeChannel(@NonNull Channel channel){
        boolean isSuccess = false;
        for (Map.Entry<String, ChannelGroup> entry : groupMap.entrySet()) {
            boolean contains = entry.getValue().contains(channel);
            if (contains){
                isSuccess = entry.getValue().remove(channel);
                break;
            }
        }
        return isSuccess;
    }

    public ChannelGroup getGroup(@NonNull String appName){return groupMap.get(appName);}


}
