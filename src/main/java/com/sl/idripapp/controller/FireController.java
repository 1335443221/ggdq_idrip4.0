package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.FireService;
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
 * FileName: FireController
 * Description: APP - 电气火灾 模块
 */
@Controller
@RequestMapping("/fire")
public class FireController {
	@Autowired
	FireService fireService;



	/**
	 * 探测器-位置列表(切换设备)
	 * @param
	 * @return
	 */
	@RequestMapping("/detectorPositionList")
	@ResponseBody
	public AppResult detectorPositionList(@RequestParam Map<String, Object> map){
		return fireService.detectorPositionList(map);
	}


	/**
	 * 探测器-状态列表(主机→主线→探测器(没有主线 则为 主机→探测器))
	 * @param
	 * @return
	 */
	@RequestMapping("/detectorStateList")
	@ResponseBody
	public AppResult detectorStateList(@RequestParam Map<String, Object> map){
		return fireService.detectorStateList(map);
	}

	/**
	 * 探测器-设备信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getDetectorData")
	@ResponseBody
	public AppResult getDetectorData(@RequestParam Map<String, Object> map){
		return fireService.getDetectorData(map);
	}

	/**
	 * 探测器-设备实时数据
	 * @param
	 * @return
	 */
	@RequestMapping("/getData")
	@ResponseBody
	public AppResult getData(@RequestParam Map<String, Object> map){
		return fireService.getData(map);
	}

	/**
	 * 探测器-设备获取园区的经纬度
	 * @param
	 * @return
	 */
	@RequestMapping("/getFactory")
	@ResponseBody
	public AppResult getFactory(@RequestParam Map<String, Object> map){
		return fireService.getFactory(map);
	}


	/**
	 * 探测器-下置复位
	 * @param
	 * @return
	 */
	@RequestMapping("/reset")
	@ResponseBody
	public AppResult reset(@RequestParam Map<String, Object> map){
		return fireService.reset(map);
	}



	/**
	 * 探测器-获取楼的横竖坐标
	 * @param
	 * @return
	 */
	@RequestMapping("/getBuilding")
	@ResponseBody
	public AppResult getBuilding(@RequestParam Map<String, Object> map){
		return fireService.getBuilding(map);
	}

	/**
	 * 探测器-获取曲线数据
	 * @param
	 * @return
	 */
	@RequestMapping("/getCurveData")
	@ResponseBody
	public AppResult getCurveData(@RequestParam Map<String, Object> map){
		return fireService.getCurveData(map);
	}
	/**
	 * 探测器-分场景页面实时数据
	 * @param
	 * @return
	 */
	@RequestMapping("/getLoopData")
	@ResponseBody
	public AppResult getLoopData(@RequestParam Map<String, Object> map){
		return fireService.getLoopData(map);
	}
	/**
	 * 探测器-分场景页面实时数据初始化
	 * @param
	 * @return
	 */
	@RequestMapping("/getLoopInitData")
	@ResponseBody
	public AppResult getLoopInitData(@RequestParam Map<String, Object> map){
		return fireService.getLoopInitData(map);
	}



//==============================巡检===========================/
	/**
	 * 探测器-巡检列表
	 * @param
	 * @return
	 */
	@RequestMapping("/patrolList")
	@ResponseBody
	public AppResult patrolList(@RequestParam Map<String, Object> map){
		return fireService.patrolList(map);
	}

	/**
	 * 探测器-新增巡检任务
	 * @param
	 * @return
	 */
	@RequestMapping("/addPatrol")
	@ResponseBody
	public AppResult addPatrol(@RequestParam Map<String, Object> map){
		return fireService.addPatrol(map);
	}
	/**
	 * 探测器-巡检详情
	 * @param
	 * @return
	 */
	@RequestMapping("/patrolDetail")
	@ResponseBody
	public AppResult patrolDetail(@RequestParam Map<String, Object> map){
		return fireService.patrolDetail(map);
	}
	/**
	 * 探测器-巡检完成
	 * @param
	 * @return
	 */
	@RequestMapping("/patrolComplete")
	@ResponseBody
	public AppResult patrolComplete(@RequestParam Map<String, Object> map){
		return fireService.patrolComplete(map);
	}
	/**
	 * 探测器-隐患上报
	 * @param
	 * @return
	 */
	@RequestMapping("/addMalfunction")
	@ResponseBody
	public AppResult addMalfunction(@RequestParam Map<String, Object> map){
		return fireService.addMalfunction(map);
	}
	/**
	 * 探测器-隐患处理
	 * @param
	 * @return
	 */
	@RequestMapping("/malfunctionDeal")
	@ResponseBody
	public AppResult malfunctionDeal(@RequestParam Map<String, Object> map){
		return fireService.malfunctionDeal(map);
	}
	/**
	 * 探测器-隐患管理列表
	 * @param
	 * @return
	 */
	@RequestMapping("/malfunctionList")
	@ResponseBody
	public AppResult malfunctionList(@RequestParam Map<String, Object> map){
		return fireService.malfunctionList(map);
	}

	/**
	 * 探测器-隐患详情
	 * @param
	 * @return
	 */
	@RequestMapping("/malfunctionDetail")
	@ResponseBody
	public AppResult malfunctionDetail(@RequestParam Map<String, Object> map){
		return fireService.malfunctionDetail(map);
	}


}
