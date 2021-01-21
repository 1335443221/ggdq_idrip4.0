package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;


public interface FireService {

    /**
     * 探测器-位置列表(切换设备)
     * @param map
     * @return
     */
    public AppResult detectorPositionList(Map<String, Object> map);

    /**
     * 探测器-状态列表(主机→主线→探测器(没有主线 则为 主机→探测器))
     * @param map
     * @return
     */
    public AppResult detectorStateList(Map<String, Object> map);


    /**
     * 探测器-设备信息
     * @param map
     * @return
     */
    public AppResult getDetectorData(Map<String, Object> map);

    /**
     * 探测器-设备实时数据
     * @param map
     * @return
     */
    public AppResult getData(Map<String, Object> map);

    /**
     * 探测器-设备获取园区的经纬度
     * @param
     * @return
     */
    public AppResult getFactory(Map<String, Object> map);

    /**
     * 探测器-下置复位
     * @param
     * @return
     */
    public AppResult reset(Map<String, Object> map);

    /**
     * 探测器-获取楼的横竖坐标
     * @param
     * @return
     */
    public AppResult getBuilding(Map<String, Object> map);
    /**
     * 探测器-曲线数据
     * @param
     * @return
     */
    public AppResult getCurveData(Map<String, Object> map);

    /**
     * 探测器-分场景页面实时数据
     * @param
     * @return
     */
    public AppResult getLoopData(Map<String, Object> map);
    /**
     * 探测器-分场景页面实时数据初始化
     * @param
     * @return
     */
    public AppResult getLoopInitData(Map<String, Object> map);

    /**
     * 探测器-巡检集合
     * @param
     * @return
     */
    public AppResult patrolList(Map<String, Object> map);
    /**
     * 探测器-新增巡检任务
     * @param
     * @return
     */
    public AppResult addPatrol(Map<String, Object> map);
    /**
     * 探测器-巡检详情
     * @param
     * @return
     */
    public AppResult patrolDetail(Map<String, Object> map);
    /**
     * 探测器-巡检完成
     * @param
     * @return
     */
    public AppResult patrolComplete(Map<String, Object> map);
    /**
     * 探测器-隐患上报
     * @param
     * @return
     */
    public AppResult addMalfunction(Map<String, Object> map);
    /**
     * 探测器-隐患处理
     * @param
     * @return
     */
    public AppResult malfunctionDeal(Map<String, Object> map);
    /**
     * 探测器-隐患管理列表
     * @param
     * @return
     */
    public AppResult malfunctionList(Map<String, Object> map);
    /**
     * 探测器-隐患详情
     * @param
     * @return
     */
    public AppResult malfunctionDetail(Map<String, Object> map);


}
