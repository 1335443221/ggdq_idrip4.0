package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.ElcmDeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("device")
public class ElcmDeviceController {

    @Autowired
    private ElcmDeviceInfoService deviceInfoService;


    /**
     * 获取所有的设备类型
     * @param map
     * @return
     */
    @PostMapping("getDeviceTypeTree")
    public AppResult getDeviceTypeTree(@RequestParam Map<String, Object> map){
        return deviceInfoService.getDeviceTypeTree(map);
    }

    /**
     * 获取所有的设备类型
     * @param map
     * @return
     */
    @PostMapping("getDeviceTypes")
    public AppResult getDeviceTypes(@RequestParam Map<String, Object> map){
        return deviceInfoService.getDeviceTypes(map);
    }

    /**
     * 获取所有的设备状态
     * @param map
     * @return
     */
    @PostMapping("getDeviceStatus")
    public AppResult getDeviceStatus(@RequestParam Map<String, Object> map){
        return deviceInfoService.getDeviceStatus(map);
    }

    /**
     * 根据设备类型和设备状态获取设备列表
     * @param map
     * @return
     */
    @PostMapping("getDeviceList")
    public AppResult getDeviceList(@RequestParam Map<String, Object> map){
        return deviceInfoService.getDeviceList(map);
    }

    /**
     * 根据设备id获取单个设备
     * @param map
     * @return
     */
    @PostMapping("getDeviceById")
    public AppResult getDeviceById(@RequestParam Map<String, Object> map){
        return deviceInfoService.getDeviceById(map);
    }

    /**
     * 根据设备id获取技术参数
     * @param map
     * @return
     */
    @PostMapping("getTechnologyParam")
    public AppResult getTechnologyParam(@RequestParam Map<String, Object> map){
        return deviceInfoService.getTechnologyParam(map);
    }

    /**
     * 根据设备id获取设备保养清单
     * @param map
     * @return
     */
    @PostMapping("getMaintenanceList")
    public AppResult getMaintenanceList(@RequestParam Map<String, Object> map){
        return deviceInfoService.getMaintenanceList(map);
    }

    /**
     * 根据设备id获取设备维保记录
     * @param map
     * @return
     */
    @PostMapping("getMaintenanceRecord")
    public AppResult getMaintenanceRecord(@RequestParam Map<String, Object> map){
        return deviceInfoService.getMaintenanceRecord(map);
    }


    /**
     * 根据设备id获取实时数据
     * @param map
     * @return
     */
    @PostMapping("getRealTimeData")
    public AppResult getRealTimeData(@RequestParam Map<String, Object> map) {
        return deviceInfoService.getRealTimeData(map);

    }
    /**
     * 根据设备id获取元器件组成
     * @param map
     * @return
     */
    @PostMapping("getComponent")
    public AppResult getComponent(@RequestParam Map<String, Object> map){
        return deviceInfoService.getComponent(map);
    }

    /**
     * 获取设备资料
     * @param map
     * @return
     */
    @PostMapping("getMaterial")
    public AppResult getMaterial(@RequestParam Map<String, Object> map){
        return deviceInfoService.getMaterial(map);
    }
}
