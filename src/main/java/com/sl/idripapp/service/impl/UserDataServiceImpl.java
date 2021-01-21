package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.JwtToken;
import com.sl.idripapp.dao.AppRegisterUserDao;
import com.sl.idripapp.service.UserDataService;
import com.sl.idripweb.dao.WebCommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("userDataImpl")
public class UserDataServiceImpl implements UserDataService {
	@Autowired
	private AppRegisterUserDao appRegisterUserDao;
	@Autowired
	private WebCommonDao webCommonDao;


	/**
	 * app登录验证 智慧园区
	 */
	@Override
	public AppResult zhyqLogin(Map<String, Object> map) {
		Map<String, Object> map3 = new HashMap<String, Object>(); // 返给接口的数据
		Map<String, Object> result = new HashMap<String, Object>(); // data数据
		if (map.get("account") == null || map.get("password") == null) {
			return AppResult.error("1003");
		} else {
			map3.put("phone", map.get("account"));
			map3.put("pwd", map.get("password"));
			ArrayList<HashMap<String, Object>> dataList = appRegisterUserDao.query(map3);
			if(dataList.size()==0){
				return AppResult.error("1005");
			}else{
				Map<String, Object> user=dataList.get(0);
				//token内容
				JSONObject tokenData = new JSONObject();
				tokenData.put("uid",user.get("uid"));   //user表id
				tokenData.put("oper_pwd",user.get("oper_pwd"));
				tokenData.put("id",user.get("id"));  //注册表id
				tokenData.put("user_name",user.get("u_name"));
				tokenData.put("name",user.get("name"));
				tokenData.put("module",user.get("module"));
				tokenData.put("project_id",user.get("project_id"));
				tokenData.put("factory_id",user.get("factory_id"));
				tokenData.put("code_name",user.get("code_name"));
				tokenData.put("device_role",user.get("device_role"));

				//返回内容
				result.put("id",user.get("id"));
				result.put("uid",user.get("uid"));
				result.put("module",user.get("module"));
				result.put("portrait",user.get("portrait"));
				result.put("phone",user.get("phone"));
				result.put("name",user.get("name"));
				result.put("project_name",user.get("project_name"));
				result.put("project_id",user.get("project_id"));
				result.put("factory_id",user.get("factory_id"));
				result.put("factory_name",user.get("factory_name"));
				result.put("token", JwtToken.createToken(tokenData));  ///生成token
				Map<String, Object> smart_electricity = new HashMap<String, Object>(); // 智用电
				if("1".equals(String.valueOf(user.get("zyd_open")))){
					smart_electricity.put("auth",true);
				}else{
					smart_electricity.put("auth",false);
				}
				result.put("smart_electricity",smart_electricity);
				Map<String, Object> electrical_fire = new HashMap<String, Object>(); // 火灾
				electrical_fire.put("fire_level",user.get("fire_level"));
				result.put("electrical_fire",electrical_fire);
				Map<String, Object> collection_payment = new HashMap<String, Object>(); // 收缴费
				collection_payment.put("sjf_role",user.get("sjf_role"));
				result.put("collection_payment",collection_payment);
				Map<String, Object> device_management = new HashMap<String, Object>(); // 设备管理
				device_management.put("device_role",user.get("device_role"));
				device_management.put("device_role_name",user.get("device_role_name"));
				result.put("device_management",device_management);
				return AppResult.success(result);
			}
		}
	}




