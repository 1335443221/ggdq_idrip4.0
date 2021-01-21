package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.SjfAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/sjfAdmin")
public class SjfAdminController {
	@Autowired
	SjfAdminService sjfAdminService;


	/**
	 * 用电查询-查询列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseEpList")
	@ResponseBody
	public AppResult getHouseEpList(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHouseEpList(map);
	}
	/**
	 * 用电查询-查询列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseEpDetail")
	@ResponseBody
	public AppResult getHouseEpDetail(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHouseEpDetail(map);
	}
	/**
	 * 缴费记录-缴费记录列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getHousePaymentRecord")
	@ResponseBody
	public AppResult getHousePaymentRecord(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHousePaymentRecord(map);
	}
	/**
	 *  缴费记录-缴费详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseRecordDetail")
	@ResponseBody
	public AppResult getHouseRecordDetail(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHouseRecordDetail(map);
	}
	/**
	 *  分户管理-分户列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseList")
	@ResponseBody
	public AppResult getHouseList(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHouseList(map);
	}
	/**
	 * 分户管理-分户类型
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseType")
	@ResponseBody
	public AppResult getHouseType(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHouseType(map);
	}
	/**
	 *分户管理-修改分户信息
	 * @param
	 * @return
	 */
	@RequestMapping("/updateHouse")
	@ResponseBody
	public AppResult updateHouse(@RequestParam Map<String, Object> map){
		return sjfAdminService.updateHouse(map);
	}
	/**
	 *分户管理-分户详情
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseDetail")
	@ResponseBody
	public AppResult getHouseDetail(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHouseDetail(map);
	}
	/**
	 *分户管理-删除分户
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteHouse")
	@ResponseBody
	public AppResult deleteHouse(@RequestParam Map<String, Object> map){
		return sjfAdminService.deleteHouse(map);
	}
	/**
	 *分户管理-新增分户
	 * @param
	 * @return
	 */
	@RequestMapping("/addHouse")
	@ResponseBody
	public AppResult addHouse(@RequestParam Map<String, Object> map){
		return sjfAdminService.addHouse(map);
	}
	/**
	 *分户管理-移动分户
	 * @param
	 * @return
	 */
	@RequestMapping("/moveHouseToType")
	@ResponseBody
	public AppResult moveHouseToType(@RequestParam Map<String, Object> map){
		return sjfAdminService.moveHouseToType(map);
	}
	/**
	 *分户管理-移动分户
	 * @param
	 * @return
	 */
	@RequestMapping("/moveAllHouseToType")
	@ResponseBody
	public AppResult moveAllHouseToType(@RequestParam Map<String, Object> map){
		return sjfAdminService.moveAllHouseToType(map);
	}
	/**
	 *分户管理-获取电表编号
	 * @param
	 * @return
	 */
	@RequestMapping("/getHouseElecMeterNumber")
	@ResponseBody
	public AppResult getHouseElecMeterNumber(@RequestParam Map<String, Object> map){
		return sjfAdminService.getHouseElecMeterNumber(map);
	}
	/**
	 *分户管理-补加电费
	 * @param
	 * @return
	 */
	@RequestMapping("/supplementFees")
	@ResponseBody
	public AppResult supplementFees(@RequestParam Map<String, Object> map){
		return sjfAdminService.supplementFees(map);
	}
/////////=======用电设置================///////////
	/**
	 *电费设置-修改峰平谷信息
	 * @param
	 * @return
	 */
	@RequestMapping("/updateFpg")
	@ResponseBody
	public AppResult updateFpg(@RequestParam Map<String, Object> map){
		return sjfAdminService.updateFpg(map);
	}
	/**
	 *电费设置-修改电费设置
	 * @param
	 * @return
	 */
	@RequestMapping("/updateElecSettingByType")
	@ResponseBody
	public AppResult updateElecSettingByType(@RequestParam Map<String, Object> map){
		return sjfAdminService.updateElecSettingByType(map);
	}
	/**
	 *电费设置-修改阶梯信息
	 * @param
	 * @return
	 */
	@RequestMapping("/updateLadder")
	@ResponseBody
	public AppResult updateLadder(@RequestParam Map<String, Object> map){
		return sjfAdminService.updateLadder(map);
	}
	/**
	 *电费设置-删除峰平谷待生效
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteFpgFuture")
	@ResponseBody
	public AppResult deleteFpgFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.deleteFpgFuture(map);
	}
	/**
	 *电费设置-删除电费待生效
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteElecSettingFuture")
	@ResponseBody
	public AppResult deleteElecSettingFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.deleteElecSettingFuture(map);
	}
	/**
	 *电费设置-删除阶梯待生效
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteLadderFuture")
	@ResponseBody
	public AppResult deleteLadderFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.deleteLadderFuture(map);
	}
	/**
	 *电费设置-新增电费设置
	 * @param
	 * @return
	 */
	@RequestMapping("/addElecSetting")
	@ResponseBody
	public AppResult addElecSetting(@RequestParam Map<String, Object> map){
		return sjfAdminService.addElecSetting(map);
	}

