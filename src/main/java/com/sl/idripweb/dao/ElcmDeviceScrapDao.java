package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface ElcmDeviceScrapDao {

    ArrayList<Map<String, Object>> getScrapStatus(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getScrapList(Map<String, Object> map);
    int getScrapListCount(Map<String, Object> map);
    int getScrapDeciceCount(Map<String, Object> map);
    int updateScrapStatus(Map<String, Object> map);
    int updateDeviceStatus(Map<String, Object> map);
    int deleteScrap(Map<String, Object> map);
    int addScrap(Map<String, Object> map);
    int updateScrap(Map<String, Object> map);
    Map<String, Object> getScrapDetail(Map<String, Object> map);
}
