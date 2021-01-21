package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountUserDao {

    //#####################人员台账###############################
    //获取所有公司
    List<Map<String, Object>> getAllCompany(Map<String, Object> map);

    //获取所有部门
    List<Map<String, Object>> getAllDepartment(Map<String, Object> map);

    //获取所有角色
    List<Map<String, Object>> getAllRoles(Map<String, Object> map);

    //获取用户台账所有标签
    List<Map<String, Object>> getAllLabel(Map<String, Object> map);

    //标签新增
    int addLabel(Map<String, Object> map);

    //标签删除
    int deleteLabel(Map<String, Object> map);

    //删除标签和用户关联信息
    int deleteLabelRelation(Map<String, Object> map);

    //删除用户下所有标签
    int deleteUserLabel(Map<String, Object> map);

    //查询标签是否已经被用户使用
    int checkLabel(Map<String, Object> map);

    //根据web user表id查询人员台账基础表信息
    Map<String, Object> getAccountUser(Map<String, Object> map);

    //为用户批量添加标签关联关系
    int addUserLabelRelation(Map<String, Object> map);

    //获取人员台账列表数据
    List<Map<String, Object>> getUserList(Map<String, Object> map);

    //获取人员台账列表--count
    int getUserListCount(Map<String, Object> map);

    //维保台账维保人员列表
    List<Map<String, Object>> getMaintenanceUserList(Map<String, Object> map);

    //根据台账用户id获取用户下所有标签
    List<Map<String, Object>> getLabelsByUserId(Map<String, Object> map);

    //根据台账用户id获取用户下所有技能证书
    List<Map<String, Object>> getCertificatesByUserId(Map<String, Object> map);

    //通过web user获取 app_user
    int getAppUserByWebUser(Map<String, Object> map);

    //通过app user获取 account_user
    int getAccountUserByAppUser(Map<String, Object> map);

    //通过app user获取user
    Map<String, Object> getAppUserById(Map<String, Object> map);

    //获取人员台账人员详情
    Map<String, Object> getUserDetail(Map<String, Object> map);

    //添加app user表
    int addAppUser(Map<String, Object> map);

    //添加web user表
    int addWebUser(Map<String, Object> map);

    //添加account user表
    int addAccountUser(Map<String, Object> map);

    //添加web端用户和项目关联表
    int addWebUserProjectRelation(Map<String, Object> map);

    //添加web端用户和角色关联表
    int addWebUserRoleRelation(Map<String, Object> map);

    //添加技能证书
    int addCertificate(Map<String, Object> map);

    //删除用户角色关联表
    int deleteUserRoleRelation(Map<String, Object> map);

    //删除用户项目关联表
    int deleteUserProjectRelation(Map<String, Object> map);

    //删除web 用户表
    int deleteWebUser(Map<String, Object> map);

    //删除account 用户表
    int deleteAccountUser(Map<String, Object> map);

    //删除人员下所有技能证书
    int deleteUserCertificate(Map<String, Object> map);

    //删除app 用户表
    int deleteAppUser(Map<String, Object> map);

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

    //根据id获取单个提醒事项
    Map<String, Object> getReminderById(Map<String, Object> map);

    //获取公司部门下的人员
    List<Map<String, Object>> getReminderUsers(Map<String, Object> map);

    //新增提醒事项
    int addReminder(Map<String, Object> map);

    //修改提醒事项
    int updateReminder(Map<String, Object> map);

    //删除提醒事项
    int deleteReminder(Map<String, Object> map);



    //#####################数据统计###############################
    //男女比例
    List<Map<String, Object>> getSexRatioChart(Map<String, Object> map);

    //政治面貌比例
    List<Map<String, Object>> getPoliticalOutlookRatioChart(Map<String, Object> map);

    //人员新增趋势
    List<Map<String, Object>> getUserAddTrendChart(Map<String, Object> map);

    //人员类型
    List<Map<String, Object>> getUserTypeChart(Map<String, Object> map);

    //最高学历
    List<Map<String, Object>> getHighestEducationChart(Map<String, Object> map);

}
