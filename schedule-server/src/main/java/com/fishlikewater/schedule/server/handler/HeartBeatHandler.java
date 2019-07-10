package com.fishlikewater.schedule.server.handler;

import com.fishlikewater.schedule.common.context.ScheduleJobContext;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 用于检测channel的心跳handler
 * 继承ChannelInboundHandlerAdapter，从而不需要实现channelRead0方法
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    public static final MessageProbuf.Message HEARTBEAT_SEQUENCE = MessageProbuf.Message.newBuilder()
            .setLength(10)
            .setBody("ping")
            .setType(MessageProbuf.MessageType.HEALTH)
            .setExtend(ScheduleJobContext.getInstance().getAppName())
            .build();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // 判断evt是否是IdleStateEvent（用于触发用户事件，包含 读空闲/写空闲/读写空闲 ）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;        // 强制类型转换
            ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE)
                    .addListener(new ChannelFutureListener(){
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if(!channelFuture.isSuccess()){
                                ctx.channel().close();
                            }
                        }
                    });//(ChannelFutureListener.CLOSE_ON_FAILURE);
           /* if (event.state() == IdleState.READER_IDLE) {
                System.out.println("进入读空闲...");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("进入写空闲...");
            } else if (event.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                // 关闭无用的channel，以防资源浪费
                channel.close();
            }*/
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

}
