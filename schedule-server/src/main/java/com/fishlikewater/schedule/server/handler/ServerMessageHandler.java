package com.fishlikewater.schedule.server.handler;

import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.context.ServerContext;
import com.fishlikewater.schedule.server.executor.ScheduleExecutor;
import com.fishlikewater.schedule.server.manage.ChanneGrouplManager;
import com.fishlikewater.schedule.server.manage.ConnectionValidate;
import com.fishlikewater.schedule.server.manage.TaskDistribution;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ServerMessageHandler
 * @Description
 * @date 2018年12月26日 11:14
 **/
@Slf4j
public class ServerMessageHandler extends SimpleChannelInboundHandler<MessageProbuf.Message> {

    private ChanneGrouplManager channeGrouplManager;
    private ConnectionValidate connectionValidate;
    private TaskDistribution taskDistribution;
    private ServerContext serverContext;

    public ServerMessageHandler(ChanneGrouplManager channeGrouplManager, ConnectionValidate connectionValidate,
                                TaskDistribution taskDistribution, ServerContext serverContext) {
        this.channeGrouplManager = channeGrouplManager;
        this.connectionValidate = connectionValidate;
        this.taskDistribution = taskDistribution;
        this.serverContext = serverContext;
    }

    private MessageProbuf.Message message = MessageProbuf.Message.newBuilder()
            .setBody("normal connection")
            .setType(MessageProbuf.MessageType.HEALTH)
            .build();
    /**
     * 服务端监听到客户端活动
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        super.channelActive(ctx);
    }

    /**
     * 客户端与服务端断开连接的时候调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * 每当从服务端收到新的客户端连接时， 客户端的 Channel 存入ChannelGroup列表中，
     * ChannelHandler添加到实际上下文中准备处理事件
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //channeGrouplManager.addChannel(ctx.channel());
        super.handlerAdded(ctx);
    }

    /**
     * 每当从服务端收到客户端断开时，客户端的 Channel 移除 ChannelGroup 列表中，
     * 将ChannelHandler从实际上下文中删除，不再处理事件
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channeGrouplManager.removeChannel(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProbuf.Message msg) throws Exception {
        int typeValue = msg.getTypeValue();
        if (typeValue == MessageProbuf.MessageType.VALID_VALUE) {
            boolean validate = connectionValidate.validate(msg.getBody());
            if (!validate){
                log.info("valid fail");
                channeGrouplManager.removeChannel(ctx.channel());
                ctx.close();
            }
            log.info("valid successful");
            channeGrouplManager.addChannel(msg.getExtend(), ctx.channel());
        } else {
            Channel channel = channeGrouplManager.getGroup(msg.getExtend()).find(ctx.channel().id());
            if (channel ==null){
                ctx.close();
                return;
            }
            switch (typeValue) {
                case MessageProbuf.MessageType.CONNECTION_VALUE://初始化任务
                    int size = channeGrouplManager.getGroup(msg.getExtend()).size();
                    log.info("current client number 【{}】", size);
                    List<TaskDetail> list = JSON.parseArray(msg.getBody(), TaskDetail.class);
                    serverContext.addTask(msg.getExtend(), list);
                    /** 分配任务*/
                    ScheduleExecutor.getInstance().beginJob(list, channeGrouplManager);
                    //taskDistribution.distribution(msg.getExtend(), ctx.channel(), channeGrouplManager);
                    break;
                case MessageProbuf.MessageType.CLOSE_VALUE:
                    ctx.close();
                    break;
                case MessageProbuf.MessageType.HEALTH_VALUE:
                    log.info("client health packet");
                    ctx.channel().writeAndFlush(message);
                    break;
                default:
                    log.info("don't support this message type");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("happen error:", cause.fillInStackTrace());
    }
}