	/**
	 *电费设置-删除电费设置
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteElecSetting")
	@ResponseBody
	public AppResult deleteElecSetting(@RequestParam Map<String, Object> map){
		return sjfAdminService.deleteElecSetting(map);
	}


	/**
	 *电费设置-检验时段设置最新待生效时间
	 * @param
	 * @return
	 */
	@RequestMapping("/checkFpgFuture")
	@ResponseBody
	public AppResult checkFpgFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.checkFpgFuture(map);
	}
	/**
	 *电费设置-检验电费设置最新待生效时间
	 * @param
	 * @return
	 */
	@RequestMapping("/checkElecSettingFuture")
	@ResponseBody
	public AppResult checkElecSettingFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.checkElecSettingFuture(map);
	}
	/**
	 *电费设置-检验阶梯设置最新待生效时间
	 * @param
	 * @return
	 */
	@RequestMapping("/checkLadderFuture")
	@ResponseBody
	public AppResult checkLadderFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.checkLadderFuture(map);
	}
	/**
	 *电费设置-获取峰平谷信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getFpg")
	@ResponseBody
	public AppResult getFpg(@RequestParam Map<String, Object> map){
		return sjfAdminService.getFpg(map);
	}
	/**
	 *电费设置-获取峰平谷待生效信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getFpgFuture")
	@ResponseBody
	public AppResult getFpgFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.getFpgFuture(map);
	}
	/**
	 *电费设置-获取待生效信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getElecSettingByFuture")
	@ResponseBody
	public AppResult getElecSettingByFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.getElecSettingByFuture(map);
	}
	/**
	 *电费设置-获取电费设置
	 * @param
	 * @return
	 */
	@RequestMapping("/getElecSettingByType")
	@ResponseBody
	public AppResult getElecSettingByType(@RequestParam Map<String, Object> map){
		return sjfAdminService.getElecSettingByType(map);
	}
	/**
	 *电费设置-获取阶梯设置信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getLadder")
	@ResponseBody
	public AppResult getLadder(@RequestParam Map<String, Object> map){
		return sjfAdminService.getLadder(map);
	}
	/**
	 *电费设置-获取阶梯设置待生效信息
	 * @param
	 * @return
	 */
	@RequestMapping("/getLadderFuture")
	@ResponseBody
	public AppResult getLadderFuture(@RequestParam Map<String, Object> map){
		return sjfAdminService.getLadderFuture(map);
	}



	/**
	 *下置 初始化
	 * @param
	 * @return
	 */
	@RequestMapping("/setAllVal")
	@ResponseBody
	public AppResult setAllVal(@RequestParam Map<String, Object> map){
		return sjfAdminService.setAllVal(map);
	}


}
