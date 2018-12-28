package com.fishlikewater.schedule.client.kit;

import com.fishlikewater.schedule.client.boot.ClientStart;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import io.netty.channel.Channel;
import io.netty.util.concurrent.FutureListener;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ChannelKit
 * @Description
 * @date 2018年12月26日 15:18
 **/
public class ChannelKit {

    /**
     * 向服务端发送消息
     * @param message
     * @param listener
     */
    public static void sendMessage(MessageProbuf.Message message, FutureListener listener){
        Channel channel = ClientStart.build().getChannel();
        if(listener != null){
            channel.writeAndFlush(message).addListener(listener);
        }else{
            channel.writeAndFlush(message);
        }
    }

}
