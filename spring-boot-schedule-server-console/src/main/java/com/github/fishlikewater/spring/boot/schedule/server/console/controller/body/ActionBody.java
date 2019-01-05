package com.github.fishlikewater.spring.boot.schedule.server.console.controller.body;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleStatusBody
 * @Description
 * @date 2019年01月04日 16:41
 **/
@Data
public class ActionBody {

    @NotNull
    private String appName;

    @Min(1)
    private int num;

    @NotNull
    private String actionAddress;
}
