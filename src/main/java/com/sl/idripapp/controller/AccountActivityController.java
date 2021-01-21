package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/12/29 15:48
 * FileName: AccountActivityController
 * Description: 班组台账
 */
@Controller
@RequestMapping("/accountActivity")
public class AccountActivityController {
	@Autowired
	AccountActivityService accountActivityService;


	/**
	 * 类型列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getType")
	@ResponseBody
	public AppResult getType(@RequestParam Map<String, Object> map){
		return accountActivityService.getType(map);
	}

	/**
	 * 获取时间年份
	 * @param
	 * @return
	 */
	@RequestMapping("/getDate")
	@ResponseBody
	public AppResult getDate(@RequestParam Map<String, Object> map){
		return accountActivityService.getDate(map);
	}

	/**
	 * 班组列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getActivityList")
	@ResponseBody
	public AppResult getActivityList(@RequestParam Map<String, Object> map){
		return accountActivityService.getActivityList(map);
	}

}
