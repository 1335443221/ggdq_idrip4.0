package com.sl.idripapp.dao;

import com.sl.idripapp.entity.AccountMaterialOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface AccountPartsDao {

    ArrayList<HashMap<String, Object>> getPartsList(Map<String, Object> map);
    Integer getPartsListCount(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getPartsOutList(Map<String, Object> map);
    Integer getPartsOutListCount(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getAllParts(Map<String, Object> map);




    Integer insertPartsOut(Map<String, Object> map);
    Integer updatePartsOut(Map<String, Object> map);
    Integer deletePartsOut(Map<String, Object> map);
    AccountMaterialOut getPartsOutDetail(Map<String, Object> map);


    ArrayList<HashMap<String, Object>> getPartsInList(Map<String, Object> map);
    Integer getPartsInListCount(Map<String, Object> map);
    Integer insertPartsIn(Map<String, Object> map);
    Integer updatePartsIn(Map<String, Object> map);
    Integer deletePartsIn(Map<String, Object> map);
    Map<String, Object> getPartsInDetail(Map<String, Object> map);


    //获取库存
    Integer getPartsAmount(@Param("id") int id);
    Integer getPartsOutAmount(@Param("id") int id);
    Integer getPartsInAmount(@Param("id") int id);
    Integer outOrInParts(@Param("amount") int amount, @Param("id") int id);
}
