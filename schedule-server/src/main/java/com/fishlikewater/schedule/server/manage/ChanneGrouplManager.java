package com.fishlikewater.schedule.server.manage;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.JsonFilter;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.NonNull;

import java.net.InetSocketAddress;
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
        boolean isSuccess;
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

    public static Map<String, ChannelGroup> getGroupMap(){return groupMap;}

    /**
     * 随机获取一个客户端
     * @param appName
     * @return
     */
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

    /**
     * 发送到指定的客户端
     * @param taskDetail
     * @return
     */
    public static boolean sendByAddress(TaskDetail taskDetail){
        boolean isFound;
        ChannelGroup group = ChanneGrouplManager.getGroup(taskDetail.getAppName());
        isFound = false;
        for (Channel channel : group) {
            InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
            if(remoteAddress.getHostString().equals(taskDetail.getActionAdress())){
                isFound = true;
                channel.writeAndFlush(MessageProbuf.Message.newBuilder()
                        .setType(MessageProbuf.MessageType.EXCUTOR)
                        .setBody(JSON.toJSONString(taskDetail, JsonFilter.sendClientFilter))
                        .build());
                break;
            }
        }
        return isFound;
    }


}
