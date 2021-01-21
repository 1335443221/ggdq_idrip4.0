package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/webCommon")
public class WebCommonController {
	@Autowired
	WebCommonService webCommonService;


	/**
	 * 园区接口
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getFactory")
	@ResponseBody
	public WebResult getFactoryBuilding(@RequestAttribute Map<String,Object> map){
		return webCommonService.getFactory(map);
	}
	/**
	 * 园区-建筑接口
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getBuilding")
	@ResponseBody
	public WebResult getBuilding(@RequestAttribute Map<String,Object> map){
		return webCommonService.getBuilding(map);
	}

	/**
	 * 园区-获取分户
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getSjfHouse")
	@ResponseBody
	public WebResult getHouse(@RequestAttribute Map<String,Object> map){
		return webCommonService.getSjfHouse(map);
	}



///=============
	/**
	 * user列表
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getUserList")
	@ResponseBody
	public WebResult getUserList(@RequestAttribute Map<String,Object> map){
		return webCommonService.getUserList(map);
	}

	/**
	 * 获取当前登录用户信息
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getUserInfo")
	@ResponseBody
	public WebResult getUserInfo(@RequestAttribute Map<String,Object> map, HttpServletRequest request){
		return webCommonService.getUserInfo(map, request);
	}



}
