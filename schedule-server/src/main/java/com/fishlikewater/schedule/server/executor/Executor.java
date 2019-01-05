package com.fishlikewater.schedule.server.executor;

import com.fishlikewater.schedule.server.context.ServerContext;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName Executor
 * @Description
 * @date 2019年01月02日 17:22
 **/
public interface Executor {

    void beginJob(ServerContext serverContext);

    /**
     * 线程异常处理
     * @param thread 线程
     */
    default void reStartThred(Thread thread){
        if (thread.isAlive()) {
            boolean interrupted = thread.isInterrupted();
            if (interrupted) {
                thread.start();
            }
        } else {
            thread.start();
        }
    }
}
