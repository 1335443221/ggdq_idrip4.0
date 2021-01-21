package com.sl.idripapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AlermDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: AlermController
 * Description: APP - 报警模块 曲线接口
 */
@Controller
@RequestMapping("/alerm")
public class AlermController {
	@Autowired
	AlermDataService alermDataImpl;


	/**
	 * 报警列表接口
	 * @param
	 * @return
	 */
	@RequestMapping("/alermList")
	@ResponseBody
	public AppResult alermList(@RequestParam Map<String, Object> map){
		return alermDataImpl.alermList(map);
	}


	/**
	 * 报警详情接口
	 * @param
	 * @return
	 */
	@RequestMapping("/alermDetail")
	@ResponseBody
	public AppResult alermDetail(@RequestParam Map<String, Object> map){
		return alermDataImpl.alermDetail(map);
	}


	/**
	 * 报警处理接口
	 * @param
	 * @return
	 */
	@RequestMapping("/alermDispose")
	@ResponseBody
	public AppResult alermDispose(@RequestParam Map<String, Object> map){

		return alermDataImpl.alermDispose(map);
	}

	//===========fire 火灾报警!!!!!===============//

	/**
	 * 报警-报警列表
	 * @param
	 * @return
	 */
	@RequestMapping("/fireAlermList")
	@ResponseBody
	public AppResult fireAlermList(@RequestParam Map<String, Object> map){
		return alermDataImpl.fireAlermList(map);
	}

	/**
	 * 报警-火灾报警处理
	 * @param
	 * @return
	 */
	@RequestMapping("/fireAlermDispose")
	@ResponseBody
	public AppResult fireAlermDispose(@RequestParam Map<String, Object> map){
		return alermDataImpl.fireAlermDispose(map);
	}

	/**
	 * 报警-火灾报警等级列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getAlermLevel")
	@ResponseBody
	public AppResult getAlermLevel(@RequestParam Map<String, Object> map){
		return alermDataImpl.getAlermLevel(map);
	}

	/**
	 * 报警-火灾报警详情
	 * @param
	 * @return
	 */
	@RequestMapping("/fireAlermDetail")
	@ResponseBody
	public AppResult fireAlermDetail(@RequestParam Map<String, Object> map){
		return alermDataImpl.fireAlermDetail(map);
	}

	/**
	 * 报警-火灾报警类型
	 * @param
	 * @return
	 */
	@RequestMapping("/getFireCategoryRelation")
	@ResponseBody
	public AppResult getFireCategoryRelation(@RequestParam Map<String, Object> map){
		return alermDataImpl.getFireCategoryRelation(map);
	}


	/**
	 * 报警-火灾标签列表
	 * @param
	 * @return
	 */
	@RequestMapping("/getFireTagByDeviceId")
	@ResponseBody
	public AppResult getFireTagByDeviceId(@RequestParam Map<String, Object> map){
		return alermDataImpl.getFireTagByDeviceId(map);
	}

	/**
	 * 报警-火灾报警配置新增
	 * @param
	 * @return
	 */
	@RequestMapping(value="/fireAlConfSave",method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public AppResult fireAlConfSave(@RequestBody JSONObject json){
		Map map= JSONObject.parseObject(json.toJSONString(),Map.class);
		map.put("parent_cate_id",4);
		return alermDataImpl.fireAlConfSave(map);
	}
	/**
	 * test
	 * @param
	 * @return
	 */
	@RequestMapping(value="/test",method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public JSONObject test(@RequestBody JSONObject json){
		System.out.println(json);
		return json;
	}


	/**
	 * 报警-火灾报警配置修改
	 * @param
	 * @return
	 */
	@RequestMapping("/fireAlConfUpdate")
	@ResponseBody
	public AppResult fireAlConfUpdate(@RequestParam Map<String, Object> map){
		return alermDataImpl.fireAlConfUpdate(map);
	}

	/**
	 * 报警-火灾报警配置信息
	 * @param
	 * @return
	 */
	@RequestMapping("/fireAlConfData")
	@ResponseBody
	public AppResult fireAlConfData(@RequestParam Map<String, Object> map){
		return alermDataImpl.fireAlConfData(map);
	}



}
