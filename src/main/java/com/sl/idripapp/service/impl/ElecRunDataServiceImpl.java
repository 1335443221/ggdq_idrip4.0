package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.common.config.ConstantConfig;
import com.sl.common.service.CommonService;
import com.sl.common.utils.JwtToken;
import com.sl.common.utils.AppResult;
import com.sl.idripapp.dao.AppRegisterUserDao;
import com.sl.idripapp.dao.ElecRunDao;
import com.sl.idripapp.service.ElecRunDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: ElecRunDataServiceImpl
 * Description: APP - 智用电 模块 实时数据模块 业务逻辑层
 */
@Service("runDataImpl")
public class ElecRunDataServiceImpl implements ElecRunDataService {
	@Autowired
    ConstantConfig constant;
	@Autowired
	ElecRunDao elecRunDao;
	@Autowired
	CommonService commonService;
	@Autowired
	AppRegisterUserDao appRegisterUserDao;

	/**
	 * 获取进线列表
	 */
	@Override
	public Object getCoilinList(Map<String, Object> map) {
		if (map.get("factory_id")==null||map.get("rid")==null){
			return AppResult.error("1003");
		}
		if(map.get("pageSize")==null||map.get("pageNum")==null){
			map.put("pageNum",1);
			map.put("pageSize",20);
		}
		int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
		int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
		map.put("fromNum",fromNum);

		//根据token获取project_id
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		//厂区信息
		Map<String, Object> factory = commonService.getFactoryByPid(map).get(0);
		//配电室信息
		Map<String, Object> transformerroom = elecRunDao.getCategoryTransformerroom(map).get(0);
		// 进线列表数据
		ArrayList<Map<String, Object>> elecLineList = elecRunDao.getElecRoomAndLineListByRid(map);
		// 进线列表数据数量
		int total = elecRunDao.getElecRoomAndLineListByRidCount(map);
		//redis数据
		HashMap<String, Object> redisData=commonService.getRedisData(elecLineList,constant.getElecMeter());
		//电价 如果没有返回null
		HashMap<String, Object> price = appRegisterUserDao.queryElecPrice(map);
		// 开关 ep  如果没有开关di 或者ep标签 则返回-1
		for (int i = 0; i < elecLineList.size(); i++) {
			String epTag=elecLineList.get(i).get("tg_id") + "_" +elecLineList.get(i).get("device_name")+"_"+"ep";
			String diTag=elecLineList.get(i).get("tg_id") + "_" +elecLineList.get(i).get("device_name")+"_"+"di";
			elecLineList.get(i).put("ep",redisData.get(epTag)==null?-1:redisData.get(epTag));
			elecLineList.get(i).put("di",redisData.get(diTag)==null?-1: (int)Double.parseDouble(redisData.get(diTag).toString()));
			//电价
			elecLineList.get(i).put("elec_price",price==null?0:price.get("elec_price"));
		}

		//返回数据
		LinkedHashMap<String,Object> result=new LinkedHashMap<>();
		if((fromNum+pageSize)>=total){
			result.put("is_lastpage",true);
		}else{
			result.put("is_lastpage",false);
		}
		result.put("factory_id", factory.get("factory_id"));
		result.put("factory_name", factory.get("factory_name"));
		result.put("rid", transformerroom.get("rid"));
		result.put("rname", transformerroom.get("category_name"));
		result.put("coilin_list", elecLineList);
		return AppResult.success(result);
	}


