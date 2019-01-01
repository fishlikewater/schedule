package com.fishlikewater.schedule.common.kit;

import com.fishlikewater.schedule.common.entity.TaskDetail;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName TaskQueue
 * @Description
 * @date 2018年12月27日 10:18
 **/
public class TaskQueue {

    private static PriorityBlockingQueue<TaskDetail> queue = new PriorityBlockingQueue(20, new Comparator<TaskDetail>() {
        @Override
        public int compare(TaskDetail t1, TaskDetail t2) {
            if(t1.getNextTime() >= t2.getNextTime()){
                return 1;
            }else{
                return -1;
            }
        }
    });

    public static void add(TaskDetail taskDetail){
        queue.add(taskDetail);
    }

    public static TaskDetail poll(){
        return queue.poll();
    }

    public static TaskDetail peek(){
        return queue.peek();
    }

    public static TaskDetail remove(){
        return queue.remove();
    }

    public static PriorityBlockingQueue getQueue(){
        return queue;
    }

}
