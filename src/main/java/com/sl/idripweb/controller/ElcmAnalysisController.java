package com.sl.idripweb.controller;

import com.sl.idripweb.service.ElcmAnalysisService;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/elcmAnalysis")
public class ElcmAnalysisController {
	@Autowired
	ElcmAnalysisService elcmAnalysisService;

    /**
     * 故障分析首页
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/malAnalysisIndex")
    @ResponseBody
    public WebResult malAnalysisIndex(@RequestAttribute Map<String,Object> map){
		return  elcmAnalysisService.malAnalysisIndex(map);
    }

	/**
	 * 故障分析 折线图
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/malCountByCycle")
	@ResponseBody
	public WebResult malCountByCycle(@RequestAttribute Map<String,Object> map){
		return  elcmAnalysisService.malCountByCycle(map);
	}


}
