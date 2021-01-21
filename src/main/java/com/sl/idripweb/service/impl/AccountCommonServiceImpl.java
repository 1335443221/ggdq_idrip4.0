package com.sl.idripweb.service.impl;

import com.sl.common.config.AccountDictionary;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.AccountCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountCommonServiceImpl implements AccountCommonService {

    @Autowired
    private AccountDictionary accountDictionary;


    //#####################人员台账###############################
    //获取用户台账民族信息
    @Override
    public WebResult getUserNation() {
        List<String> userNation = accountDictionary.getUserNation();
        return handleList(userNation);
    }

    //获取人员类型
    @Override
    public WebResult getUserType() {
        List<String> userNation = accountDictionary.getUserType();
        return handleList(userNation);
    }

    //获取用户台账政治面貌
    @Override
    public WebResult getUserPoliticalOutlook() {
        List<String> userNation = accountDictionary.getUserPoliticalOutlook();
        return handleList(userNation);
    }

    //获取用户台账婚姻状况
    @Override
    public WebResult getUserMaritalStatus() {
        List<String> userNation = accountDictionary.getUserMaritalStatus();
        return handleList(userNation);
    }

    //获取用户台账最高学历
    @Override
    public WebResult getUserHighestEducation() {
        List<String> userNation = accountDictionary.getUserHighestEducation();
        return handleList(userNation);
    }



    //#####################培训台账###############################
    //获取培训台账学习成绩
    @Override
    public WebResult getTrainAchievement() {
        List<String> userNation = accountDictionary.getTrainAchievement();
        return handleList(userNation);
    }



    //#####################合同台账###############################
    //获取所有合同类型
    @Override
    public WebResult getContractType() {
        List<String> userNation = accountDictionary.getContractType();
        return handleList(userNation);
    }

    //获取所有到期提醒时间
    @Override
    public WebResult getRemindDate() {
        List<String> userNation = accountDictionary.getRemindDate();
        return handleList(userNation);
    }

    //获取所有提醒频率
    @Override
    public WebResult getRemindFrequency(Map<String, Object> map) {
//        if(map.get("remindDate") == null) return WebResult.success(new ArrayList<>());
        List<String> remindDates = accountDictionary.getRemindDate();
        List<String> remindFrequency = new ArrayList<>(accountDictionary.getRemindFrequency());
        String remindDate = String.valueOf(map.get("remindDate"));
        if(remindDates.get(0).equals(remindDate) || remindDates.get(1).equals(remindDate) || remindDates.get(2).equals(remindDate))
            remindFrequency.remove(remindFrequency.get(remindFrequency.size() - 1));
        return handleList(remindFrequency);
    }



    //#####################供应商台账###############################
    //获取所有信誉等级
    @Override
    public WebResult getCreditRating(Map<String, Object> map) {
        List<String> userNation = accountDictionary.getCreditRating();
        return handleList(userNation);
    }

    //统一下拉框结果集处理方法
    private WebResult handleList(List<String> userNation) {
        List<Map<String, String>> resultList = new ArrayList<>();
        userNation.stream().forEach(each -> {
            Map<String, String> map = new HashMap<>();
            map.put("label", each);
            map.put("value", each);
            resultList.add(map);
        });
        return WebResult.success(resultList);
    }
}
