package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface AppRegisterUserDao {

	public int save(Map<String, Object> map);

	public int update(Map<String, Object> map);

	public int delete(Map<String, Object> map);

	public ArrayList<HashMap<String, Object>> query(Map<String, Object> map);

	public HashMap<String, Object> queryElecPrice(Map<String, Object> map);

	public int updateElecPrice(Map<String, Object> map);

	//session
	public HashMap<String, Object> getSession(Map<String, Object> map);
	public int insertSession(Map<String, Object> map);



	public int updateAppUser(Map<String, Object> map);
	public int updateProjectUser(Map<String, Object> map);
	public int updateProjectRoleUser(Map<String, Object> map);


	public int insertAppUser(Map<String, Object> map);
	public int insertProjectUser(Map<String, Object> map);
	public int insertProjectRoleUser(Map<String, Object> map);


	public int deleteAppUser(Map<String, Object> map);
	public int deleteProjectUser(Map<String, Object> map);

	public ArrayList<HashMap<String, Object>> getUserInfo(Map<String, Object> map);

	public ArrayList<HashMap<String, Object>> getUserProjectInfo(Map<String, Object> map);
}
