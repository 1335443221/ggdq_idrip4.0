package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/12/29 15:48
 * FileName: AccountMaintenanceController
 * Description: 劳保台账
 */
@Controller
@RequestMapping("/webAccountMaintenance")
public class WebAccountDeviceController {
	@Autowired
	WebAccountDeviceService webAccountMaintenanceService;

	/**
	 * 获取所有设备
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllDevice")
	@ResponseBody
	public WebResult getAllDevice(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.getAllDevice(map);
	}
	/**
	 * 周期
	 * @param
	 * @return
	 */
	@RequestMapping("/getDeviceCycle")
	@ResponseBody
	public WebResult getDeviceCycle(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.getDeviceCycle(map);
	}
	/**
	 * 提醒时间
	 * @param
	 * @return
	 */
	@RequestMapping("/getDeviceRemind")
	@ResponseBody
	public WebResult getDeviceRemind(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.getDeviceRemind(map);
	}
	/**
	 * 提醒频率
	 * @param
	 * @return
	 */
	@RequestMapping("/getDeviceFrequency")
	@ResponseBody
	public WebResult getDeviceFrequency(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.getDeviceFrequency(map);
	}

	/**
	 * 新增维保设备
	 * @param
	 * @return
	 */
	@RequestMapping("/addDevice")
	@ResponseBody
	public WebResult addDevice(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.addDevice(map);
	}

	/**
	 * 编辑设备
	 * @param
	 * @return
	 */
	@RequestMapping("/updateDevice")
	@ResponseBody
	public WebResult updateDevice(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.updateDevice(map);
	}

	/**
	 * 删除设备
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteDevice")
	@ResponseBody
	public WebResult deleteDevice(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.deleteDevice(map);
	}
	/**
	 * 获取设备详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getDevice")
	@ResponseBody
	public WebResult getDevice(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.getDevice(map);
	}

	/**
	 * 维保状态
	 * @param
	 * @return
	 */
	@RequestMapping("/getMaintenanceStatus")
	@ResponseBody
	public WebResult getMaintenanceStatus(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.getMaintenanceStatus(map);
	}

	/**
	 * 新增维保
	 * @param
	 * @return
	 */
	@RequestMapping("/addMaintenance")
	@ResponseBody
	public WebResult addMaintenance(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.addMaintenance(map);
	}


	/**
	 * 维保列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getMaintenanceList")
	@ResponseBody
	public WebResult getMaintenanceList(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.getMaintenanceList(map);
	}

	/**
	 * 修改维保
	 * @param
	 * @return
	 */
	@RequestMapping("/updateMaintenance")
	@ResponseBody
	public WebResult updateMaintenance(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.updateMaintenance(map);
	}

	/**
	 * 删除维保
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteMaintenance")
	@ResponseBody
	public WebResult deleteMaintenance(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.deleteMaintenance(map);
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/statistics")
	@ResponseBody
	public WebResult statistics(@RequestAttribute Map<String, Object> map){
		return webAccountMaintenanceService.statistics(map);
	}

}
