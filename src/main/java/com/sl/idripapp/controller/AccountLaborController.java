package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountLaborService;
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
 * FileName: AccountLaborController
 * Description: 劳保台账
 */
@Controller
@RequestMapping("/accountLabor")
public class AccountLaborController {
	@Autowired
	AccountLaborService accountLaborService;


	/**
	 * 劳保列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborList")
	@ResponseBody
	public AppResult getLaborList(@RequestParam Map<String, Object> map){
		return accountLaborService.getLaborList(map);
	}

	/**
	 * 出库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborOutList")
	@ResponseBody
	public AppResult getLaborOutList(@RequestParam Map<String, Object> map){
		return accountLaborService.getLaborOutList(map);
	}


	/**
	 * 所有劳保列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllLabor")
	@ResponseBody
	public AppResult getAllLabor(@RequestParam Map<String, Object> map){
		return accountLaborService.getAllLabor(map);
	}


	/**
	 * 新增出库
	 * @param
	 * @return
	 */
	@RequestMapping("/addLaborOut")
	@ResponseBody
	public AppResult addLaborOut(@RequestParam Map<String, Object> map){
		return accountLaborService.addLaborOut(map);
	}



	/**
	 * 修改出库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateLaborOut")
	@ResponseBody
	public AppResult updateLaborOut(@RequestParam Map<String, Object> map){
		return accountLaborService.updateLaborOut(map);
	}


	/**
	 * 删除出库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteLaborOut")
	@ResponseBody
	public AppResult deleteLaborOut(@RequestParam Map<String, Object> map){
		return accountLaborService.deleteLaborOut(map);
	}

	/**
	 * 出库详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborOutDetail")
	@ResponseBody
	public AppResult getLaborOutDetail(@RequestParam Map<String, Object> map){
		return accountLaborService.getLaborOutDetail(map);
	}

	//==========================================入库==================================//
	/**
	 * 获取入库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborInList")
	@ResponseBody
	public AppResult getLaborInList(@RequestParam Map<String, Object> map){
		return accountLaborService.getLaborInList(map);
	}


	/**
	 * 新增入库
	 * @param
	 * @return
	 */
	@RequestMapping("/addLaborIn")
	@ResponseBody
	public AppResult addLaborIn(@RequestParam Map<String, Object> map){
		return accountLaborService.addLaborIn(map);
	}


	/**
	 * 修改入库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateLaborIn")
	@ResponseBody
	public AppResult updateLaborIn(@RequestParam Map<String, Object> map){
		return accountLaborService.updateLaborIn(map);
	}


	/**
	 * 删除入库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteLaborIn")
	@ResponseBody
	public AppResult deleteLaborIn(@RequestParam Map<String, Object> map){
		return accountLaborService.deleteLaborIn(map);
	}

	/**
	 * 入库详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborInDetail")
	@ResponseBody
	public AppResult getLaborInDetail(@RequestParam Map<String, Object> map){
		return accountLaborService.getLaborInDetail(map);
	}
}
