package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface ElcmDeviceTaskService {

    //获取即将开始任务数量
    AppResult getAboutToStartCount(Map<String, Object> map);

    //根据任务状态获取任务列表数据
    AppResult getTaskList(Map<String, Object> map);
    AppResult getMyTaskRecordList(Map<String, Object> map);


    AppResult getTaskRecordStatus(Map<String, Object> map);
    AppResult getTaskRecordType(Map<String, Object> map);

    //根据任务id获取单个任务
    AppResult getTaskById(Map<String, Object> map);

    AppResult updateStartTime(Map<String, Object> map);

    AppResult assignUser(Map<String, Object> map);


    AppResult receiveRecord(Map<String, Object> map);
    AppResult chargeback(Map<String, Object> map);
    AppResult transfer(Map<String, Object> map);
    //任务完成
    AppResult completeTask(Map<String, Object> map);

    AppResult getSparepartList(Map<String, Object> map);

    AppResult getSparepartApplyDetail(Map<String, Object> map);
    AppResult getSparepartsUseChoice(Map<String, Object> map);
    AppResult getSparepartsUseDetail(Map<String, Object> map);

    AppResult getSparepartApplyList(Map<String, Object> map);

    AppResult addSeparepartsApply(Map<String, Object> map);

    AppResult revoke(Map<String, Object> map);
    AppResult approve(Map<String, Object> map);
}
