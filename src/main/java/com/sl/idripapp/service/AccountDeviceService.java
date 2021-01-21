package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountDeviceService {

    public AppResult getDeviceList(Map<String, Object> map);
    public AppResult getAllDevice(Map<String, Object> map);
    public AppResult getMaintenanceStatus(Map<String, Object> map);
    public AppResult getDevice(Map<String, Object> map);
    public AppResult addMaintenance(Map<String, Object> map);
    public AppResult getMaintenance(Map<String, Object> map);
    public AppResult updateMaintenance(Map<String, Object> map);

}
