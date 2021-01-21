package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AlermDataDao {
	ArrayList<Map<String, Object>> getAlermData(Map<String, Object> map);
	Map<String, Object> getDataById(@Param("lid")String lid);
	Map<String, Object> queryDealDetail(@Param("lid")String id);
	int saveAlermMark(Map<String, Object> map);
	int updateAlermMark(Map<String, Object> map);
	Map<String, Object> selectAlermById(@Param("lid")String id);
	int insertHistory(Map<String, Object> map);
	int deleteById(@Param("lid")String id);

	//==========火灾报警=============//
	ArrayList<Map<String,Object>> getAlermByDeviceId(Map<String, Object> map);
	ArrayList<Map<String,Object>> fireAlermList(Map<String, Object> map);
	Map<String,Object> fireAlermDetail(Map<String, Object> map);
	int fireAlermListCount(Map<String, Object> map);
	Map<String,Object> queryAlermDealDetail(Map<String, Object> map);
	ArrayList<Map<String, Object>> getFireTagByDetectorId(Map<String, Object> map);
	ArrayList<Map<String, Object>> fireAlConfData(Map<String, Object> map);
	int fireAlConfUpdate(Map<String, Object> map);
	int fireAlConfSave(Map<String, Object> map);
	ArrayList<Map<String, Object>> selectIsExistConfig(Map<String, Object> map);

	ArrayList<HashMap<String, Object>> getCategoryRelation(Map<String, Object> map);
	ArrayList<HashMap<String, Object>> getTgidByRelation_id(Map<String, Object> map);


	ArrayList<Map<String,Object>> getNoDealAlermData(Map<String,Object> map);
	int  insertAllHistory(List<Map> list);
	int  insertAllMesg(List<Map<String,Object>> list);
	int  deleteAllMesg(List<Integer> list);
	int  deleteAllLogs(List<Integer> list);

}