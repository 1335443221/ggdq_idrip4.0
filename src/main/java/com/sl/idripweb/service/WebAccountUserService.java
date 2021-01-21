package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountUserService {


    //#####################人员台账###############################
    //获取用户台账所有公司
    public WebResult getAllCompany(Map<String,Object> map);

    //获取用户台账所有部门
    public WebResult getAllDepartment(Map<String,Object> map);

    //获取用户台账所有角色
    public WebResult getAllRoles(Map<String,Object> map);

    //获取用户台账所有标签
    public WebResult getAllLabel(Map<String,Object> map);

    //标签新增
    public WebResult addLabel(Map<String,Object> map);

    //标签删除
    public WebResult deleteLabel(Map<String,Object> map);

    //标签验证是否被人员使用
    public WebResult checkLabel(Map<String,Object> map);

    //给人员关联标签
    public WebResult relationLabel(Map<String,Object> map);

    //获取人员台账列表数据
    public WebResult getUserList(Map<String,Object> map);

    //维保台账维保人员列表
    public WebResult getMaintenanceUserList(Map<String,Object> map);

    //获取人员台账详情数据
    public WebResult getUserDetail(Map<String,Object> map);

    //人员台账新增
    public WebResult addUser(Map<String,Object> map);

    //人员台账删除
    public WebResult deleteUser(Map<String,Object> map);

    //人员台账编辑
    public WebResult updateUser(Map<String,Object> map);


    //#####################提醒事项###############################
    //提醒事项列表查询
    public WebResult getReminderList(Map<String,Object> map);

    //根据id获取单个提醒事项
    public WebResult getReminderById(Map<String,Object> map);

    //获取公司部门下的人员
    public WebResult getReminderUsers(Map<String,Object> map);

    //新增提醒事项
    public WebResult addReminder(Map<String,Object> map);

    //修改提醒事项
    public WebResult updateReminder(Map<String,Object> map);

    //删除提醒事项
    public WebResult deleteReminder(Map<String,Object> map);



    //#####################数据统计###############################
    //数据统计页面大接口
    public WebResult statistics(Map<String,Object> map);

}
