package com.fislike.spring.boot.schedule.client.config;

import com.fishlikewater.schedule.client.executor.ScheduleExecutor;
import com.fishlikewater.schedule.client.kit.ScheduleJobContext;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fislike.spring.boot.schedule.client.enums.MatchUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName SigleConfiguration
 * @Description
 * @date 2018年12月28日 11:19
 **/

@Configuration
@EnableConfigurationProperties(ScheduleClientProperties.class)
public class SigleConfiguration {
    @Autowired
    private ScheduleClientProperties scheduleClientProperties;

    @EventListener
    public void deployScheduleClient(ApplicationReadyEvent event){
        ScheduleJobContext.getInstance().setBasePath(scheduleClientProperties.getBasePackage());
        List<TaskDetail> allJobList = ScheduleJobContext.getInstance().getAllJobList();
        ScheduleJobContext.getInstance().updateCurrentJobList(allJobList);
        if(scheduleClientProperties.getMatchUnit() == MatchUnit.SECOND){
            ScheduleJobContext.getInstance().setSleepTime(1000l);
        }else{
            ScheduleJobContext.getInstance().setSleepTime(60*1000l);
        }
        ScheduleExecutor.getInstance().beginJob();
    }


}
