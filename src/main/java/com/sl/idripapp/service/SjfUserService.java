package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;


public interface SjfUserService {

    /**
     * 通用-获取厂区/楼列表
     * @param map
     * @return
     */
    public AppResult getFactoryList(Map<String, Object> map);

    /**
     *  缴费-获取收费单位
     * @param map
     * @return
     */
    public AppResult getChargeUnit(Map<String, Object> map);

    /**
     *  分户管理-验证分户是否存在
     * @param map
     * @return
     */
    public AppResult checkHouseNumber(Map<String, Object> map);

    /**
     *   分户管理-获取分户分组列表
     * @param map
     * @return
     */
    public AppResult getGroupList(Map<String, Object> map);

    /**
     *  分户管理-缴费分户列表
     * @param map
     * @return
     */
    public AppResult getUserHouseList(Map<String, Object> map);

    /**
     *  分户管理-新增分户分组
     * @param map
     * @return
     */
    public AppResult addGroup(Map<String, Object> map);

    /**
     * 分户管理-编辑分组信息
     * @param map
     * @return
     */
    public AppResult updateGroup(Map<String, Object> map);

    /**
     *  分户管理-删除分组
     * @param map
     * @return
     */
    public AppResult deleteGroup(Map<String, Object> map);

    /**
     *   分户管理-删除户号
     * @param map
     * @return
     */
    public AppResult deleteHouse(Map<String, Object> map);
    public AppResult updateHouse(Map<String, Object> map);

    /**
     *余额-分户余额列表
     * @param map
     * @return
     */
    public AppResult getHouseBalance(Map<String, Object> map);
    /**
     *询-查询列表
     * @param map
     * @return
     */
    public AppResult getHouseEpList(Map<String, Object> map);
    /**
     * 询-用电详情
     * @param map
     * @return
     */
    public AppResult getHouseDetail(Map<String, Object> map);
    /**
     * 缴费记录-缴费记录列表
     * @param map
     * @return
     */
    public AppResult getHousePaymentRecord(Map<String, Object> map);
    /**
     * 缴费记录-缴费记录詳情
     * @param map
     * @return
     */
    public AppResult getHouseRecordDetail(Map<String, Object> map);
    /**
     *缴费-获取缴费用户信息
     * @param map
     * @return
     */
    public AppResult getChargeHouseDetail(Map<String, Object> map);


}
