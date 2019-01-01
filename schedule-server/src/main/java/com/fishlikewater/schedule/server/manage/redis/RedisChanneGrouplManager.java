package com.fishlikewater.schedule.server.manage.redis;

import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName RedisChanneGrouplManager
 * @Description
 * @date 2018年12月30日 15:05
 **/
public class RedisChanneGrouplManager implements ChanneGrouplManager {
    private String GROUP_KEY = "group_channel";

    @Override
    public boolean addChannel(String appName, Channel channel) {
        RedisAsyncCommands asyncCommands = RedisConfig.getInstance().getRedisAsyncCommands();
        RedisFuture redisFuture = asyncCommands.lpush(GROUP_KEY, channel.id().asLongText());

        return false;
    }

    @Override
    public boolean removeChannel(Channel channel) {
        return false;
    }

    @Override
    public ChannelGroup getGroup(String appName) {
        return null;
    }

    @Override
    public Channel getRandomChannel(String appName) {
        return null;
    }
}
