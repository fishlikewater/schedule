package com.fislike.spring.boot.schedule.client.annotion;

import com.fislike.spring.boot.schedule.client.config.SigleConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName EnableSigleSchedule
 * @Description
 * @date 2018年12月28日 11:49
 **/
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(SigleConfiguration.class)
@Documented
@Configuration
public @interface EnableSigleSchedule {
}
