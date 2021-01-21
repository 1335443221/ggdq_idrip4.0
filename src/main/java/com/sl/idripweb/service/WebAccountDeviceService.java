package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountDeviceService {

    public WebResult getAllDevice(Map<String, Object> map);
    public WebResult getDeviceCycle(Map<String, Object> map);
    public WebResult getDeviceRemind(Map<String, Object> map);
    public WebResult getDeviceFrequency(Map<String, Object> map);

    public WebResult addDevice(Map<String, Object> map);
    public WebResult updateDevice(Map<String, Object> map);
    public WebResult deleteDevice(Map<String, Object> map);
    public WebResult getDevice(Map<String, Object> map);
    public WebResult getMaintenanceStatus(Map<String, Object> map);


    public WebResult addMaintenance(Map<String, Object> map);
    public WebResult getMaintenanceList(Map<String, Object> map);
    public WebResult updateMaintenance(Map<String, Object> map);
    public WebResult deleteMaintenance(Map<String, Object> map);
    public WebResult statistics(Map<String, Object> map);

}
