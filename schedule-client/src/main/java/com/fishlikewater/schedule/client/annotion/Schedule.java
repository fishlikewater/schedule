package com.fishlikewater.schedule.client.annotion;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName Schedule
 * @Description 任务调度逻辑实现类
 * @date 2018年12月24日 20:43
 **/
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Schedule {

    String value() default "";

    String desc() default "";
}
