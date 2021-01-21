package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;


public interface AlermDataService {
    //报警列表
    public AppResult alermList(Map<String, Object> map);


    //报警详情
    public AppResult alermDetail(Map<String, Object> map);


    //报警处理
    public AppResult alermDispose(Map<String, Object> map);

//////====fire===============//

    //fire报警列表
    public AppResult fireAlermList(Map<String, Object> map);

    //fire火灾报警处理
    public AppResult fireAlermDispose(Map<String, Object> map);

    //fire火灾报警等级列表
    public AppResult getAlermLevel(Map<String, Object> map);

    //火灾报警详情
    public AppResult fireAlermDetail(Map<String, Object> map);

    //fire火灾报警类型
    public AppResult getFireCategoryRelation(Map<String, Object> map);

    //火灾标签列表
    public AppResult getFireTagByDeviceId(Map<String, Object> map);

    //报警配置新增
    public AppResult fireAlConfSave(Map<String, Object> map);

    public AppResult fireAlConfUpdate(Map<String, Object> map);

    //报警配置信息
    public AppResult fireAlConfData(Map<String, Object> map);

    //处理全部
    public int alermDisposeAll(Map<String, Object> map);
}
