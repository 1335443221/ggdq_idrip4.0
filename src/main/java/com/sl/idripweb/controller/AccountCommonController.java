package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.AccountCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 台账管理部分通用接口，大部分是字典表
 */
@RestController
@RequestMapping("accountCommon")
public class AccountCommonController {

    @Autowired
    AccountCommonService commonService;


    //#####################人员台账###############################
    /**
     * 获取用户台账民族信息
     * @return
     */
    @GetMapping("getUserNation")
    public WebResult getUserNation(){
        return commonService.getUserNation();
    }

    /**
     * 获取用户台账人员类型
     * @return
     */
    @GetMapping("getUserType")
    public WebResult getUserType(){
        return commonService.getUserType();
    }

    /**
     * 获取用户台账政治面貌
     * @return
     */
    @GetMapping("getUserPoliticalOutlook")
    public WebResult getUserPoliticalOutlook(){
        return commonService.getUserPoliticalOutlook();
    }

    /**
     * 获取用户台账婚姻状况
     * @return
     */
    @GetMapping("getUserMaritalStatus")
    public WebResult getUserMaritalStatus(){
        return commonService.getUserMaritalStatus();
    }

    /**
     * 获取用户台账最高学历
     * @return
     */
    @GetMapping("getUserHighestEducation")
    public WebResult getUserHighestEducation(){
        return commonService.getUserHighestEducation();
    }




    //#####################培训台账###############################

    /**
     * 获取培训台账学习成绩
     * @return
     */
    @GetMapping("getTrainAchievement")
    public WebResult getTrainAchievement(){
        return commonService.getTrainAchievement();
    }




    //#####################合同台账###############################
    /**
     * 获取所有合同类型
     * @return
     */
    @GetMapping("getContractType")
    public WebResult getContractType(){
        return commonService.getContractType();
    }

    /**
     * 获取所有到期提醒时间
     * @return
     */
    @GetMapping("getRemindDate")
    public WebResult getRemindDate(){
        return commonService.getRemindDate();
    }

    /**
     * 获取所有提醒频率
     * @return
     */
    @GetMapping("getRemindFrequency")
    public WebResult getRemindFrequency(@RequestParam Map<String, Object> map){
        return commonService.getRemindFrequency(map);
    }



    //#####################供应商台账###############################
    /**
     * 获取所有信誉等级
     * @return
     */
    @GetMapping("getCreditRating")
    public WebResult getCreditRating(@RequestParam Map<String, Object> map){
        return commonService.getCreditRating(map);
    }
}
