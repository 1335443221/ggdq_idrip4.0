package com.sl.idripapp.service.impl;

import com.sl.common.utils.*;
import com.sl.idripapp.dao.AccountContractDao;
import com.sl.idripapp.service.AccountContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountContractServiceImpl implements AccountContractService {

    @Autowired
    AccountContractDao contractDao;

    //获取合同信息
    @Override
    public AppResult getContractList(Map<String, Object> map) {
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int total = contractDao.getContractListCount(map);
        //获取列表
        List<Map<String, Object>> trainList = contractDao.getContractList(map);
        resultMap.put("recordList", trainList);
        resultMap.put("is_lastpage", PageUtil.setLastPage(map,total));
        return AppResult.success(resultMap);
    }

    //获取id获取合同详情
    @Override
    public AppResult getContractById(Map<String, Object> map) {
        return AppResult.success(contractDao.getContractById(map));
    }

    //修改合同信息
    @Override
    public AppResult updateContract(Map<String, Object> map) {
        //先查询合同台账
        Map<String, Object> contractById = contractDao.getContractById(map);
        Date terminationDate1 = DateUtil.parseStrToDate(String.valueOf(map.get("termination_date")), "yyyy-MM-dd");
        Date terminationDate = DateUtil.parseStrToDate(String.valueOf(contractById.get("termination_date")).substring(0, 11), "yyyy-MM-dd");
        if(terminationDate1.getTime() > terminationDate.getTime()){
            map.put("update_time", new Date());
        }
        int flag = contractDao.updateContract(map);
        if(flag >= 1) return AppResult.success();
        else return AppResult.error("1010");
    }
}
