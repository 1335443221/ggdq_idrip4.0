package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface ParkWholeService {
	//健康指数
	public AppResult healthIndex(Map<String, Object> map);
	//运维状态
	public AppResult operationAndMaintenance(Map<String, Object> map);

	//意见反馈
	public AppResult addOpinion(Map<String, Object> map);

	//任务类型
	public AppResult getTaskType(Map<String, Object> map);
	public AppResult getTaskStatus(Map<String, Object> map);
	public AppResult getTaskList(Map<String, Object> map);
}
