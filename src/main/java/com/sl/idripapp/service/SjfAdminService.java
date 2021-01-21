package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;


public interface SjfAdminService {

    /**
     * 询
     * @param map
     * @return
     */
    public AppResult getHouseEpList(Map<String, Object> map);
    public AppResult getHouseEpDetail(Map<String, Object> map);

    /**
     * 缴费记录
     * @param map
     * @return
     */
    public AppResult getHousePaymentRecord(Map<String, Object> map);
    public AppResult getHouseRecordDetail(Map<String, Object> map);
    /**
     * 分户管理
     * @param map
     * @return
     */
    public AppResult getHouseList(Map<String, Object> map);
    public AppResult getHouseType(Map<String, Object> map);
    public AppResult updateHouse(Map<String, Object> map);
    public AppResult getHouseDetail(Map<String, Object> map);
    public AppResult deleteHouse(Map<String, Object> map);
    public AppResult addHouse(Map<String, Object> map);
    public AppResult moveHouseToType(Map<String, Object> map);
    public AppResult moveAllHouseToType(Map<String, Object> map);
    public AppResult getHouseElecMeterNumber(Map<String, Object> map);
    public AppResult supplementFees(Map<String, Object> map);
    /**
     * 电费设置
     * @param map
     * @return
     */
    public AppResult updateFpg(Map<String, Object> map);
    public AppResult updateElecSettingByType(Map<String, Object> map);
    public AppResult updateLadder(Map<String, Object> map);
    public AppResult deleteFpgFuture(Map<String, Object> map);
    public AppResult deleteElecSettingFuture(Map<String, Object> map);
    public AppResult deleteLadderFuture(Map<String, Object> map);
    public AppResult addElecSetting(Map<String, Object> map);
    public AppResult deleteElecSetting(Map<String, Object> map);
    public AppResult checkFpgFuture(Map<String, Object> map);
    public AppResult checkElecSettingFuture(Map<String, Object> map);
    public AppResult checkLadderFuture(Map<String, Object> map);
    public AppResult getFpg(Map<String, Object> map);
    public AppResult getFpgFuture(Map<String, Object> map);
    public AppResult getElecSettingByFuture(Map<String, Object> map);
    public AppResult getElecSettingByType(Map<String, Object> map);
    public AppResult getLadder(Map<String, Object> map);
    public AppResult getLadderFuture(Map<String, Object> map);
    public AppResult setAllVal(Map<String, Object> map);

}
