package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface WebSjfReportService {

    //获取厂区楼宇树形列表
    public WebResult getCategoryTree(Map<String, Object> map);

    //通过厂区楼宇树形列表获取所有分户信息
    public WebResult getHousesByTree(Map<String, Object> map);

    //电费报表
    public WebResult chargeReport(Map<String, Object> map);

    //电费报表-》下载
    public void chargeReportDownload(Map<String, Object> map, HttpServletResponse response);

    //电量报表
    public WebResult powerReport(Map<String, Object> map);

    //电量报表-》下载
    public void powerReportDownload(Map<String, Object> map, HttpServletResponse response);

    //财务报表
    public WebResult financeReport(Map<String, Object> map);

    //财务报表-》下载
    public void financeReportDownload(Map<String, Object> map, HttpServletResponse response);

    //获取首页数据
    public WebResult getIndexData(Map<String, Object> map);

    //首页根据选中分时用户获取峰平谷电费电度统计
    public WebResult getFpgData(Map<String, Object> map);

    //获取某个阶梯下的用户、用电量和缴费金额
    public WebResult getHouseByLadder(Map<String, Object> map);

    //获取分户分组下的电费和用电量
    public WebResult getPowerAndFeesOfHouse(Map<String, Object> map);

}
