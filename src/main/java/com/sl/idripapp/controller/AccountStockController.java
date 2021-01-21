package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountStockService;
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
 * Date: 2020/12/29 15:48
 * FileName: AccountStockController
 * Description: 库存台账
 */
@Controller
@RequestMapping("/accountStock")
public class AccountStockController {
	@Autowired
	AccountStockService accountStockService;


	/**
	 * 库存列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockList")
	@ResponseBody
	public AppResult getStockList(@RequestParam Map<String, Object> map){
		return accountStockService.getStockList(map);
	}

	/**
	 * 出库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockOutList")
	@ResponseBody
	public AppResult getStockOutList(@RequestParam Map<String, Object> map){
		return accountStockService.getStockOutList(map);
	}


	/**
	 * 所有库存列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllStock")
	@ResponseBody
	public AppResult getAllStock(@RequestParam Map<String, Object> map){
		return accountStockService.getAllStock(map);
	}


	/**
	 * 新增出库
	 * @param
	 * @return
	 */
	@RequestMapping("/addStockOut")
	@ResponseBody
	public AppResult addStockOut(@RequestParam Map<String, Object> map){
		return accountStockService.addStockOut(map);
	}



	/**
	 * 修改出库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateStockOut")
	@ResponseBody
	public AppResult updateStockOut(@RequestParam Map<String, Object> map){
		return accountStockService.updateStockOut(map);
	}


	/**
	 * 删除出库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteStockOut")
	@ResponseBody
	public AppResult deleteStockOut(@RequestParam Map<String, Object> map){
		return accountStockService.deleteStockOut(map);
	}

	/**
	 * 出库详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockOutDetail")
	@ResponseBody
	public AppResult getStockOutDetail(@RequestParam Map<String, Object> map){
		return accountStockService.getStockOutDetail(map);
	}

	//==========================================入库==================================//
	/**
	 * 获取入库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockInList")
	@ResponseBody
	public AppResult getStockInList(@RequestParam Map<String, Object> map){
		return accountStockService.getStockInList(map);
	}


	/**
	 * 新增入库
	 * @param
	 * @return
	 */
	@RequestMapping("/addStockIn")
	@ResponseBody
	public AppResult addStockIn(@RequestParam Map<String, Object> map){
		return accountStockService.addStockIn(map);
	}


	/**
	 * 修改入库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateStockIn")
	@ResponseBody
	public AppResult updateStockIn(@RequestParam Map<String, Object> map){
		return accountStockService.updateStockIn(map);
	}


	/**
	 * 删除入库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteStockIn")
	@ResponseBody
	public AppResult deleteStockIn(@RequestParam Map<String, Object> map){
		return accountStockService.deleteStockIn(map);
	}

	/**
	 * 入库详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockInDetail")
	@ResponseBody
	public AppResult getStockInDetail(@RequestParam Map<String, Object> map){
		return accountStockService.getStockInDetail(map);
	}
}
