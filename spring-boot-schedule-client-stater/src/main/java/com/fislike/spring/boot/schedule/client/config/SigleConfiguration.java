package com.fislike.spring.boot.schedule.client.config;

import com.fishlikewater.schedule.client.executor.ScheduleExecutor;
import com.fishlikewater.schedule.common.context.ScheduleJobContext;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName SigleConfiguration
 * @Description
 * @date 2018年12月28日 11:19
 **/
@Component
@Slf4j
@EnableConfigurationProperties(ScheduleClientProperties.class)
public class SigleConfiguration implements InitializingBean, DisposableBean {
    @Autowired
    private ScheduleClientProperties scheduleClientProperties;

 /*   @EventListener
    public void deployScheduleClient(ApplicationReadyEvent event){
        ScheduleJobContext.getInstance().setBasePath(scheduleClientProperties.getBasePackage());
        List<TaskDetail> allJobList = ScheduleJobContext.getInstance().getAllJobList();
        ScheduleJobContext.getInstance().updateCurrentJobList(allJobList);
        ScheduleExecutor.getInstance().clientExcutor();
    }*/


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("加载调度客户端");
        ScheduleJobContext.getInstance().setBasePath(scheduleClientProperties.getBasePackage());
        List<TaskDetail> allJobList = ScheduleJobContext.getInstance().getAllJobList();
        ScheduleJobContext.getInstance().updateCurrentJobList(allJobList);
        ScheduleExecutor.getInstance().clientExcutor();
    }

    @Override
    public void destroy() throws Exception {
        log.info("销毁调度客户端");
        ScheduleExecutor.getInstance().toStop();
    }
}
