package com.fishlikewater.schedule.server.manage;

import cn.hutool.core.util.RandomUtil;
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
 * @ClassName ChanneGrouplManager
 * @Description
 * @date 2018年11月25日 10:57
 **/
public class ChanneGrouplManager {


    private static Map<String, ChannelGroup> groupMap = new ConcurrentHashMap<>();
    /** 缓存channel*/
    public static boolean addChannel(@NonNull String appName, @NonNull Channel channel){
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
    public static boolean removeChannel(@NonNull Channel channel){
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

    public static ChannelGroup getGroup(@NonNull String appName){return groupMap.get(appName);}

    public static Channel getRandomChannel(String appName) {
        ChannelGroup group = getGroup(appName);
        if(group.size()>0){
            int anInt = RandomUtil.randomInt(group.size());
            Channel[] channels = group.toArray(new Channel[0]);
            return channels[anInt];
        }else{
            return null;
        }

    }


}
