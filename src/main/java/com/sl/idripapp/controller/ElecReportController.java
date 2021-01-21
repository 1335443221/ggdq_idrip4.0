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
 * Description: APP - 智用电 模块 报表接口
 */
@Controller
@RequestMapping("/elec")
public class ElecReportController {
    @Autowired
    ElecReportService elecReportImpl;


    /**
     * 一个项目的所有厂区+配电
     *
     * @param map
     * @return
     */
    @RequestMapping("/get_project_factorys")
    @ResponseBody
    public AppResult getProjectFactorys(@RequestParam Map<String, Object> map) {
        return elecReportImpl.getProjectFactorys(map);
    }


    /**
     * 单日报表列表
     */
    @RequestMapping("/singleDay_list")
    @ResponseBody
    public AppResult singleDayList(@RequestParam Map<String, Object> map) {
        return elecReportImpl.singleDayList(map);
    }

    /**
     * 区间报表列表
     */
    @RequestMapping("/section_list")
    @ResponseBody
    public AppResult sectionList(@RequestParam Map<String, Object> map) {
        return elecReportImpl.sectionList(map);
    }

    /**
     * 单日报表详情
     */
    @RequestMapping("/singleDay_detail")
    @ResponseBody
    public AppResult singleDayDetail(@RequestParam Map<String, Object> map) {
        return elecReportImpl.singleDayDetail(map);
    }

    /**
     * 区间报表详情
     */
    @RequestMapping("/section_detail")
    @ResponseBody
    public AppResult sectionDetail(@RequestParam Map<String, Object> map) {
        return elecReportImpl.sectionDetail(map);
    }

    /**
     * 出线列表单日
     */
    @RequestMapping("/singleDay_Coilin_sort")
    @ResponseBody
    public AppResult singleDayCoilinSort(@RequestParam Map<String, Object> map) {

        return elecReportImpl.singleDayCoilinSort(map);
    }


    /**
     * 出线列表区间
     */
    @RequestMapping("/section_Coilin_sort")
    @ResponseBody
    public AppResult sectionCoilinSort(@RequestParam Map<String, Object> map) {

        return elecReportImpl.sectionCoilinSort(map);
    }

}
