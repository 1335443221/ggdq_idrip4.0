package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface ElcmMalfunctionDao {

    //============================APP=============================//
    //获取未处理故障
    ArrayList<Map<String, Object>> getMalfunctionList(Map<String, Object> map);
    int getMalfunctionListCount(Map<String, Object> map);
    int getNoDealMalfunctionCount(Map<String, Object> map);

    //获取这台机器已存在的故障
    ArrayList<Map<String, Object>> getExistingMalfunction(Map<String, Object> map);
    //上传故障
    int reportMalfunction(Map<String, Object> map);
    int addReapir(Map<String, Object> map);
    int updateMalfunction(Map<String, Object> map);
    //维修完成
    int repairComplete(Map<String, Object> map);
    //故障详情
    Map<String, Object> getMalfunctionDetail(Map<String, Object> map);
    //设备信息
    Map<String, Object> getDeviceById(Map<String, Object> map);

}
