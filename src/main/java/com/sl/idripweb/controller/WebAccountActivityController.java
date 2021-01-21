package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountActivityService;
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
 * FileName: AccountActivityController
 * Description: 劳保台账
 */
@Controller
@RequestMapping("/webAccountActivity")
public class WebAccountActivityController {
	@Autowired
	WebAccountActivityService webAccountActivityService;


	/**
	 * 获取所有类别
	 * @param
	 * @return
	 */
	@RequestMapping("/getType")
	@ResponseBody
	public WebResult getType(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.getType(map);
	}
	/**
	 * 新增分类
	 * @param
	 * @return
	 */
	@RequestMapping("/addType")
	@ResponseBody
	public WebResult addType(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.addType(map);
	}
	/**
	 * 编辑分类
	 * @param
	 * @return
	 */
	@RequestMapping("/updateType")
	@ResponseBody
	public WebResult updateType(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.updateType(map);
	}
	/**
	 * 删除分类
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteType")
	@ResponseBody
	public WebResult deleteType(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.deleteType(map);
	}

	//==========================活动=============================//
	/**
	 * 新增活动
	 * @param
	 * @return
	 */
	@RequestMapping("/addActivity")
	@ResponseBody
	public WebResult addActivity(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.addActivity(map);
	}

	/**
	 * 活动列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getActivityList")
	@ResponseBody
	public WebResult getActivityList(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.getActivityList(map);
	}

	/**
	 * 编辑活动
	 * @param
	 * @return
	 */
	@RequestMapping("/updateActivity")
	@ResponseBody
	public WebResult updateActivity(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.updateActivity(map);
	}

	/**
	 * 删除活动
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteActivity")
	@ResponseBody
	public WebResult deleteActivity(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.deleteActivity(map);
	}



	/**
	 * 回收站列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getRecycleBin")
	@ResponseBody
	public WebResult getRecycleBin(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.getRecycleBin(map);
	}

	/**
	 * 恢复
	 * @param
	 * @return
	 */
	@RequestMapping("/recovery")
	@ResponseBody
	public WebResult recovery(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.recovery(map);
	}


	/**
	 * 完全删除
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteCompletely")
	@ResponseBody
	public WebResult deleteCompletely(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.deleteCompletely(map);
	}

	/**
	 * 添加查看和下载的记录
	 * @param
	 * @return
	 */
	@RequestMapping("/addRecord")
	@ResponseBody
	public WebResult addRecord(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.addRecord(map);
	}

	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/statistics")
	@ResponseBody
	public WebResult statistics(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.statistics(map);
	}

	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/recordStatistics")
	@ResponseBody
	public WebResult recordStatistics(@RequestAttribute Map<String, Object> map){
		return webAccountActivityService.recordStatistics(map);
	}



}
