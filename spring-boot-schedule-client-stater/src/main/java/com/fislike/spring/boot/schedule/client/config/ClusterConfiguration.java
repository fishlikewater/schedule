package com.fislike.spring.boot.schedule.client.config;

import com.fishlikewater.schedule.client.boot.ClientStart;
import com.fishlikewater.schedule.client.kit.ScheduleJobContext;
import com.fislike.spring.boot.schedule.client.enums.MatchUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ClusterConfiguration
 * @Description
 * @date 2018年12月28日 11:30
 **/
@Configuration
@EnableConfigurationProperties(ScheduleClientProperties.class)
@Slf4j
public class ClusterConfiguration {
    @Autowired
    private ScheduleClientProperties scheduleClientProperties;

    @Value("${spring.application.name}")
    private String appName;

    @EventListener
    public void deployScheduleClient(ApplicationReadyEvent event){
        ScheduleJobContext scheduleJobContext = ScheduleJobContext.getInstance();
        if (appName == null){
            log.warn("not config application name, this cluster schedule not running");
            return;
        }
        scheduleJobContext.setAppName(appName);
        if(scheduleClientProperties.getBasePackage() != null){
            log.info("task base package 【{}】", scheduleClientProperties.getBasePackage());
            scheduleJobContext.setBasePath(scheduleClientProperties.getBasePackage());
        }
        if(scheduleClientProperties.getServerPort() != 0) scheduleJobContext.setPORT(scheduleClientProperties.getServerPort());
        if(scheduleClientProperties.getServerAddress() != null) scheduleJobContext.setHOST(scheduleClientProperties.getServerAddress());
        if(scheduleClientProperties.getMatchUnit() == MatchUnit.SECOND){
            scheduleJobContext.setSleepTime(1000l);
        }else{
            scheduleJobContext.setSleepTime(60*1000l);
        }
        scheduleJobContext.setHealthBeat(scheduleClientProperties.getHealthBeat());
        scheduleJobContext.setRetryInterval(scheduleClientProperties.getRetryInterval());
        ClientStart.build().run();
    }
}
