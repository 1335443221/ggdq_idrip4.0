package com.sl.idripweb.controller;

import com.sl.idripweb.service.ElcmApprovalService;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/elcmApproval")
public class ElcmApprovalController {
	@Autowired
	ElcmApprovalService elcmApprovalService;

    /**
     * 审批类型
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getApprovalType")
    @ResponseBody
    public WebResult getApprovalType(@RequestAttribute Map<String,Object> map){
        return  elcmApprovalService.getApprovalType(map);
    }

    /**
     * 待审批列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getPendingApprovalList")
    @ResponseBody
    public WebResult getPendingApprovalList(@RequestAttribute Map<String,Object> map){
        return  elcmApprovalService.getPendingApprovalList(map);
    }

    /**
     * 已审批列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getApprovedList")
    @ResponseBody
    public WebResult getApprovedList(@RequestAttribute Map<String,Object> map){
        return  elcmApprovalService.getApprovedList(map);
    }


	/**
	 * 查看审批
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getApproveDetail")
	@ResponseBody
	public WebResult getApproveDetail(@RequestAttribute Map<String,Object> map){
		return  elcmApprovalService.getApproveDetail(map);
	}


	/**
	 * 审批
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/approve")
	@ResponseBody
	public WebResult approve(@RequestAttribute Map<String,Object> map){
		return  elcmApprovalService.approve(map);
	}



}
