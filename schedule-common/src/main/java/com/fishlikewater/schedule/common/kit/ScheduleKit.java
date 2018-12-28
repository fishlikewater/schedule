package com.fishlikewater.schedule.common.kit;

import io.netty.channel.epoll.Epoll;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleKit
 * @Description
 * @date 2018年12月25日 14:19
 **/
public class ScheduleKit {

    //private static final int cpuNum = Runtime.getRuntime().availableProcessors();
    //public static final ExecutorService executor = new ThreadPoolExecutor(cpuNum*4, cpuNum*10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000));//CPU核数4-10倍

    /**
     * 判断当前系统是否支持epoll
     * @return
     */
    public static boolean epollIsAvailable(){
        boolean available = Epoll.isAvailable();
        boolean linux = System.getProperty("os.name").toLowerCase().contains("linux");
        return available && linux;
    }
}
