package com.github.fishlikewater.spring.boot.schedule.server.console.kit;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ResponseEntity
 * @Description
 * @date 2018年12月02日 12:11
 **/
@Data
@Accessors(chain = true)
public class ResponseEntity<T> {

    private int code;

    private T data;

    private String message;

    public ResponseEntity(CodeEnum codeEnum, T data, String message){
        this.code = codeEnum.getCode();
        this.data = data;
        this.message = message;
    }

    public ResponseEntity() {
    }
}
