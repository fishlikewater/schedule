package com.fishlikewater;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.pubsub.api.async.RedisPubSubAsyncCommands;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testRedis() throws InterruptedException, ExecutionException {
        RedisURI redisUri = RedisURI.create("redis://localhost:6379/0");
        RedisClient redisClient = RedisClient.create(redisUri);
        RedisAsyncCommands redisCommands = redisClient.connect().async();
        redisCommands.zadd("testZadd", 1, "8");
        redisCommands.zadd("testZadd", 2, "2");
        redisCommands.zadd("testZadd", 3, "3");
        RedisFuture future = redisCommands.zrange("testZadd", 0, 0);
        Object o = future.get();
        System.out.println(o);
        Thread.sleep(2000);
    }

    @Test
    public void testRedisScore() throws InterruptedException, ExecutionException {
        RedisURI redisUri = RedisURI.create("redis://localhost:6379/0");
        RedisClient redisClient = RedisClient.create(redisUri);
        RedisAsyncCommands redisCommands = redisClient.connect().async();
        RedisFuture future = redisCommands.zrangebyscore("testZadd", Range.create(1,2));
        Object o = future.get();
        System.out.println(o);
        Thread.sleep(2000);
    }


    @Test
    public void redisLock() throws InterruptedException, ExecutionException, TimeoutException {
        RedisURI redisUri = RedisURI.create("redis://localhost:6379/0");
        RedisClient redisClient = RedisClient.create(redisUri);
        String SCRIPT_LOCK = "return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])";
        RedisAsyncCommands<String, String> redisCommands = redisClient.connect().async();
        String[] objects = {"SCHEDULE_LOCK"};
        RedisFuture<Boolean> eval = redisCommands.eval(SCRIPT_LOCK, ScriptOutputType.BOOLEAN, objects,"1","20000");
        Boolean isLock = eval.get(2, TimeUnit.SECONDS);
        System.out.println(isLock);
    }

    @Test
    public void redisUnLock() throws InterruptedException, ExecutionException, TimeoutException {
        RedisURI redisUri = RedisURI.create("redis://localhost:6379/0");
        RedisClient redisClient = RedisClient.create(redisUri);
        String SCRIPT_UNLOCK = "if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])==1) else return 'false' end";
        RedisAsyncCommands<String, String> redisCommands = redisClient.connect().async();
        String[] objects = {"SCHEDULE_LOCK"};
        RedisFuture<Boolean> eval = redisCommands.eval(SCRIPT_UNLOCK, ScriptOutputType.BOOLEAN, objects,"1");
        Boolean isLock = eval.get(2, TimeUnit.SECONDS);
        System.out.println(isLock);
    }

    @Test
    public void test2() throws InterruptedException {
        RedisURI redisUri = RedisURI.create("redis://localhost:6379/0");
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisPubSubConnection<String, String> pubSubConnection = redisClient.connectPubSub();
        pubSubConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                System.out.println("message");
                System.out.println(channel);
                System.out.println(message);
            }

            @Override
            public void message(String pattern, String channel, String message) {
                System.out.println("message22");
            }

            @Override
            public void subscribed(String channel, long count) {
                System.out.println("subscribed");
                System.out.println(channel);
                System.out.println(count);
            }

            @Override
            public void psubscribed(String pattern, long count) {
                System.out.println("psubscribed");
            }

            @Override
            public void unsubscribed(String channel, long count) {
                System.out.println("unsubscribed");
            }

            @Override
            public void punsubscribed(String pattern, long count) {
                System.out.println("punsubscribed");
            }
        });
        RedisPubSubAsyncCommands<String, String> asyncCommands = pubSubConnection.async();
        RedisFuture<Void> subscribe = asyncCommands.subscribe("channel");

        RedisURI redisUri2 = RedisURI.create("redis://localhost:6379/0");
        RedisClient redisClient2 = RedisClient.create(redisUri2);
        StatefulRedisPubSubConnection<String, String> pubSubConnection2 = redisClient2.connectPubSub();
        RedisPubSubAsyncCommands<String, String> asyncCommands2 = pubSubConnection2.async();
       RedisFuture<Long> futurePub = asyncCommands2.publish("channel", "test1");
        futurePub.thenAccept(t->{
            System.out.println("futurePub");
            System.out.println(t);
        });
        Thread.sleep(5000);


    }
}
