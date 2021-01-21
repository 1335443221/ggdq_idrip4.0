package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ElcmTaskDao {

    ArrayList<Map<String, Object>> getTaskStatus(Map<String, Object> map);
    ArrayList<Map<String, Object>> getTaskType(Map<String, Object> map);
    int addTask(Map<String, Object> map);
    int addTaskRelation(Map<String, Object> map);
    int deleteTaskRelation(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getTaskList(Map<String, Object> map);
    int getTaskListCount(Map<String, Object> map);
    Map<String, Object> getTask(Map<String, Object> map);
    List<Map<String, Object>> getTaskRelation(Map<String, Object> map);
    int updateTask(Map<String, Object> map);
    int deleteTask(Map<String, Object> map);
    List<Map<String, Object>>  getDeviceMaintainRelation(Map<String, Object> map);
    List<Map<String, Object>>  getElcmTaskRelation(Map<String, Object> map);



    List<Map<String, Object>>  getRecordStatus(Map<String, Object> map);
    List<Map<String, Object>>  getRemindList(Map<String, Object> map);
    int  addRemind(Map<String, Object> map);
    ArrayList<HashMap<String, Object>>   getRecordList(Map<String, Object> map);
    int  getRecordListCount(Map<String, Object> map);
    int  updateRecord(Map<String, Object> map);
    int  assignTime(Map<String, Object> map);
    ArrayList<String>  getStatusByIds(Map<String, Object> map);
    Map<String, Object>  getRecordDetail(Map<String, Object> map);

    ArrayList<HashMap<String, Object>>  getMyRecordList(Map<String, Object> map);
    int  getMyRecordListCount(Map<String, Object> map);
    int  chargeback(Map<String, Object> map);
}
