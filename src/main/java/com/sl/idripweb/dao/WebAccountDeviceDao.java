package com.sl.idripweb.dao;

import com.sl.idripapp.entity.AccountActivityTypeTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountDeviceDao {

    List<Map<String, Object>>  getAllDevice(Map<String, Object> map);
    List<Map<String, Object>>  getDeviceCycle(Map<String, Object> map);
    List<Map<String, Object>>  getDeviceRemind(Map<String, Object> map);
    List<Map<String, Object>>  getDeviceFrequency(Map<String, Object> map);
    Integer  addDevice(Map<String, Object> map);
    Integer  updateDevice(Map<String, Object> map);
    Integer  deleteDevice(Map<String, Object> map);

    Map<String, Object>  getDevice(@Param("id") String id);

    List<Map<String, Object>>   getMaintenanceStatus(Map<String, Object> map);
    Integer updateDeviceByMaintenance(Map<String, Object> map);
    Integer addMaintenance(Map<String, Object> map);

    Map<String, Object> getMaintenanceByDeviceDesc(@Param("device_id") String device_id);

    ArrayList<HashMap<String, Object>> getMaintenanceList(Map<String, Object> map);
    Integer  getMaintenanceListCount(Map<String, Object> map);
    Integer  updateMaintenance(Map<String, Object> map);

    Integer  deleteMaintenance(List<String> list);



   /* Map<String, Object> getMaintenanceById(@Param("id") String id);
    Map<String, Object> getMaintenanceByIdLast(@Param("id") String id,@Param("device_id") String device_id);
    Map<String, Object> getMaintenanceByIdNext(@Param("id") String id,@Param("device_id") String device_id);*/

    List<Map<String, Object>> getMaintenanceGroupByFault(@Param("date") String date,
                                                   @Param("project_id") String project_id);

    List<Map<String, Object>> getMaintenanceByStatusRanking(@Param("date") String date,
                                                         @Param("project_id") String project_id);
    List<Map<String, Object>> getMaintenanceByContrast(@Param("date") String date,
                                                            @Param("project_id") String project_id);
}
