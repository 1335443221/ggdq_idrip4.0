package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountContractDao {

    //##########################合同台账############################
    //获取合同台账列表数据
    List<Map<String, Object>> getContractList(Map<String, Object> map);

    //获取合同台账列表数据-count
    int getContractListCount(Map<String, Object> map);

    //供应商台账合作项目列表
    List<Map<String, Object>> getContractListSupplier(Map<String, Object> map);

    //通过id获取合同台账
    Map<String, Object> getContractById(Map<String, Object> map);

    //新增合同台账
    int addContract(Map<String, Object> map);

    //编辑合同台账
    int updateContract(Map<String, Object> map);

    //删除合同台账
    int deleteContract(Map<String, Object> map);

    //通过合同id获取提醒事项
    Map<String, Object> getRemindersByContractId(Map<String, Object> map);

    //新增合同提醒事项
    int addContractReminders(Map<String, Object> map);

    //编辑合同提醒事项
    int updateContractReminders(Map<String, Object> map);


    //##########################数据统计############################
    //状态统计
    int getStatusStatistics(Map<String, Object> map);

    //延期/中止合同
    List<Map<String, Object>> getDelayTermination(Map<String, Object> map);

    //签署/到期合同
    List<Map<String, Object>> getSignExpire(Map<String, Object> map);

}