	/**
	 * app登录验证 北自智用电
	 */
	@Override
	public Object checkLogin(Map<String, Object> map) {
		Map<String, Object> map2 = new HashMap<String, Object>(); // 最终返回数据
		Map<String, Object> map3 = new HashMap<String, Object>(); // 返给接口的数据
		Map<String, Object> result = new HashMap<String, Object>(); // data数据
		if (map.get("account") == null || map.get("account") == "" || map.get("password") == ""|| map.get("password") == null) {
			return "1003";
		} else {
			map3.put("phone", map.get("account"));
			map3.put("pwd", map.get("password"));
			ArrayList<HashMap<String, Object>> dataList = appRegisterUserDao.query(map3);
			if(dataList.size()==0){
				return "1005";
			}else{
				Map<String, Object> user=dataList.get(0);
				//token内容
				JSONObject tokenData = new JSONObject();
				tokenData.put("uid",user.get("uid"));
				tokenData.put("oper_pwd",user.get("oper_pwd"));
				tokenData.put("id",user.get("id"));
				tokenData.put("user_name",user.get("u_name"));
				tokenData.put("name",user.get("name"));
				tokenData.put("module",user.get("module"));
				tokenData.put("project_id",user.get("project_id"));
				tokenData.put("factory_id",user.get("factory_id"));
				tokenData.put("code_name",user.get("code_name"));

				//返回内容
				result.put("id",user.get("id"));
				result.put("uid",user.get("uid"));
				result.put("module",user.get("module"));
				result.put("portrait",user.get("portrait"));
				result.put("phone",user.get("phone"));
				result.put("name",user.get("name"));
				result.put("project_name",user.get("project_name"));
				result.put("project_id",user.get("project_id"));
				result.put("factory_id",user.get("factory_id"));
				result.put("factory_name",user.get("factory_name"));
				try {
					result.put("token", JwtToken.createToken(tokenData));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Map<String, Object> smart_electricity = new HashMap<String, Object>(); // 智用电
				if("1".equals(String.valueOf(user.get("zyd_open")))){
					smart_electricity.put("auth",true);
				}else{
					smart_electricity.put("auth",false);
				}
				result.put("smart_electricity",smart_electricity);
				Map<String, Object> electrical_fire = new HashMap<String, Object>(); // 火灾
				electrical_fire.put("fire_level",user.get("fire_level"));
				result.put("electrical_fire",electrical_fire);
				Map<String, Object> collection_payment = new HashMap<String, Object>(); // 收缴费
				collection_payment.put("sjf_role",user.get("sjf_role"));
				result.put("collection_payment",collection_payment);
				return AppResult.success(result);
			}
		}
	}



	
	
	/**
	 * app登录验证
	 */
	@Override
	public Object checkLogin2(Map<String, Object> map) {
		Map<String, Object> map3 = new HashMap<String, Object>(); // 返给接口的数据
		Map<String, Object> map4 = new HashMap<String, Object>(); // data数据
		if (map.get("account") == null || map.get("account") == "" || map.get("password") == ""|| map.get("password") == null) {
			return "1003";
		} else {
			String password = map.get("password").toString(); 
			map3.put("uname", map.get("account"));
			ArrayList<HashMap<String, Object>> userInfo = appRegisterUserDao.getUserInfo(map3);
			if (userInfo == null || userInfo.size() == 0) {
				return "1005";
			}
			HashMap<String, Object> userData = userInfo.get(0);
			if (userData.get("pwd").equals(password)) { // 密码相等，登录成功
				if (userData.get("operate_system").toString().contains("2")) {
					HashMap<String, Object> userData2 = new HashMap();
					userData2.put("uid", userData.get("uid"));
					userData2.put("oper_pwd", userData.get("oper_pwd"));
					userData2.put("name", userData.get("name"));
					map4.put("id", Integer.parseInt(userData.get("uid").toString())); // 用户id
					map4.put("auth", true);
					try {
						map4.put("token", JwtToken.createToken(userData2));
						int projectid = JwtToken.getProjectId(map4.get("token").toString());
						map4.put("project_id", projectid);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return AppResult.success(map4);   //登陆成功
				} else {
					return AppResult.error("1006");  //没有登录权限
				}

			} else { // 登录失败
				return  AppResult.error("1005");   //密码错误
			}
		}
	}
	
	
	
	
	
	
	/**
	 * web登录验证
	 */
	@Override
	public Map<String, Object> checkWebLogin(Map<String, Object> map) {
		Map<String, Object> map2 = new HashMap<String, Object>(); // 最终返回数据
		Map<String, Object> map3 = new HashMap<String, Object>(); // 返给接口的数据
		Map<String, Object> map4 = new HashMap<String, Object>(); // 返给接口的数据
		if (map.get("account") == null || map.get("account") == "" || map.get("password") == ""
				|| map.get("password") == null) {
			map2.put("code", "1003");
			map2.put("msg", "参数缺失");
		} else {
			String password = map.get("password").toString(); // MD5加密登录密码
			map3.put("uname", map.get("account"));
			ArrayList<HashMap<String, Object>> userInfo = appRegisterUserDao.getUserInfo(map3);
			if (userInfo == null || userInfo.size() == 0) {
				Map<String, Object> ma = new HashMap<String, Object>(); // 最终返回数据
				ma.put("code", "1005");
				ma.put("msg", "没有此用户");
				return ma;
			}
			HashMap<String, Object> userData = userInfo.get(0);
			if (userData.get("pwd").equals(password)) { // 密码相等，登录成功
					map2.put("code", "1000");
					map2.put("msg", "OK");
					
					map4.put("uid",userData.get("uid"));
					ArrayList<HashMap<String, Object>> userProjectInfo = appRegisterUserDao.getUserProjectInfo(map4);
					Map<String,Object> userData2 = new HashMap<>();
					if(userProjectInfo.size() > 0)
						userData2 = userProjectInfo.get(0);

					map2.put("uid",userData.get("uid"));
					map2.put("project_id", userData2.get("project_id"));
					map2.put("project_name", userData2.get("project_name"));
			} else { // 登录失败
				map2.put("code", "1005");
				map2.put("msg", "用户名/密码错误");
			}
		}
		return map2;
	}
	
	
	
	
	

	/**
	 * 验证操作密码
	 */
	@Override
	public Object checkOper_pwd(Map<String, Object> map) {
		if (map.get("opassword") == "" || map.get("opassword") == null) {
			return AppResult.error("1003");
		} else {
			String opassword = map.get("opassword").toString(); // MD5加密操作密码
			Gson gson = new Gson();
			Map<String, Object> userData = JwtToken.getAppUID(String.valueOf(map.get("token")));
			if (userData.get("oper_pwd").equals(opassword)) {
				return AppResult.success();
			} else {
				return AppResult.error("1005");
			}
		}
	}


	/**
	 * 注册用户
	 */
	@Override
	public Object registerUser(Map<String, Object> map) {
		if(map.get("phone")==null||map.get("pwd")==null){
			return "1003";
		}
		ArrayList<HashMap<String, Object>> dataList = appRegisterUserDao.query(new HashMap<>());
		for(int i=0;i<dataList.size();i++){
			if(map.get("phone").equals(dataList.get(i).get("phone"))||map.get("phone")==dataList.get(i).get("phone")){
				return AppResult.error("1014");
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mon = sdf.format(new Date());
		map.put("create_at", mon);  //创建时间
		if(map.get("user_id")==null){
			map.put("user_id", 113);
		}
		appRegisterUserDao.save(map);
		return AppResult.success();
	}
	
	
	
	@Override
	public Map<String, Object> getAllUser(int pageindex, String user_id){
		Map<String, Object> umap=new HashMap<>();
		umap.put("user_id", user_id);
		ArrayList<HashMap<String, Object>> dataList = appRegisterUserDao.query(umap);
		
		List<Map<String, Object>> dataList2=new ArrayList<>();
		int fana=(pageindex-1)*10;
		int fanb=pageindex*10;
		for(int i=fana;i<fanb;i++){
			if(i>=dataList.size()){
				break;
			}
			dataList2.add(dataList.get(i));
		}
		int totalPagecount = dataList.size()%10==0 ? dataList.size()/10 :dataList.size()/10+1;
		int lastpage =pageindex==1?pageindex:pageindex-1;
		int nextpage =pageindex==totalPagecount?pageindex:pageindex+1;
		Map<String, Object> map2=new HashMap<>();
		map2.put("pageindex", pageindex);
		map2.put("lastpage",lastpage);
		map2.put("nextpage",nextpage);
		map2.put("dataList", dataList2);
		map2.put("totalPagecount", totalPagecount);
		map2.put("recordCount", dataList.size());  
		return map2;
	}
	
	
	
	/**
	 * 修改密码
	 */
	@Override
	public Object updateOpenPwd(Map<String, Object> map) {
		if(map.get("oldpwd")==null||map.get("newpwd")==null){
			return "1003";
		}

		Map<String,Object> userData = JwtToken.getAppUID(String.valueOf(map.get("token")));
		ArrayList<HashMap<String, Object>> dataList = appRegisterUserDao.query(new HashMap<>());

		for(int i=0;i<dataList.size();i++){
			if(userData.get("id").toString().contains(dataList.get(i).get("id").toString())){
				if(!map.get("oldpwd").toString().equals(dataList.get(i).get("pwd").toString())){
					return AppResult.error("1015");
				}
			}
		}
		
		double d2=Double.parseDouble(userData.get("id").toString());
    	Double D2=new Double(d2); 
    	int i2=D2.intValue(); 
		
		
		Map<String,Object> map2=new HashMap<>();
		map2.put("id",i2);
		map2.put("pwd",map.get("newpwd"));
		appRegisterUserDao.update(map2);
		return AppResult.success();
	}


	@Override
	public Object registerUser2(Map<String, Object> map) {
		if(map.get("phone")==null||map.get("pwd")==null||map.get("user_id")==null){
			return "1003";
		}
		ArrayList<HashMap<String, Object>> dataList = appRegisterUserDao.query(new HashMap<>());

		for(int i=0;i<dataList.size();i++){
			if(map.get("phone").equals(dataList.get(i).get("phone"))||map.get("phone")==dataList.get(i).get("phone")){
				return AppResult.error("1014");
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mon = sdf.format(new Date());
		map.put("create_at", mon);  //创建时间
		map.put("user_id", Integer.parseInt(map.get("user_id").toString()));
		appRegisterUserDao.save(map);
		return AppResult.success();
	}


	/**
	 * 检查操作电价密码
	 */
	@Override
	public Object checkPricePwd(Map<String, Object> map) {
		if (map.get("pwd") == "" || map.get("pwd") == null) {
			return "1003";
		}
		//电价
		Map<String, Object> priceMap = new HashMap<String, Object>();
		priceMap.put("project_id", JwtToken.getProjectId(map.get("token") + ""));
		HashMap<String, Object> priceData = appRegisterUserDao.queryElecPrice(priceMap);

		if(String.valueOf(map.get("pwd")).equals(String.valueOf(priceData.get("pwd")))){  //密码相等
			return AppResult.success();
		}else {
			return "1005";
		}
	}



	@Override
	public Object updateElecPrice(Map<String, Object> map) {
		if (map.get("elec_price") == "" || map.get("elec_price") == null) {
			return "1003";
		}
		Map<String, Object> priceMap = new HashMap<String, Object>();
		priceMap.put("project_id", JwtToken.getProjectId(map.get("token") + ""));
		priceMap.put("elec_price", map.get("elec_price"));
		appRegisterUserDao.updateElecPrice(priceMap);
		return AppResult.success();
	}


	/**
	 * 请求电价的数据
	 */
	@Override
	public Object elecPriceData(Map<String, Object> map) {
		Map<String, Object> priceMap = new HashMap<String, Object>();
		priceMap.put("project_id", JwtToken.getProjectId(map.get("token") + ""));
		HashMap<String, Object> priceData = appRegisterUserDao.queryElecPrice(priceMap);// 电价列表
		JSONObject parseObject3=new JSONObject();
		parseObject3.put("project_id", priceData.get("project_id"));
		parseObject3.put("elec_price", priceData.get("elec_price"));
		return AppResult.success(parseObject3);
	}



	/**
	 * 修改密码
	 */
	@Override
	public AppResult updatePassword(Map<String, Object> map){
		return null;
	};

	/**
	 * 绑定手机
	 */
	@Override
	public AppResult bindPhone(Map<String, Object> map){
		Map<String,Object> userData = JwtToken.getAppUID(String.valueOf(map.get("token")));
		Map<String,Object> map2=new HashMap<>();
		map2.put("id",userData.get("id"));
		map2.put("bind_phone",map.get("bind_phone"));
		int i = appRegisterUserDao.updateElecPrice(map2);
		if(i > 0){
			return AppResult.success();
		}else{
			return AppResult.error("1010");
		}

	};




	@Override
	public Object getUserList(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<Map<String, Object>> userList = webCommonDao.getUserList(map);
		return AppResult.success(userList);
	}


	//========智运行web 修改用户===========//

	/**
	 * 修改用户  修改 app用户 以及 user表用户
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public Object updateAppUserAndUser(Map<String, Object> map) {
		int result=	 appRegisterUserDao.updateProjectUser(map);
		//修改role 角色
		if (result>0&&map.get("role_id")!=null){
			appRegisterUserDao.updateProjectRoleUser(map);
		}
		return result;
	}
	/**
	 * 修改用户  修改 app用户 以及 user表用户
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public Object insertAppUserAndUser(Map<String, Object> map) {
		//新增app user表用户
		//	int result=	appRegisterUserDao.insertAppUser(map);
		//新增user表用户
		int result= appRegisterUserDao.insertProjectUser(map);
		//添加role-user信息
		if(result>0){
			Map<String, Object> roleMap=new HashMap<>();
			roleMap.put("uid",map.get("id"));
			roleMap.put("rid",map.get("role_id"));
			appRegisterUserDao.insertProjectRoleUser(roleMap);
		}
		return map.get("id");
	}
	/**
	 * 修改用户  修改 app用户 以及 user表用户
	 * @param map
	 * @return
	 */
	@Override
	public Object deleteAppUserAndUser(Map<String, Object> map) {
		//新增app user表用户
		//	int result=	appRegisterUserDao.deleteAppUser(map);
		//新增user表用户
		return appRegisterUserDao.deleteProjectUser(map);
	}

	/**
	 * 新增session
	 * @param map
	 * @return
	 */
	@Override
	public int insertSession(Map<String, Object> map) {
		return appRegisterUserDao.insertSession(map);
	}

}
