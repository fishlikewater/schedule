package com.fishlikewater.schedule.server.boot;

import com.fishlikewater.schedule.common.kit.NamedThreadFactory;
import com.fishlikewater.schedule.common.kit.ScheduleKit;
import com.fishlikewater.schedule.server.handler.ServerHandlerInitializer;
import com.fishlikewater.schedule.server.manage.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ServerStart
 * @Description
 * @date 2018年12月25日 14:21
 **/
@Slf4j
public class ServerStart {

    /** 处理连接*/
    private EventLoopGroup bossGroup;
    /** 处理连接后的channel*/
    private EventLoopGroup workerGroup;
    /** channel 管理实现*/
    @Getter
    @Setter
    private ChanneGrouplManager channeGrouplManager;
    @Getter
    @Setter
    private ConnectionValidate connectionValidate;
    @Getter
    @Setter
    private TaskDistribution taskDistribution;

    private static final ServerStart build =  new ServerStart();

    public static ServerStart build(){return build;};

    private ServerStart(){

    }

    public void run(int port, String address){
        if(this.channeGrouplManager == null){
            this.channeGrouplManager = new DefaultChanneGrouplManager();
        }
        if(this.connectionValidate == null){
            this.connectionValidate = new DefaultConnectionValidate();
        }
        if (taskDistribution == null){
            this.taskDistribution = new DefaultTaskDistribution();
        }
        start(port, address);
    }

    private void start(int port, String adress) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.SO_BACKLOG, 8192);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        if (ScheduleKit.epollIsAvailable()) {//linux系统下使用epoll
            bossGroup = new EpollEventLoopGroup(0, new NamedThreadFactory("epoll-boss@"));
            workerGroup = new EpollEventLoopGroup(0, new NamedThreadFactory("epoll-worker@"));
            bootstrap.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
        } else {
            bossGroup = new NioEventLoopGroup(0, new NamedThreadFactory("nio-boss@"));
            workerGroup = new NioEventLoopGroup(0, new NamedThreadFactory("nio-worker@"));
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        }
        bootstrap.childHandler(new ServerHandlerInitializer(this));
        try {
            Channel ch = bootstrap.bind(adress, port).sync().channel();
            log.info("start ScheduleServer this port:{} and adress:{}", port, adress);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("start ScheduleServer server fail", e);
        }finally {
            stop();
        }
    }

    /**
     * 关闭服务
     */
    public void stop() {
        log.info("⬢ scheduleClient shutdown ...");
        try {
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully().sync();
            }
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully().sync();
            }
            log.info("⬢ scheduleClient shutdown successful");
        } catch (Exception e) {
            log.error("scheduleClient shutdown error", e);
        }
    }

    public static void main(String[] args) {
        ServerStart.build().run(8080, "127.0.0.1");
    }
}
