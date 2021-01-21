package com.sl.idripweb.dao;

import com.sl.idripapp.entity.AccountActivityTypeTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountActivityDao {

    List<AccountActivityTypeTree>  getType(Map<String, Object> map);
    Integer  addType(Map<String, Object> map);
    Integer  updateType(Map<String, Object> map);
    Integer  deleteActivityByType(List<Integer> deleteType);
    Integer  deleteType(List<Integer> deleteType);

    Integer  addActivity(Map<String, Object> map);
    ArrayList<HashMap<String, Object>>  getActivityList(Map<String, Object> map);
    Integer  getActivityListCount(Map<String, Object> map);
    Integer  updateActivity(Map<String, Object> map);
    Integer  deleteActivity(List<String> list);


    ArrayList<HashMap<String, Object>>  getRecycleBin(Map<String, Object> map);
    Integer  getRecycleBinCount(Map<String, Object> map);
    Integer  recovery(Map<String, Object> map);
    Integer  deleteCompletely(List<String> list);

    Integer  addRecord(Map<String, Object> map);
    Map<String, Object>  getRecord(Map<String, Object> map);
    Integer  insertRecord(Map<String, Object> map);
    Integer  updateRecord(Map<String, Object> map);
    List<Map<String, Object>>  getActivityTimeStatistics(Map<String, Object> map);
    List<Map<String, Object>>  getActivityAddStatistics(Map<String, Object> map);
    List<Map<String, Object>>  getActivityDeleteStatistics(Map<String, Object> map);

    List<Map<String, Object>>  getSeeStatistics(Map<String, Object> map);
    List<Map<String, Object>>  getDownloadStatistics(Map<String, Object> map);



}
