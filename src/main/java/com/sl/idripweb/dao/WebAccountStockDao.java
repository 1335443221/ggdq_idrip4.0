package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountStockDao {

    ArrayList<Map<String, Object>> getType(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getStockList(Map<String, Object> map);
    Integer getStockListCount(Map<String, Object> map);

    //获取库存
    Integer getStockAmount(@Param("id") int id);
    Integer getStockOutAmount(@Param("id") int id);
    Integer getStockInAmount(@Param("id") int id);
    Integer outOrInStock(@Param("amount") int amount,@Param("id") int id);



    Integer insertType(Map<String, Object> map);
    Integer insertStock(Map<String, Object> map);
    Integer updateStock(Map<String, Object> map);
    Integer deleteStock(List<String> list);


    ArrayList<HashMap<String, Object>> getAllStock(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getStockOutList(Map<String, Object> map);
    Integer getStockOutListCount(Map<String, Object> map);
    Integer insertStockOut(Map<String, Object> map);
    Integer updateStockOut(Map<String, Object> map);
    Integer deleteStockOut(List<String> list);


    ArrayList<HashMap<String, Object>> getStockInList(Map<String, Object> map);
    Integer getStockInListCount(Map<String, Object> map);
    Integer insertStockIn(Map<String, Object> map);
    Integer updateStockIn(Map<String, Object> map);
    Integer deleteStockIn(List<String> list);


    List<Map<String, Object>> getAddStatistics(@Param("project_id") String project_id,@Param("year") String year);
    List<Map<String, Object>> getOutStatistics(@Param("project_id") String project_id,@Param("year") String year);
    List<Map<String, Object>> getInStatistics(@Param("project_id") String project_id,@Param("year") String year);

    List<Map<String, Object>> getOutRanking(@Param("project_id") String project_id,@Param("date") String date);
    List<Map<String, Object>> getInRanking(@Param("project_id") String project_id,
                                           @Param("date") String date,
                                           @Param("idList") List<String> idList);
}
