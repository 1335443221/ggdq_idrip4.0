package com.sl.idripapp.service.impl;

import com.sl.common.utils.AppResult;
import com.sl.common.utils.JwtToken;
import com.sl.common.utils.PageUtil;
import com.sl.idripapp.dao.AccountDeviceDao;
import com.sl.idripapp.service.AccountDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("AccountDeviceServiceImpl")
public class AccountDeviceServiceImpl implements AccountDeviceService {

	@Autowired
	AccountDeviceDao accountDeviceDao;

	/**
	 * 班组列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getDeviceList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		if (map.get("date")!=null&&!map.get("date").toString().equals("")){
			map.put("begin_time",map.get("date")+"-01-01 00:00:00");
			map.put("end_time",map.get("date")+"-12-31 23:59:59");
		}

		ArrayList<HashMap<String, Object>> list = accountDeviceDao.getDeviceList(map);
		int total = accountDeviceDao.getDeviceListCount(map);

		Map<String, Object> result = new HashMap<>();
		result.put("is_lastpage", PageUtil.setLastPage(map,total));  //添加是否最后一页
		result.put("list",list);
		return AppResult.success(result);
	}

	/**
	 * 所有设备
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getAllDevice(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<HashMap<String, Object>> list = accountDeviceDao.getAllDevice(map);
		return AppResult.success(list);
	}

	/**
	 * 状态
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getMaintenanceStatus(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> list = accountDeviceDao.getMaintenanceStatus(map);
		return AppResult.success(list);
	}

	/**
	 * 获取设备信息
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getDevice(Map<String, Object> map) {
		Map<String, Object> device = accountDeviceDao.getDevice(String.valueOf(map.get("id")));
		return AppResult.success(device);
	}


	/**
	 * 添加维保记录
	 */
	@Override
	public AppResult addMaintenance(Map<String, Object> map) {
		Map<String, Object> device = accountDeviceDao.getDevice(String.valueOf(map.get("deviceId")));
		if (device.get("lastStatus").toString().equals("2")){
			return AppResult.error("1051");
		}
		//添加维保
		int result= accountDeviceDao.addMaintenance(map);
		//把设备信息改为新增维保的时间/状态/人员
		accountDeviceDao.updateDeviceByMaintenance(map);
		if (result>0){
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}


	/**
	 * 获取维保记录
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getMaintenance(Map<String, Object> map) {
		Map<String, Object> device = accountDeviceDao.getMaintenance(String.valueOf(map.get("deviceId")));
		return AppResult.success(device);
	}


	/**
	 * 修改维保记录
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public AppResult updateMaintenance(Map<String, Object> map) {
		//修改维保
		int result= accountDeviceDao.updateMaintenance(map);

		//把设备信息改为新增维保的时间/状态/人员
		accountDeviceDao.updateDeviceByMaintenance(map);

		if (result>0){
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}
	}
}
