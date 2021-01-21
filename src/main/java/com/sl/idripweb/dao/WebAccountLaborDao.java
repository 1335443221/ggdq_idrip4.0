package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountLaborDao {

    ArrayList<Map<String, Object>> getType(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getLaborList(Map<String, Object> map);
    Integer getLaborListCount(Map<String, Object> map);

    //获取库存
    Integer getLaborAmount(@Param("id") int id);
    Integer getLaborOutAmount(@Param("id") int id);
    Integer getLaborInAmount(@Param("id") int id);
    Integer outOrInLabor(@Param("amount") int amount, @Param("id") int id);



    Integer insertType(Map<String, Object> map);
    Integer insertLabor(Map<String, Object> map);
    Integer updateLabor(Map<String, Object> map);
    Integer deleteLabor(List<String> list);


    ArrayList<HashMap<String, Object>> getAllLabor(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getLaborOutList(Map<String, Object> map);
    Integer getLaborOutListCount(Map<String, Object> map);
    Integer insertLaborOut(Map<String, Object> map);
    Integer updateLaborOut(Map<String, Object> map);
    Integer deleteLaborOut(List<String> list);


    ArrayList<HashMap<String, Object>> getLaborInList(Map<String, Object> map);
    Integer getLaborInListCount(Map<String, Object> map);
    Integer insertLaborIn(Map<String, Object> map);
    Integer updateLaborIn(Map<String, Object> map);
    Integer deleteLaborIn(List<String> list);


    List<Map<String, Object>> getAddStatistics(@Param("project_id") String project_id, @Param("year") String year);
    List<Map<String, Object>> getOutStatistics(@Param("project_id") String project_id, @Param("year") String year);
    List<Map<String, Object>> getInStatistics(@Param("project_id") String project_id, @Param("year") String year);

    List<Map<String, Object>> getOutRanking(@Param("project_id") String project_id, @Param("date") String date);
    List<Map<String, Object>> getInRanking(@Param("project_id") String project_id,
                                           @Param("date") String date,
                                           @Param("idList") List<String> idList);
}
