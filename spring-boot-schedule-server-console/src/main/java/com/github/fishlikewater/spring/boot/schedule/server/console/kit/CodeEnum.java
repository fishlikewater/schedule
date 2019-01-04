package com.github.fishlikewater.spring.boot.schedule.server.console.kit;

import lombok.Getter;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName CodeEnum
 * @Description
 * @date 2018年12月02日 12:16
 **/
public enum CodeEnum {

    SUCCESS(200, "success"),
    FAIL(500, "fail"),
    FORBIDDEN(403,"Your current user does not have access rights"),
    PARAM_ERROR(203, "prams valid error"),
    UNAUTHORIZED(401, "you don't have permisson"),
    NOTFOUND(404, "don't have find this url mapping");

    @Getter
    private int code;

    @Getter
    private String desc;

    CodeEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
