package com.sl.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultHandleUtil {

    //为结果集添加序号
    public static void addOrderNumber(ArrayList<HashMap<String, Object>> result, Map<String, Object> map){
        int startNum = 0;
        Object pageSizeObj = map.get("pageSize");
        Object pageNumObj = map.get("pageNum");
        if(pageSizeObj != null && pageNumObj != null){
            int pageNum = Integer.parseInt(String.valueOf(pageNumObj));
            int pageSize = Integer.parseInt(String.valueOf(pageSizeObj));
            startNum += (pageNum - 1)*pageSize;
        }
        if(result == null || result.size() == 0) return;
        for(int i=0;i<result.size();i++){
            result.get(i).put("row_no", i+1+startNum);
        }
    }
}
