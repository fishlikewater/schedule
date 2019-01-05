package com.fishlikewater.schedule.common.kit;

import com.alibaba.fastjson.serializer.PropertyPreFilter;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName JsonFilter
 * @Description
 * @date 2019年01月05日 21:08
 **/
public class JsonFilter {

    private static List sendClient = Arrays.asList("serialNumber");

    private static List sendServerResult = Arrays.asList("appName", "corn", "desc", "executorResult", "executorTime", "serialNumber", "actionAdress");

    private static List view = Arrays.asList("appName", "corn", "desc", "nextTime", "isUse", "serialNumber", "actionAdress");

    public static PropertyPreFilter sendClientFilter = (serializer, object, name) -> {
        if (sendClient.contains(name)) return true;
        return false;
    };

    public static PropertyPreFilter sendServerResultFilter = (serializer, object, name) -> {
        if (sendServerResult.contains(name)) return true;
        return false;
    };

    public static PropertyPreFilter viewFilter = (serializer, object, name) -> {
        if (view.contains(name)) return true;
        return false;
    };
}
