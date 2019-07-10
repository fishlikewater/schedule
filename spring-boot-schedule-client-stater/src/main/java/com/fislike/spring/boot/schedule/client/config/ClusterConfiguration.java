package com.fislike.spring.boot.schedule.client.config;

import com.fishlikewater.schedule.client.boot.ClientStart;
import com.fishlikewater.schedule.common.context.ScheduleJobContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
public class ClusterConfiguration implements InitializingBean, DisposableBean {
    @Autowired
    private ScheduleClientProperties scheduleClientProperties;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void afterPropertiesSet() throws Exception {
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
        if(scheduleClientProperties.getServerHost() != null) scheduleJobContext.setHOST(scheduleClientProperties.getServerHost());
        if(scheduleClientProperties.getServerAddress() != null) scheduleJobContext.setAddress(scheduleClientProperties.getServerAddress());
        scheduleJobContext.setHealthBeat(scheduleClientProperties.getHealthBeat());
        scheduleJobContext.setRetryInterval(scheduleClientProperties.getRetryInterval());
        ClientStart.build().run();
    }

    @Override
    public void destroy() throws Exception {
        log.info("dstory evenloop thred of netty");
        ClientStart.build().stop();
    }
}
