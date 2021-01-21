package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface ElecReportService {

    //一个项目的所有厂区+配电
    public AppResult getProjectFactorys(Map<String, Object> map);

    //单日报表列表
    public AppResult singleDayList(Map<String, Object> map);

    //区间报表列表
    public AppResult sectionList(Map<String, Object> map);

    //单日报表详情
    public AppResult singleDayDetail(Map<String, Object> map);


    //单日报表详情
    public AppResult sectionDetail(Map<String, Object> map);

    //获取进线分类区间
    public AppResult sectionCoilinSort(Map<String, Object> map);

    //获取进线分类单日
    public AppResult singleDayCoilinSort(Map<String, Object> map);

    //电流电压曲线
    public AppResult getElecMonitorList(Map<String, Object> map);
}
