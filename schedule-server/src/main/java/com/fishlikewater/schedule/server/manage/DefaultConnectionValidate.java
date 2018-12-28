package com.fishlikewater.schedule.server.manage;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName DefaultConnectionValidate
 * @Description
 * @date 2018年11月25日 11:34
 **/
public class DefaultConnectionValidate implements ConnectionValidate {
    @Override
    public boolean validate(String token) {

        return true;
    }
}
