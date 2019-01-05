package com.fishlikewater.spring.boot.schedule.server.config;

import com.fishlikewater.schedule.server.boot.ServerStart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleServerConfiguration
 * @Description
 * @date 2018年12月28日 14:27
 **/
@Configuration
@EnableConfigurationProperties(ScheduleServerProperties.class)
public class ScheduleServerConfiguration {

    @Autowired
    private ScheduleServerProperties scheduleServerProperties;

/*
    @Bean
    @ConditionalOnProperty(prefix = "schedule.server",value = "enabled",havingValue = "true")
    public ScheduleEndPoint joinEndpoint(){
        return new ScheduleEndPoint();
    }
*/

    @EventListener
    public void deployScheduleClient(ApplicationReadyEvent event){
        ServerStart.build().run(scheduleServerProperties.getServerPort(), scheduleServerProperties.getServerAddress());
    }
}
