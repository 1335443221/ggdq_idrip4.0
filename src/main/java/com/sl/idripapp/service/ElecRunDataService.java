package com.sl.idripapp.service;

import java.util.Map;


public interface ElecRunDataService {
	

	
	//获取进线列表
	public Object getCoilinList(Map<String, Object> map);
	
	//获取进线数据（带出线列表）
	public Object getCoilinDetail(Map<String, Object> map);
	
	//获取出线数据
	public Object getCoiloutDetail(Map<String, Object> map);
	
	//开关接口
	public Object setDi(Map<String, Object> map);
	
		
		
}
