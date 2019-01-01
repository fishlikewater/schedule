package com.fishlikewater;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.pubsub.api.async.RedisPubSubAsyncCommands;
import org.junit.Test;

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

/*    @Test
    public void testRedis() throws InterruptedException {
        RedisAsyncCommands redisCommands = RedisConfig.getInstance().getRedisCommands("redis://localhost:6379/0");
        List list = new ArrayList();
        list.add("3");
        list.add("4");
        RedisFuture redisFuture = redisCommands.lpush("test11", list.toArray(new String[0]));
        redisFuture.thenAccept(t ->{
            System.out.println(t);
        });

        Thread.sleep(2000);
    }*/

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
