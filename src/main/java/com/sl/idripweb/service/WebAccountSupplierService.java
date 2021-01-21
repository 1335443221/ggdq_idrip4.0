package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountSupplierService {

    //##########################供应商台账############################
    //获取供应商列表数据
    public WebResult getSupplierList(Map<String,Object> map);

    //通过供应商id获取详情
    public WebResult getSupplierById(Map<String,Object> map);

    //新增供应商
    public WebResult addSupplier(Map<String,Object> map);

    //修改供应商
    public WebResult updateSupplier(Map<String,Object> map);

    //批量删除供应商
    public WebResult deleteSupplier(Map<String,Object> map);

    //加入黑名单
    public WebResult joinBlacklist(Map<String,Object> map);


    //##########################黑名单############################
    //获取黑名单列表数据
    public WebResult getBlackList(Map<String,Object> map);

    //黑名单批量恢复
    public WebResult restoreBlacks(Map<String,Object> map);





    //##########################数据统计############################
}
