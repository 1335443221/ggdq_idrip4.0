package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebSjfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/webSjfReport")
public class WebSjfReportController {

    @Autowired
    private WebSjfReportService webSjfReportService;

    /**
     * 获取厂区楼宇树形列表
     * @param map
     * @return
     */
    @RequestMapping("getCategoryTree")
    @ResponseBody
    public WebResult getCategoryTree(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.getCategoryTree(map);
    }

    /**
     * 通过厂区楼宇树形列表获取所有分户信息
     * @param map
     * @return
     */
    @RequestMapping("getHousesByTree")
    @ResponseBody
    public WebResult getHousesByTree(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.getHousesByTree(map);
    }

    /**
     * 电费报表
     * @param map
     * @return
     */
    @RequestMapping("chargeReport")
    @ResponseBody
    public WebResult chargeReport(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.chargeReport(map);
    }

    /**
     * 电费报表-》下载
     * @param map
     * @return
     */
    @RequestMapping("chargeReportDownload")
    @ResponseBody
    public void chargeReportDownload(HttpServletResponse response, @RequestAttribute Map<String, Object> map){
        webSjfReportService.chargeReportDownload(map, response);
    }

    /**
     * 电量报表
     * @param map
     * @return
     */
    @RequestMapping("powerReport")
    @ResponseBody
    public WebResult powerReport(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.powerReport(map);
    }

    /**
     * 电量报表-》下载
     * @param map
     * @return
     */
    @RequestMapping("powerReportDownload")
    @ResponseBody
    public void powerReportDownload(HttpServletResponse response, @RequestAttribute Map<String, Object> map){
        webSjfReportService.powerReportDownload(map, response);
    }

    /**
     * 财务报表
     * @param map
     * @return
     */
    @RequestMapping("financeReport")
    @ResponseBody
    public WebResult financeReport(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.financeReport(map);
    }

    /**
     * 财务报表->下载
     * @param map
     * @return
     */
    @RequestMapping("financeReportDownload")
    @ResponseBody
    public void financeReportDownload(HttpServletResponse response, @RequestAttribute Map<String, Object> map){
        webSjfReportService.financeReportDownload(map, response);
    }

    /**
     * 获取首页数据
     * @param map
     * @return
     */
    @RequestMapping("index")
    @ResponseBody
    public WebResult getIndexData(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.getIndexData(map);
    }

    /**
     * 首页根据选中分时用户获取峰平谷电费电度统计
     * @param map
     * @return
     */
    @RequestMapping("getFpgData")
    @ResponseBody
    public WebResult getFpgData(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.getFpgData(map);
    }

    /**
     * 获取某个阶梯下的用户、用电量和缴费金额
     * @param map
     * @return
     */
    @RequestMapping("getHouseByLadder")
    @ResponseBody
    public WebResult getHouseByLadder(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.getHouseByLadder(map);
    }

    /**
     * 获取分户分组下的电费和用电量
     * @param map
     * @return
     */
    @RequestMapping("getPowerAndFeesOfHouse")
    @ResponseBody
    public WebResult getPowerAndFeesOfHouse(@RequestAttribute Map<String, Object> map){
        return webSjfReportService.getPowerAndFeesOfHouse(map);
    }
}
