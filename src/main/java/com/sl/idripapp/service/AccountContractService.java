package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountContractService {

    //获取合同信息
    public AppResult getContractList(Map<String,Object> map);

    //获取id获取合同详情
    public AppResult getContractById(Map<String,Object> map);

    //修改合同信息
    public AppResult updateContract(Map<String,Object> map);
}
