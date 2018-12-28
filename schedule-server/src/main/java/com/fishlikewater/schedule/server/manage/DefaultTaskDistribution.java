package com.fishlikewater.schedule.server.manage;

import com.alibaba.fastjson.JSON;
import com.fishlikewater.schedule.common.entity.MessageProbuf;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.context.ServerContext;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName DefaultTaskDistribution
 * @Description 默认任务 分配规则
 * @date 2018年12月27日 21:50
 **/
public class DefaultTaskDistribution implements TaskDistribution {
    @Override
    public List<TaskDetail> distribution(String from, Channel channel, ChanneGrouplManager channeGrouplManager) {
        ChannelGroup group = channeGrouplManager.getGroup(from);
        Channel[] channels = group.toArray(new Channel[0]);
        int size = group.size();
        List<TaskDetail> taskList = ServerContext.getTaskList(from);
        /** 按取模值将任务分配到实例中*/
        for (TaskDetail taskDetail : taskList) {
            int serialNumber = taskDetail.getSerialNumber();
            int moldTaking = serialNumber % size;
            Channel channel1;
            if(moldTaking != 0){
                channel1 = channels[moldTaking - 1];
            }else{
                channel1 = channels[size - 1];
            }
            taskDetail.setActionAdress(channel1.remoteAddress().toString());
        }
        /** 将分配的结果发送至客户端*/
        List<TaskDetail> sendList = new ArrayList<>();
        for (Channel c : group) {
            sendList.clear();
            for (TaskDetail taskDetail : taskList) {
                if(c.remoteAddress().toString().equals(taskDetail.getActionAdress())){
                    sendList.add(taskDetail);
                }
            }
            MessageProbuf.Message sendMessage = MessageProbuf.Message.newBuilder()
                    .setBody(JSON.toJSONString(sendList))
                    .setType(MessageProbuf.MessageType.CONNECTION)
                    .build();
            c.writeAndFlush(sendMessage);
        }
        return taskList;
    }
}
