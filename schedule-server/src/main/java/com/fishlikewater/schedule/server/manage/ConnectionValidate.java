package com.fishlikewater.schedule.server.manage;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ConnectionValidate
 * @Description
 * @date 2018年11月25日 0:07
 **/
public interface ConnectionValidate {

    boolean validate(String token);
}
