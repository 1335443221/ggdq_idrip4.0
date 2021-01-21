package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.ParkWholeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/parkWhole")
public class ParkWholeController {
	
	@Autowired
	ParkWholeService parkWholeService;
	
	/**
	 * 健康指数
	 * @param
	 * @return
	 */
	@RequestMapping("/healthIndex")
	@ResponseBody
	public AppResult healthIndex(@RequestParam Map<String, Object> map){
		return parkWholeService.healthIndex(map);
	}
	
	/**
	 * 运维状态
	 * @param
	 * @return
	 */
	@RequestMapping("operationAndMaintenance")
	@ResponseBody
	public AppResult operationAndMaintenance(@RequestParam Map<String, Object> map){
		return parkWholeService.operationAndMaintenance(map);
	}


	/**
	 * 意见反馈
	 * @param
	 * @return
	 */
	@RequestMapping("addOpinion")
	@ResponseBody
	public AppResult addOpinion(@RequestParam Map<String, Object> map){
		return parkWholeService.addOpinion(map);
	}


	/**
	 * 任务类型 待办列表
	 * @param
	 * @return
	 */
	@RequestMapping("getTaskType")
	@ResponseBody
	public AppResult getTaskType(@RequestParam Map<String, Object> map){
		return parkWholeService.getTaskType(map);
	}


	/**
	 * 任务状态 待办列表
	 * @param
	 * @return
	 */
	@RequestMapping("getTaskStatus")
	@ResponseBody
	public AppResult getTaskStatus(@RequestParam Map<String, Object> map){
		return parkWholeService.getTaskStatus(map);
	}


	/**
	 * 列表 任务
	 * @param
	 * @return
	 */
	@RequestMapping("getTaskList")
	@ResponseBody
	public AppResult getTaskList(@RequestParam Map<String, Object> map){
		return parkWholeService.getTaskList(map);
	}


	
}
	
