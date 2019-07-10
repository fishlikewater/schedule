package com.fishlikewater.spring.boot.schedule.server.config;

import com.fishlikewater.schedule.server.boot.ServerStart;
import com.fishlikewater.schedule.server.manage.sqlite.Sql2oConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleServerConfiguration
 * @Description
 * @date 2018年12月28日 14:27
 **/
@Component
@EnableConfigurationProperties(ScheduleServerProperties.class)
public class ScheduleServerConfiguration implements InitializingBean, DisposableBean {

    @Autowired
    private ScheduleServerProperties scheduleServerProperties;

/*
    @Bean
    @ConditionalOnProperty(prefix = "schedule.server",value = "enabled",havingValue = "true")
    public ScheduleEndPoint joinEndpoint(){
        return new ScheduleEndPoint();
    }
*/

/*    @EventListener
    public void deployScheduleClient(ApplicationReadyEvent event){

    }*/

    @Override
    public void destroy() throws Exception {
        ServerStart.build().stop();
        ServerStart.build().getExecutor().toStop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Sql2oConfig.open("jdbc:sqlite:schedule-server.db", null, null);
        ServerStart.build().run(scheduleServerProperties.getServerPort(), scheduleServerProperties.getServerAddress());
    }
}
