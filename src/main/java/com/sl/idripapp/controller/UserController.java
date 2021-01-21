package com.sl.idripapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.sl.idripapp.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserDataService userDataImpl;
	
	/**
	 * 万达酒店_能耗app版本接口
	 * 验证操作密码
	 * @param user
	 * @return
	 */
	@RequestMapping("/checkOper_pwd")
	@ResponseBody
	public Object checkOper_pwd(@RequestParam Map<String, Object> map){
		return userDataImpl.checkOper_pwd(map);
	}
	
	
	
	
	/**
	 * 登陆页面
	 * @return
	 */
	@RequestMapping("/login")
	public String login(HttpSession session){
		session.invalidate();
		return "pmpappPag/login";
	}
	
	
	/**
	 * 后台管理主页面
	 * @param tg
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/index")
	public String index(String tg){
		return "pmpappPag/index";
	}
	
	
	
	/**
	 * 注册用户
	 * @param user
	 * @return
	 */
	@RequestMapping("/registerUser")
	@ResponseBody
	public Object register(@RequestParam Map<String, Object> map){
		
		return userDataImpl.registerUser(map);
	}
	
	
	/**
	 * 注册用户（根据项目分配）
	 * @param user
	 * @return
	 */
	@RequestMapping("/registerUser2")
	@ResponseBody
	public Object registerUser2(@RequestParam Map<String, Object> map){
		
		return userDataImpl.registerUser2(map);
	}
	
	
	/**
	 * 修改密码
	 * @param user
	 * @return
	 */
	@RequestMapping("/updateOpenPwd")
	@ResponseBody
	public Object updateOpenPwd(@RequestParam Map<String, Object> map){
		
		return userDataImpl.updateOpenPwd(map);
	}
	
	
	/**
	 * 展示所有用户
	 * @param stg
	 * @return
	 */
	@RequestMapping("/showAllUser")
	public String  showAllVersion(int pageindex,Model model,HttpSession session){
		Map<String, Object> map= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		Map<String, Object> l= userDataImpl.getAllUser(pageindex,map.get("uid").toString());
		model.addAttribute("user", l);
		return "pmpappPag/userPag/showUser";
	}
	
	
	
	/**
	 * 验证电价密码
	 * @param user
	 * @return
	 */
	@RequestMapping("/checkPricePwd")
	@ResponseBody
	public Object checkPricePwd(@RequestParam Map<String, Object> map){
		
		return userDataImpl.checkPricePwd(map);
	}
	
	
	/**
	 * 请求电价数据
	 * @param user
	 * @return
	 */
	@RequestMapping("/elecPriceData")
	@ResponseBody
	public Object elecPriceData(@RequestParam Map<String, Object> map){
		
		return userDataImpl.elecPriceData(map);
	}
	
	
	/**
	 * 修改电价
	 * @param user
	 * @return
	 */
	@RequestMapping("/updateElecPrice")
	@ResponseBody
	public Object updateElecPrice(@RequestParam Map<String, Object> map){
		
		return userDataImpl.updateElecPrice(map);
	}


	/**
	 * 退出  销毁session
	 * @param session
	 * @return
	 */
	@RequestMapping("/quit")
	public String quit(HttpSession session){
		session.invalidate();
		return "pmpappPag/login";
	}



	/**
	 * 用户列表  user表 项目下的所有用户
	 * @param
	 * @return
	 */
	@RequestMapping("/getUserList")
	@ResponseBody
	public Object getUserList(@RequestParam Map<String, Object> map){
		return userDataImpl.getUserList(map);
	}


	//========智运行web 修改用户===========//
	/**
	 * 修改用户  修改 app用户 以及 user表用户
	 * @param
	 * @return
	 */
	@RequestMapping("/updateAppUserAndUser")
	@ResponseBody
	public Object updateAppUserAndUser(@RequestParam Map<String, Object> map){
		return userDataImpl.updateAppUserAndUser(map);
	}

	/**
	 * 添加用户  添加 app用户 以及 user表用户
	 * @param
	 * @return
	 */
	@RequestMapping("/insertAppUserAndUser")
	@ResponseBody
	public Object insertAppUserAndUser(@RequestParam Map<String, Object> map){
		return userDataImpl.insertAppUserAndUser(map);
	}

	/**
	 * 删除用户   删除 app用户 以及 user表用户
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteAppUserAndUser")
	@ResponseBody
	public Object deleteAppUserAndUser(@RequestParam Map<String, Object> map){
		return userDataImpl.deleteAppUserAndUser(map);
	}


}
