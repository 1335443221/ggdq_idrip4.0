package com.sl.idripapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.sl.idripapp.service.VersionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/version")
public class VersionController {
	
	@Autowired
	VersionDataService versionDataImpl;
	
	/**
	 * 获取当前版本
	 * @param stg
	 * @return
	 */
	@RequestMapping("/activeVersion")
	public String  getActiveVersion(@RequestParam Map<String, Object> map,Model model,HttpSession session){
		Map<String, Object> map2= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("project_id", map2.get("project_id"));
		Map<String, Object> m= versionDataImpl.getActiveVersion(map);
		model.addAttribute("version", m);
		return "pmpappPag/versionPag/activeVersion";
	}
	
	
	/**
	 * 去Android增加页面
	 * @param stg
	 * @return
	 */
	@RequestMapping("/goAddAndroidVersion")
	public String  goAddVersion(){
		return "pmpappPag/versionPag/addAndroidVersion";
	}
	
	/**
	 * 去IOS增加页面
	 * @param stg
	 * @return
	 */
	@RequestMapping("/goAddIOSVersion")
	public String  addIOSVersion(){
		return "pmpappPag/versionPag/addIOSVersion";
	}
	
	
	/**
	 * 上传版本Android
	 * @param stg
	 * @return
	 */
	@RequestMapping("/insertVersion")
	@ResponseBody
	public int  insertVersion(@RequestParam Map<String, Object> map,MultipartFile file){
		int i= versionDataImpl.insertVersion(map,file);
		return i;
	}
	
	/**
	 * 上传版本IOS
	 * @param stg
	 * @return
	 */
	@RequestMapping("/insertIOSVersion")
	@ResponseBody
	public int  insertIOSVersion(@RequestParam Map<String, Object> map){
		int i= versionDataImpl.insertIOSVersion(map);
		return i;
	}
	
	
	
	/**
	 * 去修改页面
	 * @param stg
	 * @return
	 */
	@RequestMapping("/goUpdateVersion")
	public String  goUpdateVersion(Integer id,Model model,int app_type){
		Map<String, Object> m=versionDataImpl.getVersionById(id);
		model.addAttribute("version", m);
		if(app_type==1){
			return "pmpappPag/versionPag/updateAndroidVersion";
		}else{
			return "pmpappPag/versionPag/updateIOSVersion";
		}
		
	}
	
	
	/**
	 * 修改版本
	 * @param stg
	 * @return
	 */
	@RequestMapping("/updateVersion")
	@ResponseBody
	public int  updateVersion(@RequestParam Map<String, Object> map,MultipartFile file){
		int i= versionDataImpl.updateVersion(map,file);
		return i;
	}
	
	
	/**
	 * 展示所有版本
	 * @param stg
	 * @return
	 */
	@RequestMapping("/showAllVersion")
	public String  showAllVersion(@RequestParam Map<String, Object> map,Model model,HttpSession session){
		Map<String, Object> map2= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("project_id",map2.get("project_id"));
		Map<String, Object> l= versionDataImpl.getAllVersion(map);
		model.addAttribute("version", l);
		return "pmpappPag/versionPag/showVersion";
	}
	
	/**
	 * 删除
	 * @param stg
	 * @return
	 */
	@RequestMapping("/deleteVersionById")
	@ResponseBody
	public int  deleteVersionById(@RequestParam Map<String, Object> map,HttpSession session){
		Map<String, Object> map2= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("project_id",map2.get("project_id"));
		int i= versionDataImpl.deleteVersionById(map);
		return i;
	}
	
	
	
	/**
	 * 判断版本号是否重复
	 * @param addjob_number
	 * @return
	 */
	@RequestMapping("/checkVersionid")
	@ResponseBody
	public String checkVersionId(@RequestParam Map<String, Object> map){
		if(versionDataImpl.checkVersion(map)>0){
			return "1";
		}else{
			return "0";
		}
	}
	
	
	
	/**
	 * app版本更新
	 * @param 
	 * @return
	 */
	@RequestMapping("/appupdate")
	@ResponseBody
	public Object appupdate(@RequestParam Map<String, Object> map){
		
		return versionDataImpl.appUpdate(map);
		
	}


	/**
	 * app版本更新
	 * @param
	 * @return
	 */
	@RequestMapping("/appCheckUpdate")
	@ResponseBody
	public Object appCheckUpdate(@RequestParam Map<String, Object> map){

		return versionDataImpl.appCheckUpdate(map);

	}
	
	/**
	 * app版本更新
	 * @param 
	 * @return
	 */
	@RequestMapping("/ios_appupdate")
	@ResponseBody
	public Object ios_appupdate(@RequestParam Map<String, Object> map){
		
		return versionDataImpl.ios_appupdate(map);
		
	}
	
}
	
