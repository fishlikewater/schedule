package com.fishlikewater.spring.boot.schedule.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleServerProperties
 * @Description
 * @date 2018年12月28日 14:28
 **/
@ConfigurationProperties("schedule.server")
@Data
public class ScheduleServerProperties {

    private int serverPort;

    private String serverAddress;
}
