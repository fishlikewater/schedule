package com.fishlikewater.schedule.client.handler;

import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.client.boot.ClientStart;
import com.fishlikewater.schedule.client.executor.ScheduleExecutor;
import com.fishlikewater.schedule.client.kit.ScheduleJobContext;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ClientMessageHandler
 * @Description
 * @date 2018年12月26日 10:52
 **/
@Slf4j
public class ClientMessageHandler extends SimpleChannelInboundHandler<MessageProbuf.Message> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                ClientStart.build().start();
            }
        }, ScheduleJobContext.getInstance().getRetryInterval(), TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProbuf.Message msg) throws Exception {
        MessageProbuf.MessageType type = msg.getType();
        switch (type) {
            case HEALTH:
                log.info("get receipt health packet from server");
                break;
            case CLOSE:
                ctx.channel().close();
            case CONNECTION:
                List<TaskDetail> list = JSON.parseArray(msg.getBody(), TaskDetail.class);
                log.info("get server send task: {}", JSON.toJSONString(list));
                ScheduleJobContext.getInstance().updateCurrentJobList(list);
                ScheduleExecutor.getInstance().beginJob();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("happen error: ", cause.getMessage());
    }
}