	/**
	 * 获取进线数据
	 */
	@Override
	public Object getCoilinDetail(Map<String, Object> map) {
		if (map.get("tg_id") == null || map.get("device_name") == null || map.get("panel_id") == null) {
			return "1003";
		}
		//获取标签
		List<Map> deviceTagList= JSONArray.parseArray(constant.getDeviceTagList(),Map.class);
		//获取redis数据
		List<Map<String, Object>> redisParamsList=new ArrayList<>();
		redisParamsList.add(map);
		HashMap<String, Object> redisData=commonService.getRedisData(redisParamsList,constant.getElecMeter());
		//redis key的前缀  eg:TG435_a1_b1_
		String keyPrefix = map.get("tg_id")+"_"+map.get("device_name")+"_";
		//详情结果集
		for (int i = 0; i < deviceTagList.size(); i++) {
			Map<String, String> deviceTag = deviceTagList.get(i);
			String name = deviceTag.get("name");
			String tag = deviceTag.get("value");
			//如果有, 说明是多个在一排
			if (tag.contains(",")) {
				List<String> tagList=Arrays.asList(tag.split(","));
				StringBuffer value=new StringBuffer("");;
				for (String each:tagList) {
					if (redisData.get(keyPrefix+each)==null) {
						value.append("0 / ");
					}else {
						value.append(redisData.get(keyPrefix+each)+" / ");
					}
				}
				//把最后的/去掉
				if (value.substring(value.length() - 2).equals("/ ")) {
					value.deleteCharAt(value.length() - 1);
					value.deleteCharAt(value.length() - 1);
				}
				deviceTagList.get(i).put("value",value);
			} else {
				if (redisData.get(keyPrefix+tag) == null) {
					//如果是开关 返回-1
					if (name.equals("Di")) {
						deviceTagList.get(i).put("value", "-1");
					} else {
						deviceTagList.get(i).put("value", "0.00");
					}
				} else {
					//如果是视在功率 Loadrate
					if (name.equals("Loadrate")) {
						deviceTagList.get(i).put("value", String.format("%.2f", Double.parseDouble(redisData.get(keyPrefix+tag).toString()) / 1250 * 60000));
					} else if(name.equals("Di")) {
						//去掉小数后边数据
						deviceTagList.get(i).put("value", redisData.get(keyPrefix+tag).toString().substring(0,1));
					}else{
						deviceTagList.get(i).put("value", redisData.get(keyPrefix+tag).toString());
					}
				}
			}
		}

		//出线列表
		ArrayList<Map<String, Object>> outLineData = elecRunDao.getChildrenCategoryList(String.valueOf(map.get("panel_id")));
		//出线redis数据
		HashMap<String, Object>  outLineRedisData= commonService.getRedisData(outLineData,constant.getElecMeter());
		//电价
		HashMap<String, Object> price = appRegisterUserDao.queryElecPrice(map);
		//出线数据的ep和di
		for (int i = 0; i < outLineData.size(); i++) {
			String outLineEpTag=outLineData.get(i).get("tg_id")+"_"+outLineData.get(i).get("device_name")+"_ep";
			String outLineDiTag=outLineData.get(i).get("tg_id")+"_"+outLineData.get(i).get("device_name")+"_di";
			if (outLineRedisData.get(outLineEpTag)==null){
				outLineData.get(i).put("ep", -1);
			}else{
				outLineData.get(i).put("coilout_ep",outLineRedisData.get(outLineEpTag));
			}
			if (outLineRedisData.get(outLineDiTag)==null){
				outLineData.get(i).put("di", -1);
			}else{
				outLineData.get(i).put("di",(int)Double.parseDouble(outLineRedisData.get(outLineDiTag).toString()));
			}
			//电价
			outLineData.get(i).put("elec_price",price==null?0:price.get("elec_price"));
		}

		LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("details_list", deviceTagList);
		result.put("coilout_list", outLineData);
		return AppResult.success(result);
	}

	/**
	 * 出线数据
	 */
	@Override
	public Object getCoiloutDetail(Map<String, Object> map) {
		if (map.get("tg_id") == null || map.get("device_name") == null || map.get("panel_id") == null) {
			return "1003";
		}
		//获取标签
		List<Map> deviceTagList= JSONArray.parseArray(constant.getDeviceTagList(),Map.class);
		//获取redis数据
		List<Map<String, Object>> redisParamsList=new ArrayList<>();
		redisParamsList.add(map);
		HashMap<String, Object> redisData=commonService.getRedisData(redisParamsList,constant.getElecMeter());
		//redis key的前缀  eg:TG435_a1_b1_
		String keyPrefix = map.get("tg_id")+"_"+map.get("device_name")+"_";
		//详情结果集
		for (int i = 0; i < deviceTagList.size(); i++) {
			Map<String, String> deviceTag = deviceTagList.get(i);
			String name = deviceTag.get("name");
			String tag = deviceTag.get("value");
			//如果有, 说明是多个在一排
			if (tag.contains(",")) {
				List<String> tagList=Arrays.asList(tag.split(","));
				StringBuffer value=new StringBuffer("");;
				for (String each:tagList) {
					if (redisData.get(keyPrefix+each)==null) {
						value.append("0 / ");
					}else {
						value.append(redisData.get(keyPrefix+each)+" / ");
					}
				}
				//把最后的/去掉
				if (value.substring(value.length() - 2).equals("/ ")) {
					value.deleteCharAt(value.length() - 1);
					value.deleteCharAt(value.length() - 1);
				}
				deviceTagList.get(i).put("value",value);
			} else {
				if (redisData.get(keyPrefix+tag) == null) {
					//如果是开关 返回-1
					if (name.equals("Di")) {
						deviceTagList.get(i).put("value", "-1");
					} else {
						deviceTagList.get(i).put("value", "0.00");
					}
				} else {
					//如果是视在功率 Loadrate
					if (name.equals("Loadrate")) {
						deviceTagList.get(i).put("value", String.format("%.2f", Double.parseDouble(redisData.get(keyPrefix+tag).toString()) / 1250 * 60000));
					} else if(name.equals("Di")) {
						//去掉小数后边数据
						deviceTagList.get(i).put("value", redisData.get(keyPrefix+tag).toString().substring(0,1));
					}else{
						deviceTagList.get(i).put("value", redisData.get(keyPrefix+tag).toString());
					}
				}
			}
		}
		return AppResult.success(deviceTagList);
	}

	/**
	 * 开关接口
	 */
	@Override
	public Object setDi(Map<String, Object> map) {

		return AppResult.error("1012");
	}

}
