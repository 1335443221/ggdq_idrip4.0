package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountLaborService;
import com.sl.idripweb.service.WebAccountPartsService;
import com.sl.idripweb.service.WebAccountStockService;
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
 * FileName: AccountPartsController
 * Description: 配件、工具台账
 */
@Controller
@RequestMapping("/webAccountParts")
public class WebAccountPartsController {
	@Autowired
	WebAccountPartsService webAccountPartsService;
	@Autowired
	WebAccountStockService webAccountStockService;
	@Autowired
	WebAccountLaborService webAccountLaborService;


	/**
	 * 获取所有类别
	 * @param
	 * @return
	 */
	@RequestMapping("/getType")
	@ResponseBody
	public WebResult getType(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.getType(map);
			case "3":
				return webAccountStockService.getType(map);
			case "4":
				return webAccountLaborService.getType(map);
		}
		return WebResult.success();
	}


	/**
	 * 获取配件、工具列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsList")
	@ResponseBody
	public WebResult getPartsList(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.getPartsList(map);
			case "3":
				return webAccountStockService.getStockList(map);
			case "4":
				return webAccountLaborService.getLaborList(map);
		}
		return WebResult.success();
	}

	/**
	 * 新增配件、工具
	 * @param
	 * @return
	 */
	@RequestMapping("/addParts")
	@ResponseBody
	public WebResult addParts(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.addParts(map);
			case "3":
				return webAccountStockService.addStock(map);
			case "4":
				return webAccountLaborService.addLabor(map);
		}
		return WebResult.success();
	}
	/**
	 * 修改配件、工具
	 * @param
	 * @return
	 */
	@RequestMapping("/updateParts")
	@ResponseBody
	public WebResult updateParts(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.updateParts(map);
			case "3":
				return webAccountStockService.updateStock(map);
			case "4":
				return webAccountLaborService.updateLabor(map);
		}
		return WebResult.success();
	}

	/**
	 * 删除配件、工具
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteParts")
	@ResponseBody
	public WebResult deleteParts(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.deleteParts(map);
			case "3":
				return webAccountStockService.deleteStock(map);
			case "4":
				return webAccountLaborService.deleteLabor(map);
		}
		return WebResult.success();
	}


	//=======================出库管理=================================//

	/**
	 * 获取所有配件、工具
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllParts")
	@ResponseBody
	public WebResult getAllParts(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.getAllParts(map);
			case "3":
				return webAccountStockService.getAllStock(map);
			case "4":
				return webAccountLaborService.getAllLabor(map);
		}
		return WebResult.success();
	}


	/**
	 * 获取出库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsOutList")
	@ResponseBody
	public WebResult getPartsOutList(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.getPartsOutList(map);
			case "3":
				return webAccountStockService.getStockOutList(map);
			case "4":
				return webAccountLaborService.getLaborOutList(map);
		}
		return WebResult.success();
	}


	/**
	 * 新增出库
	 * @param
	 * @return
	 */
	@RequestMapping("/addPartsOut")
	@ResponseBody
	public WebResult addPartsOut(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.addPartsOut(map);
			case "3":
				return webAccountStockService.addStockOut(map);
			case "4":
				return webAccountLaborService.addLaborOut(map);
		}
		return WebResult.success();
	}


	/**
	 * 修改出库
	 * @param
	 * @return
	 */
	@RequestMapping("/updatePartsOut")
	@ResponseBody
	public WebResult updatePartsOut(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.updatePartsOut(map);
			case "3":
				return webAccountStockService.updateStockOut(map);
			case "4":
				return webAccountLaborService.updateLaborOut(map);
		}
		return WebResult.success();
	}


	/**
	 * 删除出库
	 * @param
	 * @return
	 */
	@RequestMapping("/deletePartsOut")
	@ResponseBody
	public WebResult deletePartsOut(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.deletePartsOut(map);
			case "3":
				return webAccountStockService.deleteStockOut(map);
			case "4":
				return webAccountLaborService.deleteLaborOut(map);
		}
		return WebResult.success();
	}

	//==========================================入库==================================//
	/**
	 * 获取入库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsInList")
	@ResponseBody
	public WebResult getPartsInList(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.getPartsInList(map);
			case "3":
				return webAccountStockService.getStockInList(map);
			case "4":
				return webAccountLaborService.getLaborInList(map);
		}
		return WebResult.success();
	}


	/**
	 * 新增入库
	 * @param
	 * @return
	 */
	@RequestMapping("/addPartsIn")
	@ResponseBody
	public WebResult addPartsIn(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.addPartsIn(map);
			case "3":
				return webAccountStockService.addStockIn(map);
			case "4":
				return webAccountLaborService.addLaborIn(map);
		}
		return WebResult.success();
	}


	/**
	 * 修改入库
	 * @param
	 * @return
	 */
	@RequestMapping("/updatePartsIn")
	@ResponseBody
	public WebResult updatePartsIn(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.updatePartsIn(map);
			case "3":
				return webAccountStockService.updateStockIn(map);
			case "4":
				return webAccountLaborService.updateLaborIn(map);
		}
		return WebResult.success();
	}


	/**
	 * 删除入库
	 * @param
	 * @return
	 */
	@RequestMapping("/deletePartsIn")
	@ResponseBody
	public WebResult deletePartsIn(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.deletePartsIn(map);
			case "3":
				return webAccountStockService.deleteStockIn(map);
			case "4":
				return webAccountLaborService.deleteLaborIn(map);
		}
		return WebResult.success();
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/statistics")
	@ResponseBody
	public WebResult statistics(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.statistics(map);
			case "3":
				return webAccountStockService.statistics(map);
			case "4":
				return webAccountLaborService.statistics(map);
		}
		return WebResult.success();
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/outAndInRanking")
	@ResponseBody
	public WebResult outAndInRanking(@RequestAttribute Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	webAccountPartsService.outAndInRanking(map);
			case "3":
				return webAccountStockService.outAndInRanking(map);
			case "4":
				return webAccountLaborService.outAndInRanking(map);
		}
		return WebResult.success();
	}



}
