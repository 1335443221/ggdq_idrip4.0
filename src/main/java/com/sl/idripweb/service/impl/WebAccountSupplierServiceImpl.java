package com.sl.idripweb.service.impl;

import com.sl.common.config.AccountDictionary;
import com.sl.common.utils.PageUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.WebAccountSupplierDao;
import com.sl.idripweb.service.WebAccountSupplierService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebAccountSupplierServiceImpl implements WebAccountSupplierService {

    @Autowired
    private WebAccountSupplierDao supplierDao;
    @Autowired
    private AccountDictionary accountDictionary;

    //##########################供应商台账############################
    //获取供应商列表数据
    @Override
    public WebResult getSupplierList(Map<String, Object> map) {
        //供应商信誉等级处理和注册资本处理
        handleParams(map);

        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int count = supplierDao.getSupplierListCount(map);
        //获取列表
        List<Map<String, Object>> supplierList = supplierDao.getSupplierList(map);
        supplierList.forEach(each -> {
            String supplierId = String.valueOf(each.get("id"));
            //通过供应商id获取合作项目合同列表信息
            map.put("supplier_id", supplierId);
            each.remove("supplier_id");
            List<Map<String, Object>> contracts = supplierDao.getContractsBySupplierId(map);
            each.put("contracts", contracts);
        });
        resultMap.put("recordList", supplierList);
        resultMap.put("count", count);
        return WebResult.success(resultMap);
    }

    //通过供应商id获取详情
    @Override
    public WebResult getSupplierById(Map<String, Object> map) {
        Map<String, Object> supplierById = supplierDao.getSupplierById(map);
        //通过供应商id获取合作项目合同列表信息
        List<Map<String, Object>> contracts = supplierDao.getContractsBySupplierId(map);
        supplierById.put("contracts", contracts);
        return WebResult.success(supplierById);
    }

    //新增供应商
    @Override
    @Transactional
    public WebResult addSupplier(Map<String, Object> map) {
        int flag = 0;
        //新增供应商
        flag += supplierDao.addSupplier(map);
        map.put("supplier_id", map.get("id"));
        //新增供应商和合同关联信息
        //处理选中的所有合作项目
        if(map.get("contract_ids") != null){
            map.put("contract_ids", String.valueOf(map.get("contract_ids")).split(","));
            flag += supplierDao.addSupplierContractRelation(map);
        }
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //修改供应商
    @Override
    @Transactional
    public WebResult updateSupplier(Map<String, Object> map) {
        int flag = 0;
        //修改供应商
        flag += supplierDao.updateSupplier(map);
        //先删除某供应商下的合同关联信息
        flag += supplierDao.deleteSupplierContractRelation(map);
        //处理选中的所有合作项目
        if(map.get("contract_ids") != null){
            map.put("contract_ids", String.valueOf(map.get("contract_ids")).split(","));
            flag += supplierDao.addSupplierContractRelation(map);
        }
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //批量删除供应商
    @Override
    public WebResult deleteSupplier(Map<String, Object> map) {
        int flag = 0;
        //处理供应商id为数组
        map.put("supplier_ids", String.valueOf(map.get("supplier_ids")).split(","));
        //批量删除供应商
        flag += supplierDao.deleteSupplier(map);
        //批量删除供应商下的合作关联项目
        flag += supplierDao.deleteSupplierContractRelations(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //加入黑名单
    @Override
    public WebResult joinBlacklist(Map<String, Object> map) {
        int flag = supplierDao.joinBlacklist(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //供应商信誉等级处理和注册资本处理
    private void handleParams(Map<String, Object> map) {
        //处理信誉等级
        String creditRating = String.valueOf(map.get("credit_rating"));
        List<String> creditRatings = accountDictionary.getCreditRating();
        int index = creditRatings.indexOf(creditRating);
        if(index == 0) map.put("credit_rating", " < 3");
        else if(index == 1) map.put("credit_rating", "between 3 and 4");
        else if(index == 2) map.put("credit_rating", " > 4");
        else map.put("credit_rating", null);
        //处理注册资本
        if(map.get("capital_start") == null || StringUtils.isEmpty(String.valueOf(map.get("capital_start")))) map.put("capital_start", 0);
        if(map.get("capital_end") == null || StringUtils.isEmpty(String.valueOf(map.get("capital_end")))) map.put("capital_end", 9999999);
        String capitalStart = String.valueOf(map.get("capital_start"));
        String capitalEnd = String.valueOf(map.get("capital_end"));
        map.put("capital_start", Long.parseLong(capitalStart)*10000);
        map.put("capital_end", Long.parseLong(capitalEnd)*10000);
    }


    //##########################黑名单############################
    //获取黑名单列表数据
    @Override
    public WebResult getBlackList(Map<String, Object> map) {
        //处理时间参数
        handleTimes(map);
        //设置分页信息
        PageUtil.setPageInfo(map);
        //结果集
        HashMap<String, Object> resultMap = new HashMap<>();
        //获取总数量
        int count = supplierDao.getBlackListCount(map);
        //获取列表
        List<Map<String, Object>> blackList = supplierDao.getBlackList(map);
        resultMap.put("recordList", blackList);
        resultMap.put("count", count);
        return WebResult.success(resultMap);
    }

    //黑名单批量恢复
    @Override
    public WebResult restoreBlacks(Map<String, Object> map) {
        //处理供应商id为数组
        map.put("supplier_ids", String.valueOf(map.get("supplier_ids")).split(","));
        int flag = supplierDao.restoreBlacks(map);
        if(flag >= 1) return WebResult.success();
        else return WebResult.error(201);
    }

    //处理时间参数
    private void handleTimes(Map<String, Object> map) {
        if(map.get("begin_date") == null || StringUtils.isEmpty(String.valueOf(map.get("begin_date")))) map.put("begin_date", "2000-01-01");
        if(map.get("end_date") == null || StringUtils.isEmpty(String.valueOf(map.get("end_date")))) map.put("end_date", "2999-01-01");
    }





    //##########################数据统计############################
}
