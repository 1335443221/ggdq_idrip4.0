package com.sl.idripapp.dao;

import com.sl.idripapp.entity.AccountMaterialOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface AccountLaborDao {

    ArrayList<HashMap<String, Object>> getLaborList(Map<String, Object> map);
    Integer getLaborListCount(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getLaborOutList(Map<String, Object> map);
    Integer getLaborOutListCount(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getAllLabor(Map<String, Object> map);




    Integer insertLaborOut(Map<String, Object> map);
    Integer updateLaborOut(Map<String, Object> map);
    Integer deleteLaborOut(Map<String, Object> map);
    AccountMaterialOut getLaborOutDetail(Map<String, Object> map);


    ArrayList<HashMap<String, Object>> getLaborInList(Map<String, Object> map);
    Integer getLaborInListCount(Map<String, Object> map);
    Integer insertLaborIn(Map<String, Object> map);
    Integer updateLaborIn(Map<String, Object> map);
    Integer deleteLaborIn(Map<String, Object> map);
    Map<String, Object> getLaborInDetail(Map<String, Object> map);


    //获取库存
    Integer getLaborAmount(@Param("id") int id);
    Integer getLaborOutAmount(@Param("id") int id);
    Integer getLaborInAmount(@Param("id") int id);
    Integer outOrInLabor(@Param("amount") int amount, @Param("id") int id);
}
