package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.SjfUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/sjfUser")
public class SjfUserController {
	@Autowired
	SjfUserService sjfService;


	/**
	 * 通用-获取厂区/楼列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getFactoryList")
	@ResponseBody
	public AppResult getFactoryList(@RequestParam Map<String, Object> map){
		return sjfService.getFactoryList(map);
	}

	/**
	 * 缴费-获取收费单位
	 * @param
	 * @return
	 */
	@PostMapping("/getChargeUnit")
	@ResponseBody
	public AppResult getChargeUnit(@RequestParam Map<String, Object> map){
		return sjfService.getChargeUnit(map);
	}

	/**
	 * 分户管理-验证分户是否存在
	 * @param
	 * @return
	 */
	@RequestMapping("/checkHouseNumber")
	@ResponseBody
	public AppResult checkHouseNumber(@RequestParam Map<String, Object> map){
		return sjfService.checkHouseNumber(map);
	}

	/**
	 * 分户管理-获取分户分组列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getGroupList")
	@ResponseBody
	public AppResult getGroupList (@RequestParam Map<String, Object> map){
		return sjfService.getGroupList(map);
	}

	/**
	 * 分户管理-缴费分户列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getUserHouseList")
	@ResponseBody
	public AppResult getUserHouseList(@RequestParam Map<String, Object> map){
		return sjfService.getUserHouseList(map);
	}

	/**
	 * 分户管理-新增分户分组
	 * @param
	 * @return
	 */
	@RequestMapping("/addGroup")
	@ResponseBody
	public AppResult addGroup(@RequestParam Map<String, Object> map){
		return sjfService.addGroup(map);
	}
	/**
	 * 分户管理-编辑分组信息
	 * @param
	 * @return
	 */
	@RequestMapping("/updateGroup")
	@ResponseBody
	public AppResult updateGroup(@RequestParam Map<String, Object> map){
		return sjfService.updateGroup(map);
	}
	/**
	 * 分户管理-删除分组
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteGroup")
	@ResponseBody
	public AppResult deleteGroup(@RequestParam Map<String, Object> map){
		return sjfService.deleteGroup(map);
	}
	/**
	 *  分户管理-删除户号
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteHouse")
	@ResponseBody
	public AppResult deleteHouse(@RequestParam Map<String, Object> map){
		return sjfService.deleteHouse(map);
	}
	/**
	 *  分户管理-修改分户户号
	 * @param
	 * @return
	 */
	@RequestMapping("/updateHouse")
	@ResponseBody
	public AppResult updateHouse(@RequestParam Map<String, Object> map){
		return sjfService.updateHouse(map);
	}

	/**
	 * 余额-分户余额列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseBalance")
	@ResponseBody
	public AppResult getHouseBalance(@RequestParam Map<String, Object> map){
		return sjfService.getHouseBalance(map);
	}

	/**
	 * 询-查询列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseEpList")
	@ResponseBody
	public AppResult getHouseEpList(@RequestParam Map<String, Object> map){
		return sjfService.getHouseEpList(map);
	}

	/**
	 *  询-用电详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseDetail")
	@ResponseBody
	public AppResult getHouseDetail(@RequestParam Map<String, Object> map){
		return sjfService.getHouseDetail(map);
	}
	/**
	 *  缴费记录-缴费记录列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getHousePaymentRecord")
	@ResponseBody
	public AppResult getHousePaymentRecord(@RequestParam Map<String, Object> map){
		return sjfService.getHousePaymentRecord(map);
	}
	/**
	 * 缴费记录-缴费记录詳情
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseRecordDetail")
	@ResponseBody
	public AppResult getHouseRecordDetail(@RequestParam Map<String, Object> map){
		return sjfService.getHouseRecordDetail(map);
	}
	/**
	 * 缴费-获取缴费用户信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getChargeHouseDetail")
	@ResponseBody
	public AppResult getChargeHouseDetail(@RequestParam Map<String, Object> map){
		return sjfService.getChargeHouseDetail(map);
	}

}
