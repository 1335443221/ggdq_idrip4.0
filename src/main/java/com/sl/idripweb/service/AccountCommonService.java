package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface AccountCommonService {


    //#####################人员台账###############################
    //获取用户台账民族信息
    public WebResult getUserNation();

    //获取人员类型
    public WebResult getUserType();

    //获取用户台账政治面貌
    public WebResult getUserPoliticalOutlook();

    //获取用户台账婚姻状况
    public WebResult getUserMaritalStatus();

    //获取用户台账最高学历
    public WebResult getUserHighestEducation();


    //#####################培训台账###############################
    //获取培训台账学习成绩
    public WebResult getTrainAchievement();


    //#####################合同台账###############################
    //获取所有合同类型
    public WebResult getContractType();

    //获取所有到期提醒时间
    public WebResult getRemindDate();

    //获取所有提醒频率
    public WebResult getRemindFrequency(Map<String, Object> map);



    //#####################供应商台账###############################
    //获取所有信誉等级
    public WebResult getCreditRating(Map<String, Object> map);

}
