package com.fishlikewater.schedule.client.boot;

import com.alibaba.fastjson.JSONObject;
import com.fishlikewater.schedule.client.handler.ClientHandlerInitializer;
import com.fishlikewater.schedule.client.kit.ScheduleJobContext;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.NamedThreadFactory;
import com.fishlikewater.schedule.common.kit.ScheduleKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

/**
 * @a1uthor zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ClientStart
 * @Description
 * @date 2018年12月25日 14:21
 **/
@Slf4j
@Accessors(chain = true)
public class ClientStart {

    /**
     * 处理连接
     */
    private EventLoopGroup bossGroup;
    private Bootstrap clientstrap;

    @Getter
    private Channel channel;

    private static final ClientStart build = new ClientStart();

    public static ClientStart build() {
        return build;
    }

    private ConnectionListener connectionListener = new ConnectionListener();

    private int retryCount = 3;
    private String HOST;
    private int PORT;
    private String appName;
    private ClientStart() {

    }

    public ClientStart retryOne() {
        this.retryCount -= 1;
        return this;
    }

    public void run() {
        registerShutdownHook(() ->{
            this.stop();
            return null;

        });
        HOST = ScheduleJobContext.getInstance().getHOST();
        PORT = ScheduleJobContext.getInstance().getPORT();
        appName = ScheduleJobContext.getInstance().getAppName();
        bootstrapConfig();
        start();
    }

    private Bootstrap bootstrapConfig(){
        if(clientstrap == null) clientstrap = new Bootstrap();
        clientstrap.option(ChannelOption.SO_REUSEADDR, true);
        clientstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        if (ScheduleKit.epollIsAvailable()) {//linux系统下使用epoll
            bossGroup = new EpollEventLoopGroup(0, new NamedThreadFactory("client-epoll-boss@"));
            clientstrap.group(bossGroup).channel(EpollSocketChannel.class);
        } else {
            bossGroup = new NioEventLoopGroup(0, new NamedThreadFactory("client-nio-boss@"));
            clientstrap.group(bossGroup).channel(NioSocketChannel.class);
        }
        clientstrap.handler(new ClientHandlerInitializer());
        clientstrap.remoteAddress(new InetSocketAddress(HOST, PORT));
        return clientstrap;
    }

    public void start() {
        try {
            ChannelFuture future = clientstrap.connect().addListener(connectionListener).sync();
            this.channel = future.channel();
            log.info("start scheduleClient this port:{} and adress:{}", PORT, HOST);
            afterConnectionSuccessful(channel);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("start scheduleClient server fail");
        }
    }


    /**
     * 连接成功后的操作
     * @param channel
     */
    private void afterConnectionSuccessful(Channel channel) {
        /** 发送首先发送验证信息*/
        MessageProbuf.Message validMessage = MessageProbuf.Message
                .newBuilder()
                .setBody("token")
                .setExtend(appName)
                .setType(MessageProbuf.MessageType.VALID)
                .build();
        channel.writeAndFlush(validMessage).addListener(new FutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                sendScannerInfo(channel);
            }
        });
    }

    /**
     * 当前实例 任务信息
     */
    private void sendScannerInfo(Channel channel) {
        List<TaskDetail> allJobList = ScheduleJobContext.getInstance().getAllJobList();
        String jsonString = JSONObject.toJSONString(allJobList);
        MessageProbuf.Message initMessage = MessageProbuf.Message
                .newBuilder()
                .setExtend(appName)
                .setBody(jsonString)
                .setType(MessageProbuf.MessageType.CONNECTION)
                .build();
        channel.writeAndFlush(initMessage);
    }

    /**
     * 关闭服务
     */
    @PreDestroy
    public void stop() {
        log.info("⬢ scheduleClient shutdown ...");
        try {
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully();
            }
            log.info("⬢ scheduleClient shutdown successful");
        } catch (Exception e) {
            log.error("scheduleClient shutdown error", e);
        }
    }
    private void registerShutdownHook(Supplier supplier){
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    System.out.println("startting shutdown working......");
                    supplier.get();
                } catch (Throwable e) {
                    log.error("shutdownHook error", e);
                } finally {
                    log.info("jvm shutdown");
                }
            }

        });
    }
    public static void main(String[] args) {
        ScheduleJobContext.getInstance().setBasePath("com.fishlikewater");
        ClientStart.build().run();
    }
}
