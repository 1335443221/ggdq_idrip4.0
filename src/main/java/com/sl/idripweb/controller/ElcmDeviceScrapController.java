package com.sl.idripweb.controller;

import com.sl.idripweb.service.ElcmDeviceScrapService;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/elcmDeviceScrap")
public class ElcmDeviceScrapController {
	@Autowired
    ElcmDeviceScrapService elcmDeviceScrapService;


	/**
	 * 报废状态查询
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getScrapStatus")
	@ResponseBody
	public WebResult getScrapStatus(@RequestAttribute Map<String,Object> map){

		return  elcmDeviceScrapService.getScrapStatus(map);
	}

    /**
     * 报废列表查询
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getScrapList")
    @ResponseBody
    public WebResult getScrapList(@RequestAttribute Map<String,Object> map){

        return  elcmDeviceScrapService.getScrapList(map);
    }


	/**
	 * 撤销申请
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/revokeApply")
	@ResponseBody
	public WebResult revokeApply(@RequestAttribute Map<String,Object> map){

		return  elcmDeviceScrapService.revokeApply(map);
	}

	/**
	 * 再申请
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/reapply")
	@ResponseBody
	public WebResult reapply(@RequestAttribute Map<String,Object> map){

		return  elcmDeviceScrapService.reapply(map);
	}
	/**
	 * 删除
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/deleteScrap")
	@ResponseBody
	public WebResult deleteScrap(@RequestAttribute Map<String,Object> map){
		return  elcmDeviceScrapService.deleteScrap(map);
	}

	/**
	 * 新增
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/addScrap")
	@ResponseBody
	public WebResult addScrap(@RequestAttribute Map<String,Object> map){
		return  elcmDeviceScrapService.addScrap(map);
	}

	/**
	 * 修改
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/updateScrap")
	@ResponseBody
	public WebResult updateScrap(@RequestAttribute Map<String,Object> map){
		return  elcmDeviceScrapService.updateScrap(map);
	}

	/**
	 * 查看
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getScrapDetail")
	@ResponseBody
	public WebResult getScrapDetail(@RequestAttribute Map<String,Object> map){
		return  elcmDeviceScrapService.getScrapDetail(map);
	}

}
