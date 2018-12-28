package com.fishlikewater.schedule.server.manage;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName TaskDistribution
 * @Description
 * @date 2018年12月27日 21:47
 **/
public interface TaskDistribution {

    List<TaskDetail>  distribution(String from, Channel channel, ChanneGrouplManager channeGrouplManager);

 /*   default List<TaskDetail> getDistributionTask(String from, Channel channel, ChanneGrouplManager channeGrouplManager){
        ChannelGroup group = channeGrouplManager.getGroup(from);

    }*/
}
