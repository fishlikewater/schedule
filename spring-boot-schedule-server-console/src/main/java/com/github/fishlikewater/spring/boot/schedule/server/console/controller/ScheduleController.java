package com.github.fishlikewater.spring.boot.schedule.server.console.controller;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import com.fishlikewater.schedule.server.boot.ServerStart;
import com.fishlikewater.schedule.server.context.ServerContext;
import com.fishlikewater.schedule.server.manage.record.RecordManage;
import com.github.fishlikewater.spring.boot.schedule.server.console.controller.body.ActionBody;
import com.github.fishlikewater.spring.boot.schedule.server.console.controller.body.ScheduleStatusBody;
import com.github.fishlikewater.spring.boot.schedule.server.console.kit.BaseRest;
import com.github.fishlikewater.spring.boot.schedule.server.console.kit.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
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

    @GetMapping("recode_page")
    public ModelAndView recordPage(ModelAndView mv){
        mv.setViewName("record");
        return mv;

    }

    /**
     * 获取所有的任务
     * @return
     */
    @GetMapping("all")
    public ResponseEntity getAllJob(){
        List<TaskDetail> taskList = ServerStart.build().getServerContext().getTaskList();
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("count", taskList.size());
        returnMap.put("item", taskList);
        return sendSuccess(returnMap);
    }

    /**
     * 查看执行记录
     * @param page
     * @param limit
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("record")
    public ResponseEntity getRecord(@RequestParam(value = "page", defaultValue = "1")int page,
                                    @RequestParam(value = "limit", defaultValue = "20")int limit) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        int begin = (page-1)*limit;
        int end = page*limit-1;
        List<TaskDetail> cacheData = RecordManage.getInstance().getCacheData(begin, end);
        long count = RecordManage.getInstance().getRecordCount();
        returnMap.put("count", count);
        returnMap.put("item", cacheData);
        return sendSuccess(returnMap);
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

    /**
     * 指定任务执行的远程服务器
     * @param actionBody
     * @return
     */
    @PostMapping("action")
    public ResponseEntity updateActionAddress(@Validated @RequestBody ActionBody actionBody){
        ServerContext serverContext = ServerStart.build().getServerContext();
        boolean isSuccess = serverContext.updateActionAddress(actionBody.getAppName(), actionBody.getNum(), actionBody.getActionAddress());
        if(isSuccess){
            return sendSuccess("执行成功");
        }else {
            return sendSuccess("执行失败");
        }
    }

    /**
     * 获取所有的远程可执行服务器
     * @param appName
     * @return
     */
    @GetMapping("client/{appName}")
    public ResponseEntity getAllRemoteClient(@PathVariable String appName){
        List returnList = new ArrayList();
        ServerContext serverContext = ServerStart.build().getServerContext();
        Set<String> allClient = serverContext.getAllClient(appName);
        for (String s : allClient) {
            Map<String, String> map = new HashMap<>();
            String[] split = s.split(":");
            String host = split[0].replaceAll("//", "");
            map.put("appName", appName);
            map.put("host", host);
            map.put("port", split[1]);
            returnList.add(map);
        }
        return sendSuccess(returnList);
    }

}
