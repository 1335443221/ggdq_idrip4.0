package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: AirLightDao
 * Description: 空调照明APP
 */

@Mapper
public interface AirLightDao {
    //============================APP 空调控制=============================//
    int addAirTimingSetting(Map<String, Object> map);
    int addAirIntelligentControl(Map<String, Object> map);
    int addAirRelation(Map<String, Object> map);
    String getAirSwitchTagById(String id);


    int updateAirTimingSetting(Map<String, Object> map);
    int updateAirIntelligentControl(Map<String, Object> map);
    int deleteAirTimingSetting(Map<String, Object> map);
    int deleteAirIntelligentControl(Map<String, Object> map);


    ArrayList<Map<String, Object>> getAirTimingSetting(Map<String, Object> map);

    ArrayList<Map<String, Object>> getAirOperationRecord(Map<String, Object> map);
    int getAirOperationRecordCount(Map<String, Object> map);
    int insertAirOperationRecord(Map<String, Object> map);

    ArrayList<Map<String, Object>> getAirBuilding(Map<String, Object> map);
    ArrayList<Map<String, Object>> getAirByParams(Map<String, Object> map);

    ArrayList<Map<String, Object>> getAirList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getAirSwitchTag(Map<String, Object> map);

    ArrayList<Map<String, Object>> getAirEnergyList(Map<String, Object> map);



    //============================APP 照明控制=============================//
    int addLightTimingSetting(Map<String, Object> map);
    int addLightIntelligentControl(Map<String, Object> map);
    int addLightRelation(Map<String, Object> map);

    int updateLightTimingSetting(Map<String, Object> map);
    int updateLightIntelligentControl(Map<String, Object> map);
    int deleteLightTimingSetting(Map<String, Object> map);
    int deleteLightIntelligentControl(Map<String, Object> map);

    String getLightSwitchTagById(String id);
    ArrayList<Map<String, Object>> getLightTimingSetting(Map<String, Object> map);

    ArrayList<Map<String, Object>> getLightOperationRecord(Map<String, Object> map);
    int getLightOperationRecordCount(Map<String, Object> map);
    int insertLightOperationRecord(Map<String, Object> map);

    ArrayList<Map<String, Object>> getLightBuilding(Map<String, Object> map);
    ArrayList<Map<String, Object>> getLightByParams(Map<String, Object> map);
    ArrayList<Map<String, Object>> getLightEnergyList(Map<String, Object> map);

    ArrayList<Map<String, Object>> getLightList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getLightSwitchTag(Map<String, Object> map);




}
