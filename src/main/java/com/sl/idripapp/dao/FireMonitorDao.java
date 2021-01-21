package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电气火灾监控
 */
@Mapper
public interface FireMonitorDao {
    //==============APP======================//
    ArrayList<Map<String,Object>> getFactory(Map<String, Object> map);
    ArrayList<Map<String,Object>> getBuildingByFactoryId(Map<String, Object> map);
    ArrayList<Map<String,Object>> getFloorByBuildingId(Map<String, Object> map);
    ArrayList<Map<String,Object>> getDetector(Map<String, Object> map);
    ArrayList<Map<String,Object>> detectorList(Map<String, Object> map);
    ArrayList<Map<String,Object>> getTransformerroom(Map<String, Object> map);
    ArrayList<Map<String,Object>> getThreadByTransformerroom(Map<String, Object> map);
    Map<String,Object> getDetectorData(Map<String, Object> map);
    ArrayList<Map<String,Object>> getFactoryPosition(Map<String, Object> map);
    ArrayList<Map<String,Object>> getBuildingPosition(Map<String, Object> map);
    Map<String,Object> getProjectInfo(Map<String, Object> map);
    ArrayList<Map<String,Object>> getAppFireTag(Map<String, Object> map);

    ArrayList<Map<String,Object>> getAlermByDeviceId(Map<String, Object> map);
    ArrayList<Map<String,Object>> fireAlermList(Map<String, Object> map);
    Map<String,Object> fireAlermDetail(Map<String, Object> map);
    int fireAlermListCount(Map<String, Object> map);
    Map<String,Object> queryAlermDealDetail(Map<String, Object> map);
    int insertAlermDeal(Map<String, Object> map);
    int updateAlermDeal(Map<String, Object> map);
    ArrayList<Map<String, Object>> getFireTagByDetectorId(Map<String, Object> map);
    ArrayList<Map<String, Object>> fireAlConfData(Map<String, Object> map);
    int fireAlConfUpdate(Map<String, Object> map);
    int fireAlConfSave(Map<String, Object> map);
    ArrayList<Map<String, Object>> selectIsExistConfig(Map<String, Object> map);

    ArrayList<Map<String, Object>> patrolList(Map<String, Object> map);
    ArrayList<Map<String, Object>> allPatrolList(Map<String, Object> map);
    Map<String, Object> patrolDetail(Map<String, Object> map);
    int patrolListCount(Map<String, Object> map);
    ArrayList<Map<String, Object>> malfunctionList(Map<String, Object> map);
    Map<String, Object> malfunctionDetail(Map<String, Object> map);
    int malfunctionListCount(Map<String, Object> map);
    int addPatrol(Map<String, Object> map);
    int addAllPatrolLog(List<Map> list);
    int patrolComplete(Map<String, Object> map);
    int addMalfunction(Map<String, Object> map);
    int malfunctionDeal(Map<String, Object> map);

    String getModelUrl(Map<String, Object> map);
}
