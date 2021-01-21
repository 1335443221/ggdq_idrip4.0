package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface AppEnergyVersionDao {

	public int save(Map<String, Object> map);

	public int update(Map<String, Object> map);

	public int delete(Map<String, Object> map);

	public ArrayList<HashMap<String, Object>> query(Map<String, Object> map);

	//
	public ArrayList<HashMap<String, Object>> download_query(Map<String, Object> map);

	public int download_save(Map<String, Object> map);

	public int download_update(Map<String, Object> map);

	public int download_delete(Map<String, Object> map);
}
