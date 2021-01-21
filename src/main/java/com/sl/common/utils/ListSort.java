package com.sl.common.utils;

import com.sl.idripapp.entity.TaskPark;

import java.text.SimpleDateFormat;
import java.util.*;

public class ListSort {

	   //根据时间排序  list中含有date
	   public static void listSortByDate(List<Map<String,Object>> list) {
        Collections.sort(list, new Comparator<Map>() {  
		            @Override  
		            public int compare(Map o1, Map o2) {  
		                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
		                try {  
		                    Date dt1 = format.parse(o1.get("date").toString());  
		                    Date dt2 = format.parse(o2.get("date").toString());  
		                    if (dt1.getTime() > dt2.getTime()) {  
		                        return 1;  
		                    } else if (dt1.getTime() < dt2.getTime()) {  
		                        return -1;  
		                    } else {  
		                        return 0;  
		                    }  
		                } catch (Exception e) {  
		                    e.printStackTrace();  
		                }  
		                return 0;  
		            }  
		        });  
		    }


	   //根据状态排序  list中含有Status
	   public static void parkTaskSortByStatus(List<TaskPark> list) {
        Collections.sort(list, new Comparator<TaskPark>() {
		            @Override
		            public int compare(TaskPark o1, TaskPark o2) {
		                try {
							int s1 = o1.getStatus();
							int s2 = o2.getStatus();
		                    if (s1 > s2) {
		                        return 1;
		                    } else if (s1 < s2) {
		                        return -1;
		                    } else {
		                        return 0;
		                    }
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		                return 0;
		            }
		        });
		    }

	   
}
