package com.fishlikewater.schedule.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName Message
 * @Description
 * @date 2018年12月25日 17:49
 **/
@Data
@Accessors(chain = true)
public class Message implements Serializable {

    private int length;

    private String body;

    private int messageType;


}
