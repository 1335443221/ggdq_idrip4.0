package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface ElcmDeviceInfoService {

    //获取所有的设备类型
    AppResult getDeviceTypeTree(Map<String, Object> map);
    AppResult getDeviceTypes(Map<String, Object> map);

    //获取所有的设备状态
    AppResult getDeviceStatus(Map<String, Object> map);

    //根据设备类型和设备状态获取设备列表
    AppResult getDeviceList(Map<String, Object> map);

    //根据设备id获取单个设备
    AppResult getDeviceById(Map<String, Object> map);

    //根据设备id获取技术参数
    AppResult getTechnologyParam(Map<String, Object> map);

    //根据设备id获取设备保养清单
    AppResult getMaintenanceList(Map<String, Object> map);

    //根据设备id获取设备维保记录
    AppResult getMaintenanceRecord(Map<String, Object> map);

    //获取实时数据
    AppResult getRealTimeData(Map<String, Object> map);

    //根据设备id获取元器件组成
    AppResult getComponent(Map<String, Object> map);

    //获取设备资料
    AppResult getMaterial(Map<String, Object> map);
}
