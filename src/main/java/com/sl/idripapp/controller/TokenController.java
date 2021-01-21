package com.sl.idripapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.sl.common.config.ConstantConfig;
import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/token")
public class TokenController {
	@Autowired
	UserDataService userDataImpl;
	@Autowired
    ConstantConfig constant;

	/**
	 * app登录接口（用app注册表登录  新的智慧园区登录）
	 * @param
	 * @return
	 */
	@RequestMapping("/zhyqLogin")
	@ResponseBody
	public AppResult zhyqLogin(@RequestParam Map<String, Object> map){
		return userDataImpl.zhyqLogin(map);
	}




	
	/**
	 * app登录接口（用app注册表登录  智用电登录 为了兼容北京电力自动化）
	 * @param
	 * @return
	 */
	@RequestMapping("/checkLogin")
	@ResponseBody
	public Object checkLogin(@RequestParam Map<String, Object> map){
		return userDataImpl.checkLogin(map);
	}
	
	/**
	 * app登录接口2（用user表登录）
	 * @param
	 * @return
	 */
	@RequestMapping("/checkLogin2")
	@ResponseBody
	public Object checkLogin2(@RequestParam Map<String, Object> map){
		return userDataImpl.checkLogin2(map);
	}





	
	/**
	 * web登录接口
	 * @param
	 * @return
	 */
	@RequestMapping("/checkWebLogin")
	@ResponseBody
	public Object checkWebLogin(@RequestParam Map<String, Object> map,HttpSession session, HttpServletResponse response){
		Map<String, Object> map2=userDataImpl.checkWebLogin(map);
		if("1000".equals(map2.get("code"))){
			Map<String, Object> userMap=new HashMap<>();
			userMap.put("uid",map2.get("uid"));
			userMap.put("project_id", map2.get("project_id"));
			userMap.put("project_name", map2.get("project_name"));
			session.setAttribute("activeAdmin",userMap);  //把当前登陆用户放入session
			Cookie userCookie=new Cookie("app_session",session.getId());
			userCookie.setMaxAge(24*60*60);   //存活期为一天 24*60*60
			userCookie.setPath("/");
			response.addCookie(userCookie);

			Map<String,Object> sessionMap=new HashMap<>();
			String json= JSONObject.toJSONString(userMap);
			sessionMap.put("app_session",session.getId());
			sessionMap.put("app_value",json);
			userDataImpl.insertSession(sessionMap);
			return "1";
		}else{
			return "0";
		}
	}


	/**
	 * 修改密码
	 * @param
	 * @return
	 */
	@RequestMapping("/updatePassword")
	@ResponseBody
	public AppResult updatePassword(@RequestParam Map<String, Object> map){
		return userDataImpl.updatePassword(map);
	}
	/**
	 * 绑定手机
	 * @param
	 * @return
	 */
	@RequestMapping("/bindPhone")
	@ResponseBody
	public AppResult bindPhone(@RequestParam Map<String, Object> map){
		return userDataImpl.bindPhone(map);
	}




	//ios 服务支持
	@RequestMapping("/test")
	public String test(@RequestParam Map<String, Object> map){
		return "pmpappPag/test";
	}
	
	@RequestMapping("/iosPrivacy")
	public String iosPrivacy(@RequestParam Map<String, Object> map){
		return "pmpappPag/iosPrivacy";
	}
	
	@RequestMapping("/iosSupport")
	public String iosTechnical(@RequestParam Map<String, Object> map){
		return "pmpappPag/iosTechnical";
	}






	
}
