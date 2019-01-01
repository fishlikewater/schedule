package com.fishlikewater.schedule.server.manage.redis;

import com.lambdaworks.redis.pubsub.RedisPubSubListener;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleRedisPubSubListener
 * @Description
 * @date 2018年12月30日 21:30
 **/
public class ScheduleRedisPubSubListener implements RedisPubSubListener {
    @Override
    public void message(Object channel, Object message) {

    }

    @Override
    public void message(Object pattern, Object channel, Object message) {

    }

    @Override
    public void subscribed(Object channel, long count) {

    }

    @Override
    public void psubscribed(Object pattern, long count) {

    }

    @Override
    public void unsubscribed(Object channel, long count) {

    }

    @Override
    public void punsubscribed(Object pattern, long count) {

    }
}
