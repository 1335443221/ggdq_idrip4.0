package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;


public interface UserDataService {
	//智慧园区登录
	public AppResult zhyqLogin(Map<String, Object> map);




	//登录验证
	public Object checkLogin(Map<String, Object> map);
	
	//登录验证
	public Object checkLogin2(Map<String, Object> map);


	public AppResult updatePassword(Map<String, Object> map);
	public AppResult bindPhone(Map<String, Object> map);

	//登录验证
	public Map<String, Object> checkWebLogin(Map<String, Object> map);
	
	//验证操作密码
	public Object checkOper_pwd(Map<String, Object> map);
	
	//注册用户
	public Object registerUser(Map<String, Object> map);
	
	//注册用户
	public Object registerUser2(Map<String, Object> map);
	
	
	//修改密码
	public Object updateOpenPwd(Map<String, Object> map);
	
	//所有注册用户
	public Map<String, Object> getAllUser(int pageindex,String user_id);
	
	
	//验证电价密码
	public Object checkPricePwd(Map<String, Object> map);
	
	public Object elecPriceData(Map<String, Object> map);
		
	//修改电价
	public Object updateElecPrice(Map<String, Object> map);


	//用户列表
	public Object getUserList(Map<String, Object> map);


	//修改用户  修改 app用户 以及 user表用户
	public Object updateAppUserAndUser(Map<String, Object> map);

	//添加用户  添加 app用户 以及 user表用户
	public Object insertAppUserAndUser(Map<String, Object> map);

	//删除用户  删除 app用户 以及 user表用户
	public Object deleteAppUserAndUser(Map<String, Object> map);

	//新增session
	public int insertSession(Map<String, Object> map);

	
	
	
}
