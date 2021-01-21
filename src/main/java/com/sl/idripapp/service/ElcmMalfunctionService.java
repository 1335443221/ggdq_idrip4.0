package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;


public interface ElcmMalfunctionService {

    /**
     * 获取故障
     * @param map
     * @return
     */
    public AppResult getMalfunctionList(Map<String, Object> map);

    /**
     * 获取已经存在的故障
     * @param map
     * @return
     */
    public AppResult getExistingMalfunction(Map<String, Object> map);
    /**
     * 上报故障
     * @param map
     * @return
     */
    public AppResult reportMalfunction(Map<String, Object> map);


    /**
     * 故障详情
     * @param map
     * @return
     */
    public AppResult getMalfunctionDetail(Map<String, Object> map);
    /**
     * 设备信息
     * @param map
     * @return
     */
    public AppResult getDeviceById(Map<String, Object> map);


    public AppResult addRepair(Map<String, Object> map);




}
