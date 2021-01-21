package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/report")
public class ReportController{
    @Autowired
    private ReportService reportService;

    /**
     * 报表数据
     * @param map
     * @param request
     * @return
     */
    @RequestMapping("/queryv3")
    @ResponseBody
    public WebResult selectByDate(@RequestParam Map<String,Object> map, HttpServletRequest request){
        return reportService.selectByDate(request,map);
    }

}
