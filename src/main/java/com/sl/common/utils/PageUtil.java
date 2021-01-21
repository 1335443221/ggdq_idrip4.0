package com.sl.common.utils;

import java.util.Map;

public class PageUtil {

    //设置分页信息
    public static void setPageInfo(Map<String, Object> map) {
        int fromNum=0;
        int pageSize=20;
        if(map.get("pageNum")!=null&&map.get("pageSize")!=null){
            fromNum=(Integer.parseInt(map.get("pageNum").toString())-1)*Integer.parseInt(map.get("pageSize").toString());
            pageSize=Integer.parseInt(map.get("pageSize").toString());
        }
        //设置请求参数
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
    }


    //判断是否最后一页
    public static boolean setLastPage(Map<String, Object> map,int total) {
        int pageSize=Integer.parseInt(map.get("pageSize").toString());
        int fromNum=Integer.parseInt(map.get("fromNum").toString());
        if ((fromNum + pageSize) >= total) {
            return true;
        } else {
            return false;
        }
    }


}
