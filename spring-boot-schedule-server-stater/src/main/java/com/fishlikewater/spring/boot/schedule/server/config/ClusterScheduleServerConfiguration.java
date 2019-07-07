package com.fishlikewater.spring.boot.schedule.server.config;

import com.fishlikewater.schedule.server.boot.ServerStart;
import com.fishlikewater.schedule.server.context.RedisServerContext;
import com.fishlikewater.schedule.server.executor.ClusterScheduleExecutor;
import com.fishlikewater.schedule.server.manage.redis.RedisConfig;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ClusterScheduleServerConfiguration
 * @Description
 * @date 2019年01月03日 10:36
 **/
@Slf4j
@Component
@EnableConfigurationProperties(ScheduleServerProperties.class)
public class ClusterScheduleServerConfiguration  implements InitializingBean, DisposableBean {

    @Autowired
    private ScheduleServerProperties scheduleServerProperties;

/*    @EventListener
    public void deployScheduleClient(ApplicationReadyEvent event){
        log.info("befin schedule server cluster config");
        String redisAddress = scheduleServerProperties.getRedisAddress();
        if(StringUtil.isNullOrEmpty(redisAddress)){
            log.error("not found redis config, please add redis config");
            return;
        }
        *//** 服务端参数设置*//*
        ServerStart serverStart = ServerStart.build();
        serverStart.setExecutor(new ClusterScheduleExecutor());
        serverStart.setServerContext(new RedisServerContext());
        *//** 初始化redis*//*
        RedisConfig.getInstance().initRedisClient(redisAddress);
        RedisConfig.getInstance().initRedisCommands();
        serverStart.run(scheduleServerProperties.getServerPort(), scheduleServerProperties.getServerAddress());
    }*/

    @Override
    public void destroy() throws Exception {
        ServerStart.build().getExecutor().toStop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("befin schedule server cluster config");
        String redisAddress = scheduleServerProperties.getRedisAddress();
        if(StringUtil.isNullOrEmpty(redisAddress)){
            log.error("not found redis config, please add redis config");
            return;
        }
        /** 服务端参数设置*/
        ServerStart serverStart = ServerStart.build();
        serverStart.setExecutor(new ClusterScheduleExecutor());
        serverStart.setServerContext(new RedisServerContext());
        /** 初始化redis*/
        RedisConfig.getInstance().initRedisClient(redisAddress);
        RedisConfig.getInstance().initRedisCommands();
        serverStart.run(scheduleServerProperties.getServerPort(), scheduleServerProperties.getServerAddress());
    }
}
