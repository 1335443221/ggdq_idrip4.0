package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.common.config.AccountDictionary;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.JwtToken;
import com.sl.common.utils.PageUtil;
import com.sl.idripapp.dao.AccountUserDao;
import com.sl.idripapp.service.AccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountUserServiceImpl implements AccountUserService {

    @Autowired
    private AccountUserDao userDao;
    @Autowired
    private AccountDictionary accountDictionary;

    //#####################人员台账###############################
    //获取所有公司
    @Override
    public AppResult getAllCompany(Map<String,Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        return AppResult.success(userDao.getAllCompany(map));
    }

    //获取所有部门
    @Override
    public AppResult getAllDepartment(Map<String,Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        return AppResult.success(userDao.getAllDepartment(map));
    }

    //获取所有角色
    @Override
    public AppResult getAllRoles(Map<String,Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        return AppResult.success(userDao.getAllRoles(map));
    }

    //获取人员台账列表数据
    @Override
    public AppResult getUserList(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int total = userDao.getUserListCount(map);
        //获取列表
        List<Map<String, Object>> userList = userDao.getUserList(map);
        userList.forEach(each -> {
            String accountUserId = String.valueOf(each.get("account_user_id"));
            //获取该用户下关联的所有标签
            map.put("account_user_id", accountUserId);
            each.remove("account_user_id");
            List<Map<String, Object>> labelsByUserId = userDao.getLabelsByUserId(map);
            each.put("labels", labelsByUserId);
        });
        resultMap.put("recordList", userList);
        resultMap.put("is_lastpage", PageUtil.setLastPage(map,total));
        return AppResult.success(resultMap);
    }

    //获取人员台账详情数据
    @Override
    public AppResult getUserDetail(Map<String, Object> map) {
        Map<String, Object> userDetail = userDao.getUserDetail(map);
        map.put("account_user_id", userDetail.get("id"));
        //查询该用户的所有证书
        List<Map<String, Object>> certificatesByUserId = userDao.getCertificatesByUserId(map);
        userDetail.put("certificates", certificatesByUserId);
        return AppResult.success(userDetail);
    }

    //人员台账编辑
    @Override
    @Transactional
    public AppResult updateUser(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        //设置所属用户管理信息
        setUserInfo(map);
        int flag = 0;
        //修改用户角色关联表
        flag += userDao.updateUserRoleRelation(map);
        //修改用户项目关联表
        flag += userDao.updateUserProjectRelation(map);
        //修改web 用户表
        flag += userDao.updateWebUser(map);
        //修改account 用户表
        flag += userDao.updateAccountUser(map);
        //修改app 用户表
        flag += userDao.updateAppUser(map);
        if(flag >= 1) return AppResult.success();
        else return AppResult.error("1010");
    }

    //设置所属用户管理信息
    private void setUserInfo(Map<String, Object> map) {
        //查询app user_id对应的user信息
        Map<String, Object> appUserById = userDao.getAppUserById(map);
        String appUserId = String.valueOf(map.get("user_id"));
        String webUserId = String.valueOf(appUserById.get("user_id"));
        String accountUserId = String.valueOf(appUserById.get("account_user_id"));
        map.put("appUserId", appUserId);
        map.put("webUserId", webUserId);
        map.put("account_user_id", accountUserId);
    }



    //#####################提醒事项###############################
    //提醒事项列表查询
    @Override
    public AppResult getReminderList(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int total = userDao.getReminderListCount(map);
        //获取列表
        List<Map<String, Object>> reminderList = userDao.getReminderList(map);
        resultMap.put("recordList", reminderList);
        resultMap.put("is_lastpage", PageUtil.setLastPage(map,total));
        return AppResult.success(resultMap);
    }

    //修改提醒事项
    @Override
    public AppResult updateReminder(Map<String, Object> map) {
        int flag = userDao.updateReminder(map);
        if(flag >= 1) return AppResult.success();
        else return AppResult.error("1010");
    }

    //删除提醒事项
    @Override
    public AppResult deleteReminder(Map<String, Object> map) {
        int flag = userDao.deleteReminder(map);
        if(flag >= 1) return AppResult.success();
        else return AppResult.error("1010");
    }

}
