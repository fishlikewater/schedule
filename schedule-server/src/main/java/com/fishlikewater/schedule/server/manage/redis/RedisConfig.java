package com.fishlikewater.schedule.server.manage.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.pubsub.api.async.RedisPubSubAsyncCommands;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName RedisConfig
 * @Description
 * @date 2018年12月30日 15:45
 **/
@Data
public class RedisConfig {

    private RedisAsyncCommands<String, String> redisAsyncCommands;

    private RedisClient redisClient;

    private String redisStr;

    private String channel = "schedule_channel";

    private long timeout = 60l;

    private RedisPubSubAsyncCommands redisPubSubAsyncCommands;

    public static RedisConfig getInstance(){
        return RedisConfigBuild.redisConfig;
    }
    private static class RedisConfigBuild{
        private static RedisConfig redisConfig = new RedisConfig();

    }

    private RedisConfig(){
    }

    /**
     * redis 异步连接
     * @return
     */
    public RedisAsyncCommands initRedisCommands(){
        RedisAsyncCommands<String, String> redisAsyncCommands = redisClient.connect().async();
        this.redisAsyncCommands = redisAsyncCommands;
        return redisAsyncCommands;
    }

    /**
     * 获取redis客户端
     * @param address
     * @return
     */
    public RedisClient initRedisClient(String address){
        RedisURI redisUri = RedisURI.create(address);
        RedisClient redisClient = RedisClient.create(redisUri);
        redisClient.setDefaultTimeout(timeout, TimeUnit.SECONDS);
        this.redisClient = redisClient;
        return redisClient;
    }

    /**
     * 订阅redis
     */
    protected void initPubAndSub(){
        StatefulRedisPubSubConnection<String, String> pubSubConnection = redisClient.connectPubSub();
        pubSubConnection.addListener(new ScheduleRedisPubSubListener());
        RedisPubSubAsyncCommands<String, String> asyncCommands = pubSubConnection.async();
        RedisFuture<Void> subscribe = asyncCommands.subscribe(channel);
        this.redisPubSubAsyncCommands = asyncCommands;
    }

    public void pulish(String message){
        RedisFuture redisFuture = redisPubSubAsyncCommands.publish(channel, message);

    }



}
