package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountUserService {


    //#####################人员台账###############################
    //获取用户台账所有公司
    public AppResult getAllCompany(Map<String,Object> map);

    //获取用户台账所有部门
    public AppResult getAllDepartment(Map<String,Object> map);

    //获取用户台账所有角色
    public AppResult getAllRoles(Map<String,Object> map);

    //获取人员台账列表数据
    public AppResult getUserList(Map<String,Object> map);

    //获取人员台账详情数据
    public AppResult getUserDetail(Map<String,Object> map);

    //人员台账编辑
    public AppResult updateUser(Map<String,Object> map);


    //#####################提醒事项###############################
    //提醒事项列表查询
    public AppResult getReminderList(Map<String,Object> map);

    //修改提醒事项
    public AppResult updateReminder(Map<String,Object> map);

    //删除提醒事项
    public AppResult deleteReminder(Map<String,Object> map);

}
