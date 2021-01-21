package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountPartsDao {

    ArrayList<Map<String, Object>> getType(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getPartsList(Map<String, Object> map);
    Integer getPartsListCount(Map<String, Object> map);

    //获取库存
    Integer getPartsAmount(@Param("id") int id);
    Integer getPartsOutAmount(@Param("id") int id);
    Integer getPartsInAmount(@Param("id") int id);
    Integer outOrInParts(@Param("amount") int amount, @Param("id") int id);



    Integer insertType(Map<String, Object> map);
    Integer insertParts(Map<String, Object> map);
    Integer updateParts(Map<String, Object> map);
    Integer deleteParts(List<String> list);


    ArrayList<HashMap<String, Object>> getAllParts(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getPartsOutList(Map<String, Object> map);
    Integer getPartsOutListCount(Map<String, Object> map);
    Integer insertPartsOut(Map<String, Object> map);
    Integer updatePartsOut(Map<String, Object> map);
    Integer deletePartsOut(List<String> list);


    ArrayList<HashMap<String, Object>> getPartsInList(Map<String, Object> map);
    Integer getPartsInListCount(Map<String, Object> map);
    Integer insertPartsIn(Map<String, Object> map);
    Integer updatePartsIn(Map<String, Object> map);
    Integer deletePartsIn(List<String> list);


    List<Map<String, Object>> getAddStatistics(@Param("project_id") String project_id,@Param("module") String module, @Param("year") String year);
    List<Map<String, Object>> getOutStatistics(@Param("project_id") String project_id,@Param("module") String module, @Param("year") String year);
    List<Map<String, Object>> getInStatistics(@Param("project_id") String project_id,@Param("module") String module,@Param("year") String year);

    List<Map<String, Object>> getOutRanking(@Param("project_id") String project_id,@Param("module") String module, @Param("date") String date);
    List<Map<String, Object>> getInRanking(@Param("project_id") String project_id,@Param("module") String module,
                                           @Param("date") String date,
                                           @Param("idList") List<String> idList);
}
