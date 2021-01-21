package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/12/29 15:48
 * FileName: AccountDeviceController
 * Description: 班组台账
 */
@Controller
@RequestMapping("/accountDevice")
public class AccountDeviceController {
	@Autowired
	AccountDeviceService accountDeviceService;

	/**
	 * 设备列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getDeviceList")
	@ResponseBody
	public AppResult getDeviceList(@RequestParam Map<String, Object> map){
		return accountDeviceService.getDeviceList(map);
	}

	/**
	 * 所有设备列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllDevice")
	@ResponseBody
	public AppResult getAllDevice(@RequestParam Map<String, Object> map){
		return accountDeviceService.getAllDevice(map);
	}

	/**
	 *维保状态
	 * @param
	 * @return
	 */
	@RequestMapping("/getMaintenanceStatus")
	@ResponseBody
	public AppResult getMaintenanceStatus(@RequestParam Map<String, Object> map){
		return accountDeviceService.getMaintenanceStatus(map);
	}


	/**
	 * 获取设备信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getDevice")
	@ResponseBody
	public AppResult getDevice(@RequestParam Map<String, Object> map){
		return accountDeviceService.getDevice(map);
	}
	/**
	 * 添加维保记录
	 * @param
	 * @return
	 */
	@RequestMapping("/addMaintenance")
	@ResponseBody
	public AppResult addMaintenance(@RequestParam Map<String, Object> map){
		return accountDeviceService.addMaintenance(map);
	}


	/**
	 * 维保详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getMaintenance")
	@ResponseBody
	public AppResult getMaintenance(@RequestParam Map<String, Object> map){
		return accountDeviceService.getMaintenance(map);
	}

	/**
	 * 修改维保
	 * @param
	 * @return
	 */
	@RequestMapping("/updateMaintenance")
	@ResponseBody
	public AppResult updateMaintenance(@RequestParam Map<String, Object> map){
		return accountDeviceService.updateMaintenance(map);
	}



}
