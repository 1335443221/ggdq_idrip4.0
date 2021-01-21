package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountLaborService;
import com.sl.idripapp.service.AccountPartsService;
import com.sl.idripapp.service.AccountStockService;
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
 * FileName: AccountPartsController
 * Description: 1配件   2工具  3库存  4劳保
 */
@Controller
@RequestMapping("/accountParts")
public class AccountPartsController {
	@Autowired
	AccountPartsService accountPartsService;
	@Autowired
	AccountStockService accountStockService;
	@Autowired
	AccountLaborService accountLaborService;


	/**
	 * 1配件、2工具、3库存、4劳保列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsList")
	@ResponseBody
	public AppResult getPartsList(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.getPartsList(map);
			case "3":
				return accountStockService.getStockList(map);
			case "4":
				return accountLaborService.getLaborList(map);
		}
		return null;
	}

	/**
	 * 出库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsOutList")
	@ResponseBody
	public AppResult getPartsOutList(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.getPartsOutList(map);
			case "3":
				return accountStockService.getStockOutList(map);
			case "4":
				return accountLaborService.getLaborOutList(map);
		}
		return null;
	}


	/**
	 * 所有配件、工具列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllParts")
	@ResponseBody
	public AppResult getAllParts(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.getAllParts(map);
			case "3":
				return accountStockService.getAllStock(map);
			case "4":
				return accountLaborService.getAllLabor(map);
		}
		return null;
	}


	/**
	 * 新增出库
	 * @param
	 * @return
	 */
	@RequestMapping("/addPartsOut")
	@ResponseBody
	public AppResult addPartsOut(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.addPartsOut(map);
			case "3":
				return accountStockService.addStockOut(map);
			case "4":
				return accountLaborService.addLaborOut(map);
		}
		return null;
	}



	/**
	 * 修改出库
	 * @param
	 * @return
	 */
	@RequestMapping("/updatePartsOut")
	@ResponseBody
	public AppResult updatePartsOut(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.updatePartsOut(map);
			case "3":
				return accountStockService.updateStockOut(map);
			case "4":
				return accountLaborService.updateLaborOut(map);
		}
		return null;
	}


	/**
	 * 删除出库
	 * @param
	 * @return
	 */
	@RequestMapping("/deletePartsOut")
	@ResponseBody
	public AppResult deletePartsOut(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.deletePartsOut(map);
			case "3":
				return accountStockService.deleteStockOut(map);
			case "4":
				return accountLaborService.deleteLaborOut(map);
		}
		return null;
	}

	/**
	 * 出库详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsOutDetail")
	@ResponseBody
	public AppResult getPartsOutDetail(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.getPartsOutDetail(map);
			case "3":
				return accountStockService.getStockOutDetail(map);
			case "4":
				return accountLaborService.getLaborOutDetail(map);
		}
		return null;
	}

	//==========================================入库==================================//
	/**
	 * 获取入库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsInList")
	@ResponseBody
	public AppResult getPartsInList(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.getPartsInList(map);
			case "3":
				return accountStockService.getStockInList(map);
			case "4":
				return accountLaborService.getLaborInList(map);
		}
		return null;
	}


	/**
	 * 新增入库
	 * @param
	 * @return
	 */
	@RequestMapping("/addPartsIn")
	@ResponseBody
	public AppResult addPartsIn(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.addPartsIn(map);
			case "3":
				return accountStockService.addStockIn(map);
			case "4":
				return accountLaborService.addLaborIn(map);
		}
		return null;
	}


	/**
	 * 修改入库
	 * @param
	 * @return
	 */
	@RequestMapping("/updatePartsIn")
	@ResponseBody
	public AppResult updatePartsIn(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.updatePartsIn(map);
			case "3":
				return accountStockService.updateStockIn(map);
			case "4":
				return accountLaborService.updateLaborIn(map);
		}
		return null;
	}


	/**
	 * 删除入库
	 * @param
	 * @return
	 */
	@RequestMapping("/deletePartsIn")
	@ResponseBody
	public AppResult deletePartsIn(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.deletePartsIn(map);
			case "3":
				return accountStockService.deleteStockIn(map);
			case "4":
				return accountLaborService.deleteLaborIn(map);
		}
		return null;
	}

	/**
	 * 入库详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getPartsInDetail")
	@ResponseBody
	public AppResult getPartsInDetail(@RequestParam Map<String, Object> map){
		String module=String.valueOf(map.get("module"));
		switch (module){
			case "1":
			case "2":
				return	accountPartsService.getPartsInDetail(map);
			case "3":
				return accountStockService.getStockInDetail(map);
			case "4":
				return accountLaborService.getLaborInDetail(map);
		}
		return null;
	}
}
