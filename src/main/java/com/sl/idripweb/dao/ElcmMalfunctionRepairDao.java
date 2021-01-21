package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ElcmMalfunctionRepairDao {

    ArrayList<HashMap<String, Object>> getMalfunctionList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getMalfunctionStatus(Map<String, Object> map);
    int getMalfunctionListCount(Map<String, Object> map);
    int addMalfunction(Map<String, Object> map);
    int addReapir(Map<String, Object> map);
    Map<String, Object> getMalfunction(Map<String, Object> map);
    int updateMalfunction(Map<String, Object> map);
    List<String> getStatusByIds(Map<String, Object> map);
    int deleteMalfunction(Map<String, Object> map);
    //================维修-=============
    ArrayList<HashMap<String, Object>>  getRepairList(Map<String, Object> map);
    ArrayList<Map<String, Object>>  getRepairStatus(Map<String, Object> map);
    int getRepairListCount(Map<String, Object> map);
    //int assignUser(Map<String, Object> map);
    Map<String, Object> getRepair(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getMyRepairList(Map<String, Object> map);
    int getMyRepairListCount(Map<String, Object> map);
    int chargeback(Map<String, Object> map);
    int transfer(Map<String, Object> map);
}
