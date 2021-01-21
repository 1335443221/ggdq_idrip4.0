package com.sl.idripapp.dao;

import com.sl.idripapp.entity.AccountMaterialOut;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountStockDao {

    ArrayList<HashMap<String, Object>> getStockList(Map<String, Object> map);
    Integer getStockListCount(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getStockOutList(Map<String, Object> map);
    Integer getStockOutListCount(Map<String, Object> map);

    ArrayList<HashMap<String, Object>> getAllStock(Map<String, Object> map);




    Integer insertStockOut(Map<String, Object> map);
    Integer updateStockOut(Map<String, Object> map);
    Integer deleteStockOut(Map<String, Object> map);
    AccountMaterialOut getStockOutDetail(Map<String, Object> map);


    ArrayList<HashMap<String, Object>> getStockInList(Map<String, Object> map);
    Integer getStockInListCount(Map<String, Object> map);
    Integer insertStockIn(Map<String, Object> map);
    Integer updateStockIn(Map<String, Object> map);
    Integer deleteStockIn(Map<String, Object> map);
    Map<String, Object> getStockInDetail(Map<String, Object> map);


    //获取库存
    Integer getStockAmount(@Param("id") int id);
    Integer getStockOutAmount(@Param("id") int id);
    Integer getStockInAmount(@Param("id") int id);
    Integer outOrInStock(@Param("amount") int amount,@Param("id") int id);
}
