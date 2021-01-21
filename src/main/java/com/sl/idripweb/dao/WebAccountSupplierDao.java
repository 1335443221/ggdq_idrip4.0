package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountSupplierDao {

    //##########################供应商台账############################
    //获取供应商列表数据
    List<Map<String, Object>> getSupplierList(Map<String, Object> map);

    //获取供应商列表数据-count
    int getSupplierListCount(Map<String, Object> map);

    //通过供应商id获取合作项目合同列表信息
    List<Map<String, Object>> getContractsBySupplierId(Map<String, Object> map);

    //通过供应商id获取详情
    Map<String, Object> getSupplierById(Map<String, Object> map);

    //新增供应商
    int addSupplier(Map<String, Object> map);

    //新增供应商和合同关联信息
    int addSupplierContractRelation(Map<String, Object> map);

    //修改供应商
    int updateSupplier(Map<String, Object> map);

    //删除某供应商下的合同关联信息
    int deleteSupplierContractRelation(Map<String, Object> map);

    //批量删除供应商
    int deleteSupplier(Map<String, Object> map);

    //批量删除某供应商下的合同关联信息
    int deleteSupplierContractRelations(Map<String, Object> map);

    //加入黑名单
    int joinBlacklist(Map<String, Object> map);



    //##########################黑名单############################
    //获取黑名单列表数据
    List<Map<String, Object>> getBlackList(Map<String, Object> map);

    //获取黑名单列表数据-count
    int getBlackListCount(Map<String, Object> map);

    //黑名单批量恢复
    int restoreBlacks(Map<String, Object> map);




    //##########################数据统计############################


}
