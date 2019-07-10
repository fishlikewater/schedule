package com.fishlikewater.schedule.common.context;

import com.fishlikewater.schedule.common.annotion.Schedule;
import com.fishlikewater.schedule.common.ScheduleJob;
import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.common.kit.CronSequenceGenerator;
import com.fishlikewater.schedule.common.kit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScannerScheduleKit
 * @Description
 * @date 2018年12月25日 15:18
 **/
@Slf4j
public class ScannerScheduleKit {

    /**
     * 扫描需加载任务包
     * @param basePackage
     */
    public static List<Class<?>> scannerSchedulePackage(String... basePackage){
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().forPackages(basePackage);//扫描基础包
        configurationBuilder.addScanners(new TypeElementsScanner());
        configurationBuilder.addScanners(new TypeAnnotationsScanner());//添加类注解扫描
        Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<?>> scheduleSet = reflections.getTypesAnnotatedWith(Schedule.class);//获取到的任务类集合
        List<Class<?>> collect = scheduleSet.stream().filter(t -> {
            if (t.getInterfaces()[0].getName().equals("com.fishlikewater.schedule.common.ScheduleJob")) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取任务类实例
     * @param basePackage
     * @return
     */
    public static List<TaskDetail> loadJobClass(String... basePackage){
        List<TaskDetail> list = new ArrayList<>();
        List<Class<?>> classes = scannerSchedulePackage(basePackage);
        classes.sort((o1, o2)->{

            return o1.getName().compareTo(o2.getName());
        });
        int num = 0;
        for (Class<?> aClass : classes) {
            try {
                Schedule aClassAnnotation = aClass.getAnnotation(Schedule.class);
                String value = aClassAnnotation.value();
                String desc = aClassAnnotation.desc();
                if(StringUtils.isEmpty(desc)){
                    desc = aClass.getName();
                }
                ScheduleJob scheduleJob = (ScheduleJob)aClass.newInstance();
                list.add(new TaskDetail()
                        .setSerialNumber(++num)
                        .setAppName(ScheduleJobContext.getInstance().getAppName())
                        .setCorn(value)
                        .setCronSequenceGenerator(new CronSequenceGenerator(value))
                        .setDesc(desc)
                        .setClassName(aClass.getSimpleName())
                        .setScheduleJob(scheduleJob));
            } catch (InstantiationException e) {
                log.error("get ScheduleJob entity error", e);
            } catch (IllegalAccessException e) {
                log.error("get ScheduleJob entity error", e);
            }
        }
        return list;
    }
}
