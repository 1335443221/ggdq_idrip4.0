package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountTrainService {

    //获取培训信息
    public AppResult getTrainList(Map<String,Object> map);

    //获取培训相关文件
    public AppResult getTrainSheetFiles(Map<String,Object> map);

    //获取参与人员
    public AppResult getAttendUser(Map<String,Object> map);

    //获取参与人员详情
    public AppResult getAttendUserDetail(Map<String,Object> map);
}
