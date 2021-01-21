package com.sl.idripapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface VersionDataService {
	//当前版本
	public Map<String, Object> getActiveVersion(Map<String, Object> map);
	
	//删除
	public int deleteVersionById(Map<String, Object> map);
	
	//上传版本
	public int insertVersion(Map<String, Object> map,MultipartFile file);
	
	public int insertIOSVersion(Map<String, Object> map);
	
	//修改版本
	public int updateVersion(Map<String, Object> map,MultipartFile file);
	
	
	//判断版本号
	public int checkVersion(Map<String, Object> map);

	//获取一个
	public Map<String, Object> getVersionById(Integer id);
		
	//展示所有版本
	public Map<String, Object> getAllVersion(Map<String, Object> map);
	
	public Object appUpdate(Map<String, Object> map);
	public Object appCheckUpdate(Map<String, Object> map);

	public Object ios_appupdate(Map<String, Object> map);
	
	
	
}
