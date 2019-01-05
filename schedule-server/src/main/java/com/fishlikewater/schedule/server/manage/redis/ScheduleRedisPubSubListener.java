package com.fishlikewater.schedule.server.manage.redis;

import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleRedisPubSubListener
 * @Description
 * @date 2018年12月30日 21:30
 **/
@Slf4j
public class ScheduleRedisPubSubListener implements RedisPubSubListener<String, String> {
    @Override
    public void message(String channel, String message) {
        log.info("get message by channel:{}", channel);
        TaskDetail taskDetail = JSON.parseObject(message, TaskDetail.class);
        ChanneGrouplManager.sendByAddress(taskDetail);
    }

    @Override
    public void message(String pattern, String channel, String message) {

    }

    @Override
    public void subscribed(String channel, long count) {
        log.info("redis subscribed the channel:{}", channel);
    }

    @Override
    public void psubscribed(String pattern, long count) {

    }

    @Override
    public void unsubscribed(String channel, long count) {

    }

    @Override
    public void punsubscribed(String pattern, long count) {

    }
}
