package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: AirLightController
 * Description: 空调照明APP
 */
public interface AirLightService {

    /**
     * 空调-新增定时设置
     * @param map
     * @return
     */
    public AppResult addAirTimingSetting(Map<String, Object> map);


    /**
     * 空调-删除定时设置
     * @param map
     * @return
     */
    public AppResult deleteAirTimingSetting(Map<String, Object> map);


    /**
     * 空调-修改定时设置
     * @param map
     * @return
     */
    public AppResult updateAirTimingSetting(Map<String, Object> map);

    /**
     * 空调-获取定时设置信息
     * @param map
     * @return
     */
    public AppResult getAirTimingSetting(Map<String, Object> map);


    /**
     * 空调-下置开关
     * @param map
     * @return
     */
    public AppResult airSetVal(Map<String, Object> map);


    /**
     * @param map
     * @return
     */
    public AppResult getAirOperationRecord(Map<String, Object> map);

    /**
     * 空调-获取楼的信息
     * @param map
     * @return
     */
    public AppResult getAirBuilding(Map<String, Object> map);

    /**
     * @param
     * @return
     */
    public AppResult getAirList(Map<String, Object> map);

    /**
     * @param
     * @return
     */
    public AppResult getAirEnergy(Map<String, Object> map);

    /**
     * @param
     * @return
     */
    public AppResult addLightTimingSetting(Map<String, Object> map);
    /**
     * @param
     * @return
     */
    public AppResult getLightTimingSetting(Map<String, Object> map);

    /**
     * @param
     * @return
     */
    public AppResult getLightOperationRecord(Map<String, Object> map);
    /**
     * @param
     * @return
     */
    public AppResult getLightBuilding(Map<String, Object> map);

    /**
     * @param
     * @return
     */
    public AppResult getLightList(Map<String, Object> map);
    /**
     * @param
     * @return
     */
    public AppResult getLightEnergy(Map<String, Object> map);
    /**
     * @param
     * @return
     */
    public AppResult lightSetVal(Map<String, Object> map);

    /**
     * 照明-删除定时设置
     * @param map
     * @return
     */
    public AppResult deleteLightTimingSetting(Map<String, Object> map);


    /**
     * 照明-修改定时设置
     * @param map
     * @return
     */
    public AppResult updateLightTimingSetting(Map<String, Object> map);




}
