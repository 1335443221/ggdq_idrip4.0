package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.ElecReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: ElecReportController
 * Description: APP - 智用电 模块 曲线接口
 */
@Controller
@RequestMapping("/elecMonitor")
public class ElecMonitorController {
    @Autowired
    ElecReportService elecMonitorImpl;

    /**
     * 设备当日的电流电压实时监控
     * @param map
     * @return
     */
    @RequestMapping("/elecList")
    @ResponseBody
    public AppResult getElecMonitorList(@RequestParam Map<String,Object> map){
        return elecMonitorImpl.getElecMonitorList(map);
    }
}
