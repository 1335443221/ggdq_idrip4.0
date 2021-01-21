package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountLaborService;
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
 * FileName: AccountLaborController
 * Description: 劳保台账
 */
@Controller
@RequestMapping("/webAccountLabor")
public class WebAccountLaborController {
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
		return webAccountLaborService.getType(map);
	}


	/**
	 * 获取劳保列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborList")
	@ResponseBody
	public WebResult getLaborList(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.getLaborList(map);
	}

	/**
	 * 新增劳保
	 * @param
	 * @return
	 */
	@RequestMapping("/addLabor")
	@ResponseBody
	public WebResult addLabor(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.addLabor(map);
	}
	/**
	 * 修改劳保
	 * @param
	 * @return
	 */
	@RequestMapping("/updateLabor")
	@ResponseBody
	public WebResult updateLabor(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.updateLabor(map);
	}

	/**
	 * 删除劳保
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteLabor")
	@ResponseBody
	public WebResult deleteLabor(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.deleteLabor(map);
	}


	//=======================出库管理=================================//

	/**
	 * 获取所有劳保
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllLabor")
	@ResponseBody
	public WebResult getAllLabor(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.getAllLabor(map);
	}


	/**
	 * 获取出库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborOutList")
	@ResponseBody
	public WebResult getLaborOutList(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.getLaborOutList(map);
	}


	/**
	 * 新增出库
	 * @param
	 * @return
	 */
	@RequestMapping("/addLaborOut")
	@ResponseBody
	public WebResult addLaborOut(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.addLaborOut(map);
	}


	/**
	 * 修改出库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateLaborOut")
	@ResponseBody
	public WebResult updateLaborOut(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.updateLaborOut(map);
	}


	/**
	 * 删除出库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteLaborOut")
	@ResponseBody
	public WebResult deleteLaborOut(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.deleteLaborOut(map);
	}

	//==========================================入库==================================//
	/**
	 * 获取入库列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getLaborInList")
	@ResponseBody
	public WebResult getLaborInList(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.getLaborInList(map);
	}


	/**
	 * 新增入库
	 * @param
	 * @return
	 */
	@RequestMapping("/addLaborIn")
	@ResponseBody
	public WebResult addLaborIn(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.addLaborIn(map);
	}


	/**
	 * 修改入库
	 * @param
	 * @return
	 */
	@RequestMapping("/updateLaborIn")
	@ResponseBody
	public WebResult updateLaborIn(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.updateLaborIn(map);
	}


	/**
	 * 删除入库
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteLaborIn")
	@ResponseBody
	public WebResult deleteLaborIn(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.deleteLaborIn(map);
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/statistics")
	@ResponseBody
	public WebResult statistics(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.statistics(map);
	}


	/**
	 * 数据统计
	 * @param
	 * @return
	 */
	@RequestMapping("/outAndInRanking")
	@ResponseBody
	public WebResult outAndInRanking(@RequestAttribute Map<String, Object> map){
		return webAccountLaborService.outAndInRanking(map);
	}



}
