package com.fishlikewater.schedule.server.handler;

import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.server.boot.ServerStart;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ServerHandlerInitializer
 * @Description
 * @date 2018年12月25日 15:05
 **/
public class ServerHandlerInitializer extends ChannelInitializer<Channel> {

    private ServerStart serverStart;

    public ServerHandlerInitializer(ServerStart serverStart) {
        this.serverStart = serverStart;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(MessageProbuf.Message.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new ServerMessageHandler(serverStart.getChanneGrouplManager(),
                        serverStart.getConnectionValidate(),
                        serverStart.getTaskDistribution(),
                        serverStart.getServerContext()));

    }
}
