package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.common.config.AccountDictionary;
import com.sl.common.utils.PageUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.WebAccountUserDao;
import com.sl.idripweb.service.WebAccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WebAccountUserServiceImpl implements WebAccountUserService {

    @Autowired
    private WebAccountUserDao userDao;
    @Autowired
    private AccountDictionary accountDictionary;

    //#####################人员台账###############################
    //获取所有公司
    @Override
    public WebResult getAllCompany(Map<String,Object> map) {
        return WebResult.success(userDao.getAllCompany(map));
    }

    //获取所有部门
    @Override
    public WebResult getAllDepartment(Map<String,Object> map) {
        return WebResult.success(userDao.getAllDepartment(map));
    }

    //获取所有角色
    @Override
    public WebResult getAllRoles(Map<String,Object> map) {
        return WebResult.success(userDao.getAllRoles(map));
    }

    //获取用户台账所有标签
    @Override
    public WebResult getAllLabel(Map<String, Object> map) {
        return WebResult.success(userDao.getAllLabel(map));
    }

    //标签新增
    @Override
    public WebResult addLabel(Map<String, Object> map) {
        int result = userDao.addLabel(map);
        if(result >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //标签删除
    @Override
    @Transactional
    public WebResult deleteLabel(Map<String, Object> map) {
        //删除标签
        int result = userDao.deleteLabel(map);
        //根据标签id删除该标签下关联的所有用户
        userDao.deleteLabelRelation(map);
        if(result >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //标签验证是否被人员使用
    @Override
    public WebResult checkLabel(Map<String, Object> map) {
        int result = userDao.checkLabel(map);
        if(result >= 1) return WebResult.success(true);
        else return WebResult.success(false);
    }

    //给人员关联标签
    @Transactional
    @Override
    public WebResult relationLabel(Map<String, Object> map) {
        //先通过web用户id查询基础用户表id
        if(map.get("account_user_id") == null){
            int accountUserId = userDao.getAccountUserByAppUser(map);
            map.put("account_user_id", accountUserId);
        }
        //先删除该用户下所有标签
        int result = userDao.deleteUserLabel(map);
        //为用户批量添加标签关联关系
        if(map.get("labels") != null){
            String labels = String.valueOf(map.get("labels"));
            map.put("labels", labels.split(","));
            userDao.addUserLabelRelation(map);
        }
        if(result >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //获取人员台账列表数据
    @Override
    public WebResult getUserList(Map<String, Object> map) {
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int count = userDao.getUserListCount(map);
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
        resultMap.put("count", count);
        return WebResult.success(resultMap);
    }

    //维保台账维保人员列表
    @Override
    public WebResult getMaintenanceUserList(Map<String, Object> map) {
        return WebResult.success(userDao.getMaintenanceUserList(map));
    }

    //获取人员台账详情数据
    @Override
    public WebResult getUserDetail(Map<String, Object> map) {
        Map<String, Object> userDetail = userDao.getUserDetail(map);
        map.put("account_user_id", userDetail.get("id"));
        //获取该用户的所有标签
        List<Map<String, Object>> labelsByUserId = userDao.getLabelsByUserId(map);
        userDetail.put("labels", labelsByUserId);
        //查询该用户的所有证书
        List<Map<String, Object>> certificatesByUserId = userDao.getCertificatesByUserId(map);
        userDetail.put("certificates", certificatesByUserId);
        return WebResult.success(userDetail);
    }

    //人员台账新增
    @Override
    @Transactional
    public WebResult addUser(Map<String, Object> map) {
        //解析证书信息为数组
        if(map.get("certificates") != null){
            map.put("certificates", JSONArray.parseArray(String.valueOf(map.get("certificates"))));
            //给人员添加技能证书
            userDao.addCertificate(map);
        }
        //添加web user表
        int flag = 0;
        flag += userDao.addWebUser(map);
        map.put("user_id", String.valueOf(map.get("id")));
        //添加用户角色关联表
        flag += userDao.addWebUserRoleRelation(map);
        //添加用户项目关联表
        flag += userDao.addWebUserProjectRelation(map);
        //添加account user表
        flag += userDao.addAccountUser(map);
        map.put("account_user_id", String.valueOf(map.get("id")));
        //添加app user表
        flag += userDao.addAppUser(map);
        //给人员关联标签
        relationLabel(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //人员台账删除
    @Override
    @Transactional
    public WebResult deleteUser(Map<String, Object> map) {
        //设置所属用户管理信息
        setUserInfo(map);

        //删除用户角色关联表
        int flag = 0;
        flag += userDao.deleteUserRoleRelation(map);
        //删除用户项目关联表
        flag += userDao.deleteUserProjectRelation(map);
        //删除web 用户表
        flag += userDao.deleteWebUser(map);
        //删除account 用户表
        flag += userDao.deleteAccountUser(map);
        //删除人员标签关联表
        flag += userDao.deleteUserLabel(map);
        //删除技能证书表
        flag += userDao.deleteUserCertificate(map);
        //删除app 用户表
        flag += userDao.deleteAppUser(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //人员台账编辑
    @Override
    @Transactional
    public WebResult updateUser(Map<String, Object> map) {
        //设置所属用户管理信息
        setUserInfo(map);
        //解析证书信息为数组
        if(map.get("certificates") != null){
            map.put("certificates", JSONArray.parseArray(String.valueOf(map.get("certificates"))));
            //删除技能证书表
            userDao.deleteUserCertificate(map);
            //添加技能证书
            userDao.addCertificate(map);
        }
        //修改用户角色关联表
        int flag = 0;
        flag += userDao.updateUserRoleRelation(map);
        //修改用户项目关联表
        flag += userDao.updateUserProjectRelation(map);
        //修改web 用户表
        flag += userDao.updateWebUser(map);
        //修改account 用户表
        flag += userDao.updateAccountUser(map);
        //修改app 用户表
        flag += userDao.updateAppUser(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
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
    public WebResult getReminderList(Map<String, Object> map) {
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int count = userDao.getReminderListCount(map);
        //获取列表
        List<Map<String, Object>> reminderList = userDao.getReminderList(map);
        resultMap.put("recordList", reminderList);
        resultMap.put("count", count);
        return WebResult.success(resultMap);
    }

    //根据id获取单个提醒事项
    @Override
    public WebResult getReminderById(Map<String, Object> map) {
        Map<String, Object> reminderById = userDao.getReminderById(map);
        return WebResult.success(reminderById);
    }

    //获取公司部门下的人员
    @Override
    public WebResult getReminderUsers(Map<String, Object> map) {
        List<Map<String, Object>> reminderUsers = userDao.getReminderUsers(map);
        return WebResult.success(reminderUsers);
    }

    //新增提醒事项
    @Override
    public WebResult addReminder(Map<String, Object> map) {
        //获取当前用户所对应的APP user
        int appUserId = userDao.getAppUserByWebUser(map);
        map.put("add_by", appUserId);
        int flag = userDao.addReminder(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //修改提醒事项
    @Override
    public WebResult updateReminder(Map<String, Object> map) {
        int flag = userDao.updateReminder(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //删除提醒事项
    @Override
    public WebResult deleteReminder(Map<String, Object> map) {
        int flag = userDao.deleteReminder(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }



    //#####################数据统计###############################
    //数据统计页面大接口
    @Override
    public WebResult statistics(Map<String, Object> map) {
        //结果集
        Map<String, Object> result = new HashMap<>();

        //获取男女比例
        Map<String, Object> sexRatio = getSexRatio(map);
        //获取政治面貌比例
        Map<String, Object> politicalOutlookRatio = getPoliticalOutlookRatio(map);
        //获取人员类型
        Map<String, Object> userType = getUserType(map);
        //获取最高学历
        Map<String, Object> highestEducation = getHighestEducation(map);
        //获取人员新增趋势
        Map<String, Object> userAddTrend = getUserAddTrend(map);

        result.put("sexRatio", sexRatio);
        result.put("politicalOutlookRatio", politicalOutlookRatio);
        result.put("userType", userType);
        result.put("highestEducation", highestEducation);
        result.put("userAddTrend", userAddTrend);
        return WebResult.success(result);
    }

    //获取男女比例
    private Map<String, Object> getSexRatio(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> sexRatioChart = userDao.getSexRatioChart(map);
        String legendStr = "男,女";
        List<String> legendData = Arrays.asList(legendStr.split(","));
        //没有数据的补零
        zeroComplementPie(sexRatioChart, legendData);
        result.put("legendData", legendData);
        result.put("seriesData", sexRatioChart);
        return result;
    }

    //获取政治面貌比例
    private Map<String, Object> getPoliticalOutlookRatio(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> outlookRatio = userDao.getPoliticalOutlookRatioChart(map);
        List<String> legendData = accountDictionary.getUserPoliticalOutlook();
        //没有数据的补零
        zeroComplementPie(outlookRatio, legendData);
        result.put("legendData", legendData);
        result.put("seriesData", outlookRatio);
        return result;
    }

    //获取人员类型
    private Map<String, Object> getUserType(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> userType = userDao.getUserTypeChart(map);
        List<String> xAxisData = accountDictionary.getUserType();
        //没有数据的补零
        List<Integer> seriesData = zeroComplementBar(userType, xAxisData);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    //获取最高学历
    private Map<String, Object> getHighestEducation(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> highestEducation = userDao.getHighestEducationChart(map);
        List<String> xAxisData = accountDictionary.getUserHighestEducation();
        //没有数据的补零
        List<Integer> seriesData = zeroComplementBar(highestEducation, xAxisData);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    //获取人员新增趋势
    private Map<String, Object> getUserAddTrend(Map<String, Object> map) {
        //封装结果集
        Map<String, Object> result = new HashMap<>();
        String legendStr = "男,女";
        List<String> legendData = Arrays.asList(legendStr.split(","));
        String xAxisStr = "一月,二月,三月,四月,五月,六月,七月,八月,九月,十月,十一月,十二月";
        List<String> xAxisData = Arrays.asList(xAxisStr.split(","));
        List<Map<String, Object>> userAddTrendChart = userDao.getUserAddTrendChart(map);
        List<Map<String, Object>> menValueMap = new ArrayList<>();
        List<Map<String, Object>> womenValueMap = new ArrayList<>();
        userAddTrendChart.forEach(each -> {
            String sex = String.valueOf(each.get("sex"));
            each.put("name", each.get("date"));
            each.remove("date");
            if("男".equals(sex)){
                menValueMap.add(each);
            }else{
                womenValueMap.add(each);
            }
        });
        Map<String, List<Integer>> seriesData = new HashMap<>();
        //没有数据的补零
        List<Integer> menSeriesData = zeroComplementBar(menValueMap, xAxisData);
        List<Integer> womenSeriesData = zeroComplementBar(womenValueMap, xAxisData);
        seriesData.put("男", menSeriesData);
        seriesData.put("女", womenSeriesData);
        result.put("legendData", legendData);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    //数据补零--适用于饼图数据
    private void zeroComplementPie(List<Map<String, Object>> sexRatioChart, List<String> legendData) {
        //从list集合中，取出字段name的列表
        List<String> names = sexRatioChart.stream().map(each -> String.valueOf(each.get("name"))).collect(Collectors.toList());
        legendData.forEach(each -> {
            if(!names.contains(each)){
                Map<String, Object> zeroMap = new HashMap<>();
                zeroMap.put("name", each);
                zeroMap.put("value", 0);
                sexRatioChart.add(zeroMap);
            }
        });
    }

    //数据补零--适用于折线图柱形图数据
    private List<Integer> zeroComplementBar(List<Map<String, Object>> sexRatioChart, List<String> xAxisData) {
        //从list集合中，取出字段name的列表
        List<Integer> datas = new ArrayList<>();
        Map<String, Integer> valueMap = new HashMap<>();
        sexRatioChart.forEach(each -> {
            valueMap.put(String.valueOf(each.get("name")), Integer.parseInt(String.valueOf(each.get("value"))));
        });
        xAxisData.forEach(each -> {
            if(valueMap.get(each) == null)
                datas.add(0);
            else
                datas.add(valueMap.get(each));
        });
        return datas;
    }

}
