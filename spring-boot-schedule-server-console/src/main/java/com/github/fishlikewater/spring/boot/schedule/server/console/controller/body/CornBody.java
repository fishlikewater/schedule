package com.github.fishlikewater.spring.boot.schedule.server.console.controller.body;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName CornBody
 * @Description
 * @Date 2019年02月18日 17:04
 * @since
 **/
@Data
public class CornBody {


    @NotNull
    private String appName;

    @Min(1)
    private int num;

    @NotNull
    private String corn;
}
