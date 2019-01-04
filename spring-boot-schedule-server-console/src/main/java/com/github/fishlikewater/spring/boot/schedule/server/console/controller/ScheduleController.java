package com.github.fishlikewater.spring.boot.schedule.server.console.controller;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.boot.ServerStart;
import com.fishlikewater.schedule.server.manage.record.RecordManage;
import com.github.fishlikewater.spring.boot.schedule.server.console.controller.body.ScheduleStatusBody;
import com.github.fishlikewater.spring.boot.schedule.server.console.kit.BaseRest;
import com.github.fishlikewater.spring.boot.schedule.server.console.kit.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleController
 * @Description
 * @date 2019年01月04日 15:54
 **/

@Slf4j
@RestController
@RequestMapping("/schedule")
public class ScheduleController extends BaseRest {

    /**
     * 获取所有的任务
     * @return
     */
    @GetMapping("all")
    public ResponseEntity getAllJob(){
        List<TaskDetail> taskList = ServerStart.build().getServerContext().getTaskList();
        return sendSuccess(taskList);
    }

    /**
     * 查看执行记录
     * @param page
     * @param size
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("record")
    public ResponseEntity getRecord(@RequestParam(value = "page", defaultValue = "1")int page,
                                    @RequestParam(value = "size", defaultValue = "20")int size) throws ExecutionException, InterruptedException {

        int begin = (page-1)*size;
        int end = page*size;
        List<TaskDetail> cacheData = RecordManage.getInstance().getCacheData(begin, end);
        return sendSuccess(cacheData);
    }

    /**
     * 更新状态
     * @param scheduleStatusBody
     * @return
     */
    @PostMapping("status")
    public ResponseEntity updataStatus(@Validated @RequestBody ScheduleStatusBody scheduleStatusBody){
        boolean updateIsUse = ServerStart.build().getServerContext().updateIsUse(scheduleStatusBody.getAppName(),
                scheduleStatusBody.getNum(), scheduleStatusBody.isUse());
        return sendSuccess(updateIsUse);
    }

}
