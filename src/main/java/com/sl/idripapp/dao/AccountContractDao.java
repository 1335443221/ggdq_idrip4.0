package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountContractDao {

    //获取合同信息
    List<Map<String, Object>> getContractList(Map<String, Object> map);

    //获取合同信息->count
    int getContractListCount(Map<String, Object> map);

    //获取id获取合同详情
    Map<String, Object> getContractById(Map<String, Object> map);

    //修改合同信息
    int updateContract(Map<String, Object> map);

}
