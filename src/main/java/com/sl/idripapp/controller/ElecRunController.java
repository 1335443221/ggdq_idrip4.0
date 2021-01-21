package com.sl.idripapp.controller;

import com.sl.idripapp.service.ElecRunDataService;
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
 * FileName: ElecRunController
 * Description: APP - 智用电 模块 实时数据接口
 */
@Controller
@RequestMapping("/run")
public class ElecRunController {
	
	@Autowired
	ElecRunDataService runDataImpl;

	/**
	 * 进线列表
	 * @param map
	 * @return
	 */
	@RequestMapping("/getCoilin_list")
	@ResponseBody
	public Object getCoilinList(@RequestParam Map<String, Object> map){
		return runDataImpl.getCoilinList(map);
	}	
	
	
	
	/**
	 * 进线详情（带出线数据）
	 * @param map
	 * @return
	 */
	@RequestMapping("/getCoilin_detail")
	@ResponseBody
	public Object getCoilin_detail(@RequestParam Map<String, Object> map){
		return runDataImpl.getCoilinDetail(map);
	}	
	
	
	/**
	 * 出线详情
	 * @param map
	 * @return
	 */
	@RequestMapping("/getCoilout_detail")
	@ResponseBody
	public Object getCoilout_detail(@RequestParam Map<String, Object> map){
		return runDataImpl.getCoiloutDetail(map);
	}	
	
	
	
	/**
	 * 开关按钮
	 * @param map
	 * @return
	 */
	@RequestMapping("/set_di")
	@ResponseBody
	public Object set_di(@RequestParam Map<String, Object> map){
		return runDataImpl.setDi(map);
	}	
	
	

}
