package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface ElcmTaskService {

    public WebResult getTaskStatus(Map<String, Object> map);
    public WebResult getTaskType(Map<String, Object> map);
    public WebResult addTask(Map<String, Object> map);
    public WebResult getTaskList(Map<String, Object> map);
    public WebResult revokeTask(Map<String, Object> map);
    public WebResult updateTask(Map<String, Object> map);
    public WebResult deleteTask(Map<String, Object> map);
    public WebResult getTaskDetail(Map<String, Object> map);
    public WebResult startOrStop(Map<String, Object> map);
    public WebResult getDeviceMaintainRelation(Map<String, Object> map);



    public WebResult getRecordStatus(Map<String, Object> map);
    public WebResult getRecordList(Map<String, Object> map);
    public WebResult assignUser(Map<String, Object> map);
    public WebResult assignRemindTime(Map<String, Object> map);
    public WebResult updateStartTime(Map<String, Object> map);
    public WebResult getRecordDetail(Map<String, Object> map);
    public WebResult getRecordByIds(Map<String, Object> map);

    public WebResult getMyRecordList(Map<String, Object> map);
    public WebResult receiveRecord(Map<String, Object> map);
    public WebResult chargeback(Map<String, Object> map);
    public WebResult transfer(Map<String, Object> map);
    public WebResult complete(Map<String, Object> map);
}
