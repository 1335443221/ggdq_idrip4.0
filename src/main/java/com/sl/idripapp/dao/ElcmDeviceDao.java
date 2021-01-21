package com.sl.idripapp.dao;

import com.sl.idripapp.entity.ElcmDeviceTypeTree;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface ElcmDeviceDao {

    //获取所有的设备类型
    ArrayList<ElcmDeviceTypeTree> getDeviceTypeTree(Map<String, Object> map);
    ArrayList<Map<String,Object>> getDeviceCountByType(Map<String, Object> map);

    //获取所有的设备类型
    ArrayList<HashMap<String, Object>> getDeviceTypes();

    //获取所有的设备状态
    ArrayList<HashMap<String, Object>> getDeviceStatus();

    //根据设备类型和设备状态获取设备列表->count
    int getDeviceListCount(Map<String, Object> map);

    //根据设备类型和设备状态获取设备列表
    ArrayList<HashMap<String, Object>> getDeviceList(Map<String, Object> map);

    //根据设备id获取单个设备
    HashMap<String, Object> getDeviceById(Map<String, Object> map);

    //根据设备id获取技术参数
    ArrayList<HashMap<String, Object>> getTechnologyParam(Map<String, Object> map);

    //根据设备id获取设备保养清单->count
    int getMaintenanceListCount(Map<String, Object> map);

    //根据设备id获取设备保养清单
    ArrayList<HashMap<String, Object>> getMaintenanceList(Map<String, Object> map);

    //根据设备id获取设备维保记录->count
    int getMaintenanceRecordCount(Map<String, Object> map);

    //根据设备id获取设备维保记录
    ArrayList<HashMap<String, Object>> getMaintenanceRecord(Map<String, Object> map);

    //获取redis中需要的数据
    HashMap<String, Object> getRedisDevice(Map<String, Object> map);
    ArrayList<Map<String, Object>> getRedisDeviceTag(Map<String, Object> map);
    //维保记录统计每种维保类型次数
    ArrayList<HashMap<String, Object>> getMaintenanceTypeCount(Map<String, Object> map);

    //查询设备组成元器件
    ArrayList<HashMap<String, Object>> getComponent(Map<String, Object> map);

    //获取设备资料
    ArrayList<HashMap<String, Object>> getMaterial(Map<String, Object> map);

    //获取设备资料-count
    Integer getMaterialCount(Map<String, Object> map);
}
