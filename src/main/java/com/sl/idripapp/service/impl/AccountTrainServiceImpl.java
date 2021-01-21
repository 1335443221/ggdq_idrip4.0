package com.sl.idripapp.service.impl;

import com.sl.common.utils.AppResult;
import com.sl.common.utils.JwtToken;
import com.sl.common.utils.PageUtil;
import com.sl.idripapp.dao.AccountTrainDao;
import com.sl.idripapp.service.AccountTrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountTrainServiceImpl implements AccountTrainService {

    @Autowired
    private AccountTrainDao trainDao;

    //获取培训信息
    @Override
    public AppResult getTrainList(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int total = trainDao.getTrainListCount(map);
        //获取列表
        List<Map<String, Object>> trainList = trainDao.getTrainList(map);
        resultMap.put("recordList", trainList);
        resultMap.put("is_lastpage", PageUtil.setLastPage(map,total));
        return AppResult.success(resultMap);
    }

    //获取培训相关文件
    @Override
    public AppResult getTrainSheetFiles(Map<String, Object> map) {
        List<Map<String, Object>> trainSheetFiles = trainDao.getTrainSheetFiles(map);
        return AppResult.success(trainSheetFiles);
    }

    //获取参与人员
    @Override
    public AppResult getAttendUser(Map<String, Object> map) {
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int total = trainDao.getAttendUserCount(map);
        //获取列表
        List<Map<String, Object>> attendUser = trainDao.getAttendUser(map);
        resultMap.put("recordList", attendUser);
        resultMap.put("is_lastpage", PageUtil.setLastPage(map,total));
        return AppResult.success(resultMap);
    }

    //获取参与人员详情
    @Override
    public AppResult getAttendUserDetail(Map<String, Object> map) {
        List<Map<String, Object>> attendUserDetail = trainDao.getAttendUserDetail(map);
        return AppResult.success(attendUserDetail);
    }

}
