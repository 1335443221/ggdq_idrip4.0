package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountContractService {

    //##########################合同台账############################
    //获取合同台账列表数据
    public WebResult getContractList(Map<String,Object> map);

    //供应商台账合作项目列表
    public WebResult getContractListSupplier(Map<String,Object> map);

    //通过id获取合同台账
    public WebResult getContractById(Map<String,Object> map);

    //新增合同台账
    public WebResult addContract(Map<String,Object> map);

    //编辑合同台账
    public WebResult updateContract(Map<String,Object> map);

    //删除合同台账
    public WebResult deleteContract(Map<String,Object> map);

    //通过合同id获取提醒事项
    public WebResult getRemindersByContractId(Map<String,Object> map);

    //新增合同提醒事项
    public WebResult addContractReminders(Map<String,Object> map);

    //编辑合同提醒事项
    public WebResult updateContractReminders(Map<String,Object> map);



    //##########################数据统计############################
    //数据统计页面大接口
    public WebResult statistics(Map<String,Object> map);

}
