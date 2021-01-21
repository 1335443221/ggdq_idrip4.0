package com.sl.idripapp.controller;

import com.sl.common.config.ConstantConfig;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.QiniuUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/qiniu")
public class QiniuController {

	@Autowired
	private ConstantConfig constantConfig;
	@Autowired
	private QiniuUpload qiniuUpload;

	private String sepa = File.separator;

	/**
	 * 获取七牛存储空间
	 * @return
	 */
	@RequestMapping("/getBucket")
	@ResponseBody
	public AppResult getQiNiuBucket(@RequestParam Map<String, Object> map){
 		List<Map<String,Object>> list=new ArrayList<>();
		Map<String,Object> bucket=new HashMap<>();
		bucket.put("id",1);
		bucket.put("name","ovelec_gt");
		list.add(bucket);
		bucket=new HashMap<>();
		bucket.put("id",2);
		bucket.put("name","ovelec_app");
		list.add(bucket);
		return AppResult.success(list);
	}
	

	/**
	 * 获取覆盖凭证
	 * @return
	 */
	@RequestMapping("/getCoverUpToken")
	@ResponseBody
	public AppResult getCoverUpToken(@RequestParam Map<String, String> map){
		return qiniuUpload.getCoverUpToken(map);
	}
	
	/**
	 * 获取简单上传凭证
	 * @return
	 */
	@RequestMapping("/getUpToken")
	@ResponseBody
	public AppResult getUpToken(@RequestParam Map<String, String> map){
		return qiniuUpload.getUpToken(map);
	}
	
	
	/**
	 * 获取简单上传凭证2
	 * @return
	 */
	@RequestMapping("/ovelec_gt_UpToken")
	@ResponseBody
	public Object  getUpToken2(@RequestParam Map<String, String> map){
		Map<String, String> map2=new HashMap<>();
        map2.put("upToken", qiniuUpload.getUpToken2());
		return AppResult.success(map2);
	}

	/**
	 * 地址
	 * @return
	 */
	@RequestMapping("/url")
	@ResponseBody
	public Object  url(@RequestParam Map<String, String> map){
		return AppResult.success(constantConfig.getQiniuUrl());
	}

	/**
	 * 替代七牛的公用上传方法(单个上传)
	 * @param
	 * @return
	 */
	@RequestMapping("/singleUpload")
	@ResponseBody
	public AppResult singleUpload(@RequestParam Map<String, String> map, @RequestParam MultipartFile file){
		//上传绝对路径 服务器地址  UrlConfig.UPLOAD  +"qiniu/日期/文件名"
		//访问地址   返回数据  UrlConfig.DOWNURL  +"qiniu/日期/文件名"
		String originalFilename = file.getOriginalFilename();
		String fileTyle=originalFilename.substring(originalFilename.lastIndexOf("."),originalFilename.length());
		String fileName = map.get("fileName");
		if(StringUtils.isEmpty(fileName))
			fileName = Calendar.getInstance().getTimeInMillis()+file.getOriginalFilename();
		else
			fileName += fileTyle;
		System.out.println("fileName:"+fileName);
		String filePath = "zyd"+sepa+new SimpleDateFormat("yyMMdd").format(new Date())+sepa;
		String path = constantConfig.getFileUploadUrl() +filePath + fileName;
		System.out.println("path:"+path);
		File uploadFile = new File(path);
		// 检测是否存在目录
		if (!uploadFile.getParentFile().exists()) {
			try{
				uploadFile.getParentFile().mkdirs();// 新建文件夹
			}catch (Exception e){
				return AppResult.error("1032");
			}
		}
		String downloadUrl = constantConfig.getFileDownloadUrl() + filePath + fileName;
		if(uploadFile.exists()) return AppResult.success(downloadUrl);
		try {
			file.transferTo(uploadFile);// 文件写入
		} catch (IOException e) {
			e.printStackTrace();
			return AppResult.error("1010");
		}
		return AppResult.success(downloadUrl);
	}
	
	
}
	
