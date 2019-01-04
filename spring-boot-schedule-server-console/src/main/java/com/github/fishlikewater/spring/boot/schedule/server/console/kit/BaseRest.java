package com.github.fishlikewater.spring.boot.schedule.server.console.kit;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName BaseRest
 * @Description
 * @date 2018年12月02日 12:08
 **/
public class BaseRest {

    /**
     * 返回success 信息
     *
     * @param obj
     * @return
     */
    protected <T> ResponseEntity<T> sendSuccess(T obj) {
        return new ResponseEntity<T>().setCode(CodeEnum.SUCCESS.getCode())
                .setMessage(CodeEnum.SUCCESS.getDesc())
                .setData(obj);
    }

    protected <T> ResponseEntity<T> sendSuccess(T obj, String message){
        return new ResponseEntity<T>().setCode(CodeEnum.SUCCESS.getCode())
                .setMessage(message)
                .setData(obj);
    }
    protected ResponseEntity sendSuccess(String message){
        return new ResponseEntity().setCode(CodeEnum.SUCCESS.getCode())
                .setMessage(message);
    }

    /**
     * 返回fail信息
     * @param obj
     * @return
     */
    protected <T> ResponseEntity<T> sendFail(T obj){
        return new ResponseEntity<T>().setCode(CodeEnum.FAIL.getCode())
                .setMessage(CodeEnum.FAIL.getDesc())
                .setData(obj);
    }
    protected ResponseEntity sendFail(String message){
        return new ResponseEntity().setCode(CodeEnum.FAIL.getCode())
                .setMessage(message);
    }

    protected <T> ResponseEntity<T> sendFail(T obj, String message){
        return new ResponseEntity<T>().setCode(CodeEnum.FAIL.getCode())
                .setMessage(message)
                .setData(obj);
    }
}
