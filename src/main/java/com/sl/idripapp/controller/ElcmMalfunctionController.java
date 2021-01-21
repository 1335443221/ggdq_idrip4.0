package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.ElcmMalfunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/elcmMalfunction")
public class ElcmMalfunctionController {
	@Autowired
	ElcmMalfunctionService elcmMalfunctionService;


	/**
	 *获取故障列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getMalfunctionList")
	@ResponseBody
	public AppResult getMalfunctionList(@RequestParam Map<String, Object> map){
		return elcmMalfunctionService.getMalfunctionList(map);
	}


	/**
	 *获取已经存在的故障
	 * @param
	 * @return
	 */
	@RequestMapping("/getExistingMalfunction")
	@ResponseBody
	public AppResult getExistingMalfunction(@RequestParam Map<String, Object> map){
		return elcmMalfunctionService.getExistingMalfunction(map);
	}
	/**
	 *上报故障
	 * @param
	 * @return
	 */
	@RequestMapping("/reportMalfunction")
	@ResponseBody
	public AppResult reportMalfunction(@RequestParam Map<String, Object> map){
		return elcmMalfunctionService.reportMalfunction(map);
	}


	/**
	 *故障详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getMalfunctionDetail")
	@ResponseBody
	public AppResult getMalfunctionDetail(@RequestParam Map<String, Object> map){
		return elcmMalfunctionService.getMalfunctionDetail(map);
	}
	/**
	 *设备信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getDeviceById")
	@ResponseBody
	public AppResult getDeviceById(@RequestParam Map<String, Object> map){
		return elcmMalfunctionService.getDeviceById(map);
	}


	/**
	 * 新增维修
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/addRepair")
	@ResponseBody
	public AppResult addRepair(@RequestParam Map<String,Object> map){
		return  elcmMalfunctionService.addRepair(map);
	}
}
