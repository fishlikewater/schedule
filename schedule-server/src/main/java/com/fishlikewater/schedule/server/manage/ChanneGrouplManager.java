package com.fishlikewater.schedule.server.manage;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.NonNull;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ChanneGrouplManager
 * @Description
 * @date 2018年11月24日 14:35
 **/
public interface ChanneGrouplManager {



    /** 缓存channel*/
    boolean addChannel(@NonNull String appName, @NonNull Channel channel);

    /** 删除channel*/
    boolean removeChannel(@NonNull Channel channel);

    ChannelGroup getGroup(@NonNull String appName);

    Channel getRandomChannel(String appName);


}
