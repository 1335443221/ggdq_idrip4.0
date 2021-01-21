package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountTrainService {

    //##########################培训管理############################
    //获取培训台账列表数据
    public WebResult getTrainList(Map<String,Object> map);

    //通过id获取培训台账
    public WebResult getTrainById(Map<String,Object> map);

    //新增培训台账
    public WebResult addTrain(Map<String,Object> map);

    //编辑培训台账
    public WebResult updateTrain(Map<String,Object> map);

    //删除培训台账
    public WebResult deleteTrain(Map<String,Object> map);

    //获取所有培训内容
    public WebResult getTrainContents(Map<String,Object> map);



    //##########################培训单############################
    //获取培训单列表数据
    public WebResult getTrainSheetList(Map<String,Object> map);

    //通过id获取单个培训单
    public WebResult getTrainSheetById(Map<String,Object> map);

    //新增培训单
    public WebResult addTrainSheet(Map<String,Object> map);

    //编辑培训单
    public WebResult updateTrainSheet(Map<String,Object> map);

    //删除培训单
    public WebResult deleteTrainSheet(Map<String,Object> map);




    //##########################数据统计############################
    //数据统计页面大接口
    public WebResult statistics(Map<String,Object> map);
}
