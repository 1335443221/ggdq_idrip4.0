package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountUserDao {

    //#####################人员台账###############################
    //获取所有公司
    List<Map<String, Object>> getAllCompany(Map<String, Object> map);

    //获取所有部门
    List<Map<String, Object>> getAllDepartment(Map<String, Object> map);

    //获取所有角色
    List<Map<String, Object>> getAllRoles(Map<String, Object> map);

    //获取人员台账列表数据
    List<Map<String, Object>> getUserList(Map<String, Object> map);

    //获取人员台账列表--count
    int getUserListCount(Map<String, Object> map);

    //根据台账用户id获取用户下所有标签
    List<Map<String, Object>> getLabelsByUserId(Map<String, Object> map);

    //根据台账用户id获取用户下所有技能证书
    List<Map<String, Object>> getCertificatesByUserId(Map<String, Object> map);

    //通过app user获取user
    Map<String, Object> getAppUserById(Map<String, Object> map);

    //获取人员台账人员详情
    Map<String, Object> getUserDetail(Map<String, Object> map);

    //修改用户角色关联表
    int updateUserRoleRelation(Map<String, Object> map);

    //修改用户项目关联表
    int updateUserProjectRelation(Map<String, Object> map);

    //修改web 用户表
    int updateWebUser(Map<String, Object> map);

    //修改account 用户表
    int updateAccountUser(Map<String, Object> map);

    //修改app 用户表
    int updateAppUser(Map<String, Object> map);




    //#####################提醒事项###############################
    //获取提醒事项列表数据
    List<Map<String, Object>> getReminderList(Map<String, Object> map);

    //获取提醒事项列表--count
    int getReminderListCount(Map<String, Object> map);

    //修改提醒事项
    int updateReminder(Map<String, Object> map);

    //删除提醒事项
    int deleteReminder(Map<String, Object> map);

}
