package com.sl.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ParamConvertUtil {

    public static void convertIdsString2List(Map<String, Object> map, String field){
        String idsStr = String.valueOf(map.get(field));
        if(StringUtils.isEmpty(idsStr)) return;
        String[] split = idsStr.split(",");
        List<String> ids = Arrays.asList(split);
        map.put(field, ids);
    }
}
