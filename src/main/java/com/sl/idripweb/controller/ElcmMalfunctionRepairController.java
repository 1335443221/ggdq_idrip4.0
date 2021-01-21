package com.sl.idripweb.controller;

import com.sl.idripweb.service.ElcmMalfunctionRepairService;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/elcmMalfunctionRepair")
public class ElcmMalfunctionRepairController {
	@Autowired
	ElcmMalfunctionRepairService elcmMalfunctionRepairService;

    /**
     * 故障状态
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getMalfunctionStatus")
    @ResponseBody
    public WebResult getMalfunctionStatus(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.getMalfunctionStatus(map);
    }

	/**
	 * 故障列表
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getMalfunctionList")
	@ResponseBody
	public WebResult getMalfunctionList(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.getMalfunctionList(map);
	}
	/**
	 * 新增故障
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/addMalfunction")
	@ResponseBody
	public WebResult addMalfunction(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.addMalfunction(map);
	}


	/**
	 * 故障
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getMalfunction")
	@ResponseBody
	public WebResult getMalfunction(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.getMalfunction(map);
	}
	/**
	 * 处理故障
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/dealMalfunction")
	@ResponseBody
	public WebResult dealMalfunction(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.dealMalfunction(map);
	}
	/**
	 * 修改故障
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/updateMalfunction")
	@ResponseBody
	public WebResult updateMalfunction(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.updateMalfunction(map);
	}

	/**
	 * 撤销故障
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/revokeMalfunction")
	@ResponseBody
	public WebResult revokeMalfunction(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.revokeMalfunction(map);
	}
	/**
	 * 删除故障
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/deleteMalfunction")
	@ResponseBody
	public WebResult deleteMalfunction(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.deleteMalfunction(map);
	}


	//=============================维修工单==============================//

	/**
	 * 维修状态
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getRepairStatus")
	@ResponseBody
	public WebResult getRepairStatus(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.getRepairStatus(map);
	}
	/**
	 * 维修列表
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getRepairList")
	@ResponseBody
	public WebResult getRepairList(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.getRepairList(map);
	}

	/**
	 * 新增维修
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/addRepair")
	@ResponseBody
	public WebResult addRepair(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.addRepair(map);
	}


	/**
	 * 分配人员
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/assignUser")
	@ResponseBody
	public WebResult assignUser(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.assignUser(map);
	}



	/**
	 * 查看维修
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getRepair")
	@ResponseBody
	public WebResult getRepair(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.getRepair(map);
	}


    /**
     * 查看所有维修
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getRepairByIds")
    @ResponseBody
    public WebResult getRepairByIds(@RequestAttribute Map<String,Object> map){
        return  elcmMalfunctionRepairService.getRepairByIds(map);
    }


	/**
	 * 撤销
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/revokeRepair")
	@ResponseBody
	public WebResult revokeRepair(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.revokeRepair(map);
	}
	/**
	 * 我的工单
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getMyRepairList")
	@ResponseBody
	public WebResult getMyRepairList(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.getMyRepairList(map);
	}
	/**
	 * 我接单
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/receiveRepair")
	@ResponseBody
	public WebResult receiveRepair(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.receiveRepair(map);
	}
	/**
	 * 我退单
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/chargeback")
	@ResponseBody
	public WebResult chargeback(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.chargeback(map);
	}

	/**
	 * 转派
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/transfer")
	@ResponseBody
	public WebResult transfer(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.transfer(map);
	}

	/**
	 * 处理故障
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/complete")
	@ResponseBody
	public WebResult complete(@RequestAttribute Map<String,Object> map){
		return  elcmMalfunctionRepairService.complete(map);
	}

}
