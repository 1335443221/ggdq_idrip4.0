package com.sl.idripapp.service.impl;

import com.sl.common.config.ConstantConfig;
import com.sl.common.utils.*;
import com.sl.idripapp.dao.AppEnergyVersionDao;
import com.sl.idripapp.service.VersionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * TODO 每次新项目, 需要更新writeApkAndIndexHtml方法 同时更新activeVersion.html中的二维码部分
 * app版本
 * @author 李旭日
 */
@Service("versionDataImpl")
public class VersionDataServiceImpl implements VersionDataService {
	@Autowired
	AppEnergyVersionDao appEnergyVersionDao;
	@Autowired
	ConstantConfig constantConfig;

	/**
	 * 获取当前版本号
	 */
	@Override
	public Map<String, Object> getActiveVersion(Map<String, Object> map) {

		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(map);
		if (dataList.size() == 0) {
			return null;
		} else {
			Map<String, Object> versionData = dataList.get(0);
			ArrayList<HashMap<String, Object>> dataList2 = appEnergyVersionDao.download_query(map);
			if (dataList2.size() != 0) {
				Map<String, Object> download_Data=dataList2.get(0);
				versionData.put("download",download_Data);
			}
			return versionData;
		}

	}

	/**
	 * 删除
	 */
	@Transactional
	@Override
	public int deleteVersionById(Map<String, Object> map) {
		appEnergyVersionDao.delete(map);

		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(map);
		if (dataList.size() != 0) {
			Map<String, Object> versionData = dataList.get(0);
			String url=String.valueOf(versionData.get("download_url"));

			ArrayList<HashMap<String, Object>> dataList2 = appEnergyVersionDao.download_query(map);
			Map<String, Object> download_Data=new HashMap<>();
			if (dataList2.size() != 0) {
				download_Data=dataList2.get(0);
			}
			String projectName =String.valueOf(download_Data.get("project_name"));
			if(!"".equals(projectName)){
				projectName=projectName+"/";
			}
			String indexUrl="/data/www/15javadev_app_v3/zyd/download/" + projectName + "index.html";

			try {
				FileOutputStream testfile = new FileOutputStream(indexUrl);
				testfile.write(new String("").getBytes());
				File file1 =new File(indexUrl);
				Writer out =new FileWriter(file1);
				String index_data="<head><meta http-equiv='refresh' content='0; url="+url+"'>" +
						"<meta http-equiv=\"Expires\" content=\"0\">\n" +
						"<meta http-equiv=\"Pragma\" content=\"no-cache\">\n" +
						"<meta http-equiv=\"Cache-control\" content=\"no-cache\">\n" +
						"<meta http-equiv=\"Cache\" content=\"no-cache\">\n" +
						"</head>";
				out.write(index_data);
				testfile.close();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return 1;
	}

	/**
	 * 增加版本
	 */
	@Override
	public int insertVersion(Map<String, Object> map, MultipartFile file) {
		int pid = Integer.parseInt(String.valueOf(map.get("project_id")));
		if (file == null) {
			return 0;
		}
		//写入apk
		boolean isUploadSuccess = writeApkAndIndexHtml(pid, file, map);
		if(!isUploadSuccess){
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mon = sdf.format(new Date());
		map.put("upload_at", mon);
		// 插入到数据库
		map.put("project_id", pid);
		return appEnergyVersionDao.save(map);
	}

	/**
	 * 修改版本
	 */
	@Override
	public int updateVersion(Map<String, Object> map, MultipartFile file) {
		int pid = Integer.parseInt(map.get("project_id").toString());
		if (file == null) {
			return 0;
		}
		//写入apk
		boolean isUploadSuccess = writeApkAndIndexHtml(pid, file, map);
		if(!isUploadSuccess){
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mon = sdf.format(new Date());
		map.put("upload_at", mon);
		map.put("project_id", pid);
		return appEnergyVersionDao.update(map);
	}
	
	/**
	 * 写入apk到硬盘, 写入最新的index.html
	 * @param pid 项目id
	 * @param file 上传的apk
	 * @param map 参数 
	 */
	private boolean writeApkAndIndexHtml(int pid,MultipartFile file,Map<String, Object> map){
		String filename = file.getOriginalFilename();
		FileOutputStream testfile = null;
		Writer out = null;
		boolean isUploadSuccess = false;

		ArrayList<HashMap<String, Object>> dataList2 = appEnergyVersionDao.download_query(map);
		Map<String, Object> download_Data=new HashMap<>();
		if (dataList2.size() != 0) {
			download_Data=dataList2.get(0);
		}
		try {
			//简化如下
			String projectName =String.valueOf(download_Data.get("project_name"));
			if(!"".equals(projectName)){
				projectName=projectName+"/";
			}

			String sqlPath = projectName + filename;
			String url = constantConfig.getDownloadUrl() + sqlPath;
			String filepath = "/data/www/15javadev_app_v3/zyd/download/" + projectName + filename;
			isUploadSuccess = FileUtils.upload(file, filepath);
			map.put("download_url", url);
			File file1 = new File("/data/www/15javadev_app_v3/zyd/download/" + projectName + "index.html");
			if(!file1.getParentFile().exists()){
				file1.getParentFile().mkdirs();
            }
			testfile = new FileOutputStream("/data/www/15javadev_app_v3/zyd/download/" + projectName + "index.html");
			testfile.write(new String("").getBytes());
			
			out = new FileWriter(file1);
			String data = "<head><meta http-equiv='refresh' content='0; url=" + url + "'>" +
					"<meta http-equiv=\"Expires\" content=\"0\">\n" +
					"<meta http-equiv=\"Pragma\" content=\"no-cache\">\n" +
					"<meta http-equiv=\"Cache-control\" content=\"no-cache\">\n" +
					"<meta http-equiv=\"Cache\" content=\"no-cache\">" +
					"</head>";
			out.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("文件写入异常 + " + isUploadSuccess,e);
		} finally {
			try {  
		        if (out != null) {  
		        	out.close();  
		        } 
		        if (testfile != null) {  
		        	testfile.close();  
		        }
		    } catch (Exception e) {  
		    	throw new RuntimeException("文件写入异常 + " + isUploadSuccess,e); 
		    } 
		}
		return isUploadSuccess;
	}

	/**
	 * 版本号校验
	 */
	@Override
	public int checkVersion(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(map);
		int a = 0;
		if (dataList != null) {
			if(map.get("version")!=null){
				for (int i = 0; i < dataList.size(); i++) {
					if (dataList.get(i).get("version").toString().equals(map.get("version").toString())) {
						a = 1;
					}
				}
			}
			
			if(map.get("ios_version")!=null){
				for (int i = 0; i < dataList.size(); i++) {
					if (dataList.get(i).get("ios_version").toString().equals(map.get("ios_version").toString())) {
						a = 1;
					}
				}
			}
			
			
		}

		return a;
	}

	/**
	 * 获取一个
	 */
	@Override
	public Map<String, Object> getVersionById(Integer id) {
		Map<String, Object> map2 = new HashMap<>();
		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(new HashMap<>());
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).get("id") == id) {
				map2 = dataList.get(i);
			}
		}
		return map2;
	}

	/**
	 * 获取全部
	 */
	@Override
	public Map<String, Object> getAllVersion(Map<String, Object> map) {
		int pageindex=Integer.parseInt(map.get("pageindex").toString());
		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(map);
		List<Map<String, Object>> dataList2 = new ArrayList<>();
		int fana = (pageindex - 1) * 3;
		int fanb = pageindex * 3;
		for (int i = fana; i < fanb; i++) {
			if (i >= dataList.size()) {
				break;
			}
			dataList2.add(dataList.get(i));
		}
		int totalPagecount = dataList.size() % 3 == 0 ? dataList.size() / 3 : dataList.size() / 3 + 1;
		totalPagecount=totalPagecount==0?1:totalPagecount;
		int lastpage = pageindex == 1 ? pageindex : pageindex - 1;
		int nextpage = pageindex == totalPagecount ? pageindex : pageindex + 1;
		Map<String, Object> map2 = new HashMap<>();
		map2.put("app_type", map.get("app_type"));
		map2.put("pageindex", pageindex);
		map2.put("lastpage", lastpage);
		map2.put("nextpage", nextpage);
		map2.put("dataList", dataList2);
		map2.put("totalPagecount", totalPagecount);
		map2.put("recordCount", dataList.size());
		return map2;
	}

	/**
	 * app版本更新
	 */
	@Override
	public Object appUpdate(Map<String, Object> map) {
		if (map.get("version") == null) {
			return "1003";
		}
			map.put("app_type", 1);  //安卓

		// 版本等于7，是强华和中粮项目， 直接返回不需要更新
		if (Integer.valueOf((String) map.get("version")) == 7) {
			Map<String, Object> resultData = new HashMap<>();
			resultData.put("is_latest", 2);
			return AppResult.success(resultData);
		}


		// 版本大于7，token不为空
		if (Integer.valueOf(String.valueOf(map.get("version"))) > 7) {
			// 根据token查询projectId，再根据projectId查询center
			if(map.get("project_id")!=null){
				String projectId =String.valueOf(map.get("project_id"));
				map.put("project_id", projectId);
			}else{
				String token = (String) map.get("token");
				if (token != null && token.length() > 0) {
					int projectId = JwtToken.getProjectId(token);
					map.put("project_id", projectId);
				}
			}

		}

		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(map);
		if (dataList.size() == 0) {
			return AppResult.error("1013");
		}
		Map<String, Object> versionData = dataList.get(0);
		int oldVersionCode = 0;
		int currentVersionCode = 0;
		try {
			oldVersionCode = Integer.valueOf(String.valueOf(versionData.get("version")));
			currentVersionCode = Integer.valueOf(String.valueOf(map.get("version")));
		} catch (NumberFormatException e) {
			// TODO: handle exception 无需处理
		}
		// 如果库里的版本小于=当前app安装的版本， 不用更新
		// 当前验证版本是否需要更新（1：需要；2：不需要-已是最新版本）
		if (oldVersionCode <= currentVersionCode) {
			versionData.put("is_latest", 2);
		} else {
			// 如果库里的版本大于当前app的版本， 需要更新
			versionData.put("is_latest", 1);
		}
		return AppResult.success(versionData);
	}

	/**
	 * app版本更新(新)
	 */
	@Override
	public Object appCheckUpdate(Map<String, Object> map) {
		if (map.get("version") == null) {
			return "1003";
		}
		if (map.get("app_module_type") == null) {
			return "1003";
		}
		map.put("app_type", 1);  //安卓

		// 根据token查询projectId，再根据projectId查询center
			if(map.get("project_id")!=null){
				String projectId =String.valueOf(map.get("project_id"));
				map.put("project_id", projectId);
			}else{
				String token = (String) map.get("token");
				if (token != null && token.length() > 0) {
					int projectId = JwtToken.getProjectId(token);
					map.put("project_id", projectId);
				}
			}

		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(map);
		if (dataList.size() == 0) {
			return AppResult.error("1013");
		}
		Map<String, Object> versionData = dataList.get(0);
		int oldVersionCode = 0;
		int currentVersionCode = 0;
		try {
			oldVersionCode = Integer.valueOf(String.valueOf(versionData.get("version")));
			currentVersionCode = Integer.valueOf(String.valueOf(map.get("version")));
		} catch (NumberFormatException e) {
			// TODO: handle exception 无需处理
		}
		// 如果库里的版本小于=当前app安装的版本， 不用更新
		// 当前验证版本是否需要更新（1：需要；2：不需要-已是最新版本）
		if (oldVersionCode <= currentVersionCode) {
			versionData.put("is_latest", 2);
		} else {
			// 如果库里的版本大于当前app的版本， 需要更新
			versionData.put("is_latest", 1);
		}
		return AppResult.success(versionData);
	}

	/**
	 * 更新ios
	 */
	@Override
	public int insertIOSVersion(Map<String, Object> map) {
		int pid = Integer.parseInt(map.get("project_id").toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mon = sdf.format(new Date());
		map.put("upload_at", mon);
		// 插入到数据库
		map.put("project_id", pid);
		return appEnergyVersionDao.save(map);
	}

	/**
	 * app检测ios版本
	 */
	@Override
	public Object ios_appupdate(Map<String, Object> map) {
		if (map.get("version") == null) {
			return "1003";
		}
		
		map.put("app_type", 2);  //ios

		ArrayList<HashMap<String, Object>> dataList = appEnergyVersionDao.query(map);
		if (dataList.size() == 0) {
			return AppResult.error("1013");
		}
		Map<String, Object> versionData = dataList.get(0);
		Map<String, Object> versionData2=new HashMap<>();
		versionData2.put("version", versionData.get("ios_version"));
		versionData2.put("upload_at", versionData.get("upload_at"));
		versionData2.put("download_url", versionData.get("download_url"));
		versionData2.put("description", versionData.get("description"));
		versionData2.put("id", versionData.get("id"));
		String oldVersionCode = String.valueOf(versionData2.get("version"));
		String currentVersionCode = String.valueOf(map.get("version"));
		// 如果库里的版本小于=当前app安装的版本， 不用更新
		// 当前验证版本是否需要更新（1：需要；2：不需要-已是最新版本）
		if (oldVersionCode.equals(currentVersionCode)) {
			versionData2.put("is_latest", 2);
		} else {
			// 如果库里的版本大于当前app的版本， 需要更新
			versionData2.put("is_latest", 1);
		}
		return AppResult.success(versionData2);
	}

}
