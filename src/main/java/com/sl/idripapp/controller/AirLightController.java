package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AirLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: AirLightController
 * Description: 空调照明APP 控制层
 */
@Controller
@RequestMapping("/airLight")
public class AirLightController {
	@Autowired
	AirLightService airLightService;


	/**
	 * 空调-新增定时设置
	 * @param
	 * @return
	 */
	@RequestMapping("/addAirTimingSetting")
	@ResponseBody
	public AppResult addAirTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.addAirTimingSetting(map);
	}

	/**
	 * 空调-删除定时设置
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteAirTimingSetting")
	@ResponseBody
	public AppResult deleteAirTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.deleteAirTimingSetting(map);
	}

	/**
	 * 空调-修改定时设置
	 * @param
	 * @return
	 */
	@RequestMapping("/updateAirTimingSetting")
	@ResponseBody
	public AppResult updateAirTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.updateAirTimingSetting(map);
	}



	/**
	 * 空调-获取定时设置信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getAirTimingSetting")
	@ResponseBody
	public AppResult getAirTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.getAirTimingSetting(map);
	}


	/**
	 * 空调-下置开关
	 * @param
	 * @return
	 */
	@RequestMapping("/airSetVal")
	@ResponseBody
	public AppResult airSetVal(@RequestParam Map<String, Object> map){
		return airLightService.airSetVal(map);
	}


	/**
	 * 空调-获取操作记录
	 * @param
	 * @return
	 */
	@RequestMapping("/getAirOperationRecord")
	@ResponseBody
	public AppResult getAirOperationRecord(@RequestParam Map<String, Object> map){
		return airLightService.getAirOperationRecord(map);
	}

	/**
	 * 空调-获取楼的信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getAirBuilding")
	@ResponseBody
	public AppResult getAirBuilding(@RequestParam Map<String, Object> map){
		return airLightService.getAirBuilding(map);
	}

	/**
	 * 空调-获取空调列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getAirList")
	@ResponseBody
	public AppResult getAirList(@RequestParam Map<String, Object> map){
		return airLightService.getAirList(map);
	}


	/**
	 * 空调-获取空调能耗
	 * @param
	 * @return
	 */
	@RequestMapping("/getAirEnergy")
	@ResponseBody
	public AppResult getAirEnergy(@RequestParam Map<String, Object> map){
		return airLightService.getAirEnergy(map);
	}



	/**
	 * 照明-新增定时设置
	 * @param
	 * @return
	 */
	@RequestMapping("/addLightTimingSetting")
	@ResponseBody
	public AppResult addLightTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.addLightTimingSetting(map);
	}

	/**
	 * 照明-删除定时设置
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteLightTimingSetting")
	@ResponseBody
	public AppResult deleteLightTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.deleteLightTimingSetting(map);
	}

	/**
	 * 照明-修改定时设置
	 * @param
	 * @return
	 */
	@RequestMapping("/updateLightTimingSetting")
	@ResponseBody
	public AppResult updateLightTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.updateLightTimingSetting(map);
	}

	/**
	 * 照明-获取定时设置信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getLightTimingSetting")
	@ResponseBody
	public AppResult getLightTimingSetting(@RequestParam Map<String, Object> map){
		return airLightService.getLightTimingSetting(map);
	}
	/**
	 *  照明-获取操作记录
	 * @param
	 * @return
	 */
	@RequestMapping("/getLightOperationRecord")
	@ResponseBody
	public AppResult getLightOperationRecord(@RequestParam Map<String, Object> map){
		return airLightService.getLightOperationRecord(map);
	}
	/**
	 * 照明-获取楼的信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getLightBuilding")
	@ResponseBody
	public AppResult getLightBuilding(@RequestParam Map<String, Object> map){
		return airLightService.getLightBuilding(map);
	}



	/**
	 * 照明-获取照明列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getLightList")
	@ResponseBody
	public AppResult getLightList(@RequestParam Map<String, Object> map){
		return airLightService.getLightList(map);
	}

	/**
	 * 照明-获取照明能耗
	 * @param
	 * @return
	 */
	@RequestMapping("/getLightEnergy")
	@ResponseBody
	public AppResult getLightEnergy(@RequestParam Map<String, Object> map){
		return airLightService.getLightEnergy(map);
	}

	/**
	 * 照明-下置开关
	 * @param
	 * @return
	 */
	@RequestMapping("/lightSetVal")
	@ResponseBody
	public AppResult lightSetVal(@RequestParam Map<String, Object> map){
		return airLightService.lightSetVal(map);
	}

}
