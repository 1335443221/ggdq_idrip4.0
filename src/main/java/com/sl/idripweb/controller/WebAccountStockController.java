package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
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
@RequestMapping("/webAccountStock")
public class WebAccountStockController {
	@Autowired
	WebAccountStockService webAccountStockService;


	/**
	 * 获取所有类别
	 * @param
	 * @return
	 */
	@RequestMapping("/getType")
	@ResponseBody
	public WebResult getType(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.getType(map);
	}


	/**
	 * 获取库存列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockList")
	@ResponseBody
	public WebResult getStockList(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.getStockList(map);
	}

	/**
	 * 新增库存
	 * @param
	 * @return
	 */
	@RequestMapping("/addStock")
	@ResponseBody
	public WebResult addStock(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.addStock(map);
	}
	/**
	 * 修改库存
	 * @param
	 * @return
	 */
	@RequestMapping("/updateStock")
	@ResponseBody
	public WebResult updateStock(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.updateStock(map);
	}

	/**
	 * 删除库存
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteStock")
	@ResponseBody
	public WebResult deleteStock(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.deleteStock(map);
	}


	//=======================出库管理=================================//

	/**
	 * 获取所有库存
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllStock")
	@ResponseBody
	public WebResult getAllStock(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.getAllStock(map);
	}


	/**
	 * 获取出库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockOutList")
	@ResponseBody
	public WebResult getStockOutList(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.getStockOutList(map);
	}


	/**
	 * 新增出库
	 * @param
	 * @return
	 */
	@RequestMapping("/addStockOut")
	@ResponseBody
	public WebResult addStockOut(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.addStockOut(map);
	}


	/**
	 * 修改出库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateStockOut")
	@ResponseBody
	public WebResult updateStockOut(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.updateStockOut(map);
	}


	/**
	 * 删除出库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteStockOut")
	@ResponseBody
	public WebResult deleteStockOut(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.deleteStockOut(map);
	}

	//==========================================入库==================================//
	/**
	 * 获取入库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getStockInList")
	@ResponseBody
	public WebResult getStockInList(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.getStockInList(map);
	}


	/**
	 * 新增入库
	 * @param
	 * @return
	 */
	@RequestMapping("/addStockIn")
	@ResponseBody
	public WebResult addStockIn(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.addStockIn(map);
	}


	/**
	 * 修改入库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateStockIn")
	@ResponseBody
	public WebResult updateStockIn(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.updateStockIn(map);
	}


	/**
	 * 删除入库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteStockIn")
	@ResponseBody
	public WebResult deleteStockIn(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.deleteStockIn(map);
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/statistics")
	@ResponseBody
	public WebResult statistics(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.statistics(map);
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/outAndInRanking")
	@ResponseBody
	public WebResult outAndInRanking(@RequestAttribute Map<String, Object> map){
		return webAccountStockService.outAndInRanking(map);
	}



}
