package com.sl.idripapp.dao;

import com.sl.idripapp.entity.AccountActivityTypeTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountDeviceDao {


    ArrayList<HashMap<String, Object>> getDeviceList(Map<String, Object> map);
    Integer getDeviceListCount(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getAllDevice(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getMaintenanceStatus(Map<String, Object> map);

    Map<String, Object> getDevice(@Param("id") String id);
    Integer addMaintenance(Map<String, Object> map);
    Integer updateDeviceByMaintenance(Map<String, Object> map);
    Integer updateMaintenance(Map<String, Object> map);

    Map<String, Object> getMaintenance(@Param("deviceId") String id);

}
