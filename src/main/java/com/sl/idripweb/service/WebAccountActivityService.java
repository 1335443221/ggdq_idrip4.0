package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountActivityService {

    public WebResult getType(Map<String, Object> map);
    public WebResult addType(Map<String, Object> map);
    public WebResult updateType(Map<String, Object> map);
    public WebResult deleteType(Map<String, Object> map);


    public WebResult addActivity(Map<String, Object> map);
    public WebResult getActivityList(Map<String, Object> map);
    public WebResult updateActivity(Map<String, Object> map);
    public WebResult deleteActivity(Map<String, Object> map);


    public WebResult getRecycleBin(Map<String, Object> map);
    public WebResult recovery(Map<String, Object> map);
    public WebResult deleteCompletely(Map<String, Object> map);

    public WebResult addRecord(Map<String, Object> map);

    public WebResult statistics(Map<String, Object> map);
    public WebResult recordStatistics(Map<String, Object> map);

}
