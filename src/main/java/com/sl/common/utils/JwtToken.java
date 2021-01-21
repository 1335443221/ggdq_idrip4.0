package com.sl.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sl.common.exception.AppMyException;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * APP登录Token的生成和解析
 * 
 */
public class JwtToken {
	
	/** token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj */
	public static final String SECRET = "JKKLJOoasdlfj";
	/** token 过期时间: 10天 */
	public static final int calendarField = Calendar.DATE;
	public static final int calendarInterval = 10;
 
	public static void main(String[] args) {
		long a=1;
		try {
			System.out.println(JwtToken.createToken(a));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * JWT生成Token.<br/>
	 * 
	 * JWT构成: header, payload, signature
	 * 
	 * @param user_id
	 *            登录成功后用户user_id, 参数user_id不可传空
	 */
	public static String createToken(Object user_id){
		Date iatDate = new Date();
		// expire time
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(calendarField, calendarInterval);
		Date expiresDate = nowTime.getTime();
 
		// header Map
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");
       String token="";
		try {
		token = JWT.create().withHeader(map) // header
				.withClaim("iss", "Service") // payload
				.withClaim("aud", "APP").withClaim("user_id", null == user_id ? null : user_id.toString())
				.withIssuedAt(iatDate) // sign time
				.withExpiresAt(expiresDate) // expire time
				.sign(Algorithm.HMAC256(SECRET)); // signature
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}
 
	/**
	 * 解密Token
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Claim> verifyToken(String token) {
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			jwt = verifier.verify(token);
		} catch (Exception e) {
			// token 校验失败, 抛出Token验证非法异常
			throw new AppMyException("1001");
		}
		return jwt.getClaims();
	}
 
	/**
	 * 根据Token获取数据
	 * 
	 * @param token
	 * @return user_id
	 */
	public static Map<String, Object> getAppUID(String token) {
		Map<String, Claim> claims = verifyToken(token);
		Claim user_id_claim = claims.get("user_id");
		if (null == user_id_claim || StringUtils.isEmpty(user_id_claim.asString())) {
			// token 校验失败, 抛出Token验证非法异常
			throw new AppMyException("1002");
		}
		return JSONObject.parseObject(user_id_claim.asString());
	}
	
	
/*	*//**
	 * 通过token获取项目id  废弃方法
	 * @param token
	 * @return
	 *//*
	public static String getProject_id(String token){
		Gson g=new Gson();
		Map<String, Object> map2= new HashMap<String, Object>();   //返回数据
		map2.put("uid",JwtToken.getAppUID(token).get("uid"));
		String GET_URL = constant.getPhpCenterUrl() + "user/get_user_project_info";   //获取线上接口的数据
		HttpClientService hc=new HttpClientService();
		String data = hc.post(GET_URL,map2); //接口数据String类型
		Map userData2=g.fromJson(data, Map.class);
		return String.valueOf(userData2.get("project_id"));
	}*/


	/**
	 * 通过token获取项目id
	 * @param token
	 * @return
	 */
	public static int getProjectId(String token){
		return (Integer) JwtToken.getAppUID(token).get("project_id");
	}

	/**
	 * 通过token获取厂区(园区)默认id
	 * @param token
	 * @return
	 */
	public static int getFactoryId(String token){
		return (Integer) JwtToken.getAppUID(token).get("factory_id");
	}

	/**
	 * 通过token获取userName(app注册表)
	 * @param token
	 * @return
	 */
	public static String getUserName(String token){
		return String.valueOf(JwtToken.getAppUID(token).get("name"));
	}

	/**
	 * 通过token获取userid(app注册表)
	 * @param token
	 * @return
	 */
	public static String getUserId(String token){
		return String.valueOf(JwtToken.getAppUID(token).get("id"));
	}


	/**
	 * 通过token获取user表的id
	 * @param token
	 * @return
	 */
	public static String getProjectUserId(String token){
		return String.valueOf(JwtToken.getAppUID(token).get("uid"));
	}

	/**
	 * 通过token获取CodeName
	 * @param token
	 * @return
	 */
	public static String getCodeName(String token){
		return String.valueOf(JwtToken.getAppUID(token).get("code_name"));
	}


	/**
	 * 通过token获取设备全生命周期用户角色信息
	 * @param token
	 * @return
	 */
	public static String getDeviceRole(String token){
		return String.valueOf(JwtToken.getAppUID(token).get("device_role"));
	}
	
	
	
	
	
	
}
