package com.sl.idripapp.service.impl;

import com.google.gson.Gson;
import com.sl.common.config.RedisConfig;
import com.sl.common.entity.UIDataVo;
import com.sl.common.entity.params.SetValParams;
import com.sl.common.service.CommonService;
import com.sl.common.service.UIDataDaoImpl;
import com.sl.common.utils.*;
import com.sl.idripapp.dao.FireMonitorDao;
import com.sl.idripapp.service.AlermDataService;
import com.sl.idripapp.service.FireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;

@Service("fireDataImpl")
public class FireServiceImpl implements FireService {

    @Autowired
    private DateUtil dateUtil;
	@Autowired
	FireMonitorDao fireMonitorDao;
	@Autowired
	private RedisPoolUtil redisPoolUtil;
	@Autowired
	RedisConfig redisConfig;
	@Autowired
	private CommonService commonService;
	@Autowired
	UIDataDaoImpl UIDataDaoImpl;
	@Autowired
	AlermDataService alermDataService;

	/**
	 * 探测器-位置列表(切换设备)
	 * @param map
	 * @return
	 */
	@Override
	public AppResult detectorPositionList(Map<String,Object> map){
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		//园区(厂区)→楼→楼层→探测器
		//厂区集合
		ArrayList<Map<String,Object>> factoryList=fireMonitorDao.getFactory(map);
		//楼集合
		ArrayList<Map<String,Object>> buildingList=null;
		//楼层集合
		ArrayList<Map<String,Object>> floorList=null;
		//设备集合
		ArrayList<Map<String,Object>> detectorList=null;

		for(int i=0;i<factoryList.size();i++){
			buildingList=fireMonitorDao.getBuildingByFactoryId(factoryList.get(i));
			for(int j=0;j<buildingList.size();j++){
				floorList=fireMonitorDao.getFloorByBuildingId(buildingList.get(j));
				for(int k=0;k<floorList.size();k++){
					detectorList=fireMonitorDao.getDetector(floorList.get(k));
					floorList.get(k).put("detectorList",detectorList);
				}
				buildingList.get(j).put("floorList",floorList);
			}
			factoryList.get(i).put("buildingList",buildingList);
		}

		return  AppResult.success(factoryList);
	}


	/**
	 * 探测器-状态列表(主机→主线→探测器(没有主线 则为 主机→探测器))
	 * @param map
	 * @return
	 */
	@Override
	public AppResult detectorStateList(Map<String,Object> map){
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		// 主机→主线→探测器
		//配电室/主机集合
		ArrayList<Map<String,Object>> transformerroomList=fireMonitorDao.getTransformerroom(map);
		//主线/网关集合
		ArrayList<Map<String,Object>> threadList=null;
		//设备集合
		ArrayList<Map<String,Object>> detectorList=null;

		for(int i=0;i<transformerroomList.size();i++){
			threadList=fireMonitorDao.getThreadByTransformerroom(transformerroomList.get(i));
			if(threadList.size()==0){
				Map<String,Object> thread=new HashMap<>();
				thread.put("thread",null);
				detectorList=fireMonitorDao.getDetector(transformerroomList.get(i));
				thread.put("detectorList",detectorList);
				threadList.add(thread);
			}else{
				for(int j=0;j<threadList.size();j++){
					detectorList=fireMonitorDao.getDetector(threadList.get(j));
					threadList.get(j).put("detectorList",detectorList);
				}
			}
			transformerroomList.get(i).put("threadList",threadList);
		}
		return  AppResult.success(transformerroomList);
	}


	/**
	 * 探测器-设备信息
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getDetectorData(Map<String,Object> map){
		if(map.get("id")==null){
			return AppResult.error("1003");
		}
		map.remove("token");
		map.put("id",map.get("id"));
		//探测器
		Map<String, Object> detectorData = fireMonitorDao.getDetectorData(map);
		return  AppResult.success(detectorData);
	}
	/**
	 * 探测器-设备实时数据
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getData(Map<String,Object> map){
		if(map.get("device_id")==null||map.get("tagName")==null||map.get("id")==null){
			return AppResult.error("1003");
		}
		map.remove("token");
		map.put("device_id",map.get("device_id"));
		map.put("tagName",map.get("tagName"));
		map.put("id",map.get("id"));
		//探测器
		Map<String, Object> dataMap = new HashMap<>();
		map.put("is_redis",1);
		ArrayList<Map<String, Object>> tagMap = getAppFireTag(map);
		try {
			if (tagMap == null || tagMap.size() <= 0){
				return AppResult.success(dataMap);
			}
			DecimalFormat df = new DecimalFormat("0"); //保留整数
			Gson gson = new Gson();
			Set<byte[]> keySet = new HashSet<byte[]>();
			for (int m = 0; m < tagMap.size(); m++) {
				String _key = String.valueOf(map.get("tagName")) + ":" + String.valueOf(tagMap.get(m).get("tag_name"));
				keySet.add(_key.getBytes());
			}
			if (keySet.size() < 1){
				return AppResult.success(dataMap);
			}
			byte[][] values = redisPoolUtil.mget(keySet);
			byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
			HashMap<String, Object> data = new HashMap<String, Object>();
			for (int i = 0; i < keySet.size(); ++i) {
				if (values[i] == null) continue;
				HashMap<String, Object> fileMap = new HashMap<>();
				String val = new String(values[i], "utf-8");
				fileMap.put("val", df.format(Double.parseDouble(gson.fromJson(val, HashMap.class).get("val").toString())));
				String key = new String(keys[i], "utf-8");
				int index=key.indexOf(":");
				index=key.indexOf(":", index+1);
				data.put(key.substring(index+1), fileMap);
			}

			ArrayList<Map<String, Object>> alerm=getAlermByDeviceId(map);
			int size=alerm.size();
			if(size>0){
				dataMap.put("status",alerm.get(0).get("category_name"));
			}else{
				dataMap.put("status","正常");
			}

			for (int m = 0; m < tagMap.size(); m++) {
				if(data.get(tagMap.get(m).get("tag_name"))!=null){
					HashMap<String, Object> fileMap =(HashMap<String, Object>)data.get(tagMap.get(m).get("tag_name"));
					/// if(String.valueOf(tagMap.get(m).get("tag_name")).contains("i")){
					//     tagMap.get(m).put("val",Integer.parseInt(String.valueOf(fileMap.get("val")))*1000); //转换成mA
					// }else{
					tagMap.get(m).put("val",fileMap.get("val"));
					//}

				}else{
					tagMap.get(m).put("val","——");
				}
				tagMap.get(m).put("is_alarm",false);
				if(size>0){
					if (alerm.toString().contains(String.valueOf(tagMap.get(m).get("tag_name")))){
						tagMap.get(m).put("is_alarm",true);
					}
				}
				if(String.valueOf(tagMap.get(m).get("tag_name")).contains("t")){
					tagMap.get(m).put("unit","℃");
				}
				if(String.valueOf(tagMap.get(m).get("tag_name")).contains("i")){
					tagMap.get(m).put("unit","mA");
				}
			}
		} catch (Exception e) {
		}

		dataMap.put("values",tagMap);
		return AppResult.success(dataMap);
	}


	/**
	 * 探测器-获取园区的经纬度
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getFactory(Map<String,Object> map){
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		//探测器
		ArrayList<Map<String, Object>> factoryPosition = fireMonitorDao.getFactoryPosition(map);
		return  AppResult.success(factoryPosition);
	}

	/**
	 * 探测器-楼的横竖坐标
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getBuilding(Map<String,Object> map){
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		//探测器
		Map<String,Object> result=new HashMap<>();
		result.put("mapUrl",fireMonitorDao.getProjectInfo(map).get("map_url"));
		result.put("building",fireMonitorDao.getBuildingPosition(map));
		return  AppResult.success(result);
	}


	/**
	 * 探测器-分场景页面实时数据
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getLoopData(Map<String,Object> map){
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		return  AppResult.success(analysisLoopData(map));
	}

	/**
	 * 解析实时数据
	 * @param
	 * @return
	 */
	public List<Map<String,Object>>  analysisLoopData(Map<String,Object> map){
		Map<String,Object> resultMap=getAppDataByDeviceList(map); //所有实时数据信息
		List<Map> tagList= new ArrayList();
		Map<String,Map> dataListMap= new HashMap<>();
		if(resultMap.get("tagList")!=null){
			tagList=(List<Map>)resultMap.get("tagList");
		}
		if(resultMap.get("dataListMap")!=null){
			dataListMap=(Map<String,Map>)resultMap.get("dataListMap");
		}
		String loop_name=null;
		String device_tag=null;
		List<String> dateList=new ArrayList<>();

		List<Map<String,Object>> resultList=new ArrayList<>();
		Map<String,Object> dataMap=new HashMap<>();
		int a=0;
		Map map2=new HashMap();
		for(int i=0;i<tagList.size();i++){
			loop_name=String.valueOf(tagList.get(i).get("loop_name"));
			device_tag=String.valueOf(tagList.get(i).get("device_tag"));
			if (dateList.contains(loop_name)){  //已经有回路了
				a=dateList.indexOf(loop_name);
				if(device_tag.contains("_t")){  //温度
					if(dataListMap.get(device_tag)==null) {
						resultList.get(a).put("temperature","0.00");
						resultList.get(a).put("temperature_alarm",false);
					}else {
						map2=dataListMap.get(device_tag);
						resultList.get(a).put("temperature",map2.get("val"));
						resultList.get(a).put("temperature_alarm",map2.get("is_alarm"));
					}
				}
				if(device_tag.contains("_i")){  //电流
					if(dataListMap.get(device_tag)==null) {
						resultList.get(a).put("intensity","0.00");
						resultList.get(a).put("intensity_alarm",false);
					}else {
						map2=dataListMap.get(device_tag);
						resultList.get(a).put("intensity",map2.get("val"));
						resultList.get(a).put("intensity_alarm",map2.get("is_alarm"));
					}
				}
			}else{  //没回路
				dateList.add(loop_name);  //存回路
				dataMap=new HashMap<>();  //存回路
				dataMap.put("name",loop_name);
				//result.put(loop_name,new HashMap());
				if(device_tag.contains("_t")){  //温度
					if(dataListMap.get(device_tag)==null) {
						dataMap.put("temperature","0.00");
						dataMap.put("temperature_alarm",false);
					}else {
						map2=dataListMap.get(device_tag);
						dataMap.put("temperature",map2.get("val"));
						dataMap.put("temperature_alarm",map2.get("is_alarm"));
					}
				}
				if(device_tag.contains("_i")){  //电流
					if(dataListMap.get(device_tag)==null) {
						dataMap.put("intensity","0.00");
						dataMap.put("intensity_alarm",false);
					}else {
						map2=dataListMap.get(device_tag);
						dataMap.put("intensity",map2.get("val"));
						dataMap.put("intensity_alarm",map2.get("is_alarm"));
					}
				}
				resultList.add(dataMap);
			}
		}
		return resultList;
	}




	/**
	 * 探测器-分场景页面实时数据初始化
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getLoopInitData(Map<String,Object> map){
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		String modelUrl = fireMonitorDao.getModelUrl(map);
		Map<String,Object> result=new HashMap<>();
		result.put("model_url",modelUrl);
		result.put("loopData",analysisLoopData(map));
		return  AppResult.success(result);
	}



	/**
	 * 探测器-曲线数据
	 * @param map
	 * @return
	 */
	@Override
	public AppResult getCurveData(Map<String,Object> map){
		//  tag  tagName singleDay_date  section_date
		if(map.get("tag")==null||map.get("tagName")==null){
			return AppResult.error("1003");
		}
		String singleDay_date=String.valueOf(map.get("singleDay_date"));
		Map<String,Object> params=new HashMap<>();
		params.put("code_name", JwtToken.getCodeName(String.valueOf(map.get("token"))));
		params.put("tag",map.get("tag"));
		params.put("tg",String.valueOf(map.get("tagName")).split(":")[0]);
		params.put("device",String.valueOf(map.get("tagName")).split(":")[1]);
		params.put("bdate",dateUtil.getBeforeDayOfTime(singleDay_date+" 00:00:00",DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));
		params.put("edate",singleDay_date+" 23:59:59");

		//今日昨日曲线
		List<UIDataVo> results = UIDataDaoImpl.getFireUIData(params);
		List<Map> todayList=new ArrayList<>();
		List<Map> yesterdayList=new ArrayList<>();
		List<Map> highList=new ArrayList<>();
		List<Map> lowList=new ArrayList<>();
		Map<String,Object> val =null;
		String log_time =null;
		for(int i=0;i<results.size();i++){
			log_time =String.valueOf(results.get(i).getLog_time());
			if(("00".equals(log_time.substring(14, 16)))){
				val = new HashMap<>();
				val.put("time",Integer.parseInt(((String) (results.get(i).getLog_time())).substring(11,13)));
				val.put("value",Double.valueOf((String)results.get(i).getVal()));
					if(log_time.contains(singleDay_date)){
						todayList.add(val);
					}else{
						yesterdayList.add(val);
					}
			}
		}
		//最大最小值曲线
		int year=Integer.parseInt(String.valueOf(map.get("section_date")).split("-")[0]);
		int month=Integer.parseInt(String.valueOf(map.get("section_date")).split("-")[1]);
		String start=dateUtil.getFirstDayOfMonth(year,month);
		String end=dateUtil.getLastDayOfMonth(year,month);
		//List<String> dateList=dateUtil.findDatesList(start,end); //一个月的时间集合
		params.put("bdate",start+" 00:00:00"); //MongoDB起始时间
		params.put("edate",end+" 23:59:59"); //MongoDB结束时间
		results = UIDataDaoImpl.getFireUIData(params);

		List<String> dateList=new ArrayList<>();
		Map<String,List> dateMap=new HashMap<>();
		for(int i=0;i<results.size();i++){
			log_time =String.valueOf(results.get(i).getLog_time()).substring(0,10);
				if (dateList.contains(log_time)){
					dateMap.get(log_time).add(Double.valueOf((String)results.get(i).getVal()));
				}else{
					dateList.add(log_time); //新的一天 填进去
					dateMap.put(log_time,new ArrayList()); //日期
				}
		}
		//遍历取出最大最小值
		int i=1;
		Map<String,Object> high=null;
		Map<String,Object> low=null;
		for(Map.Entry<String,List> date:dateMap.entrySet()){
			high=new HashMap<>();
			low=new HashMap<>();
			high.put("time",i);
			low.put("time",i);
			high.put("value",Collections.max(date.getValue()));
			highList.add(high);
			low.put("value",Collections.min(date.getValue()));
			lowList.add(low);
			i++;

		}

		Map<String,Object> result=new HashMap<>();
		result.put("todayList",todayList);
		result.put("yesterdayList",yesterdayList);
		result.put("highList",highList);
		result.put("lowList",lowList);
		return  AppResult.success(result);
	}


	/**
	 * 探测器-下置复位
	 * @param
	 * @return
	 */
	@Transactional
	@Override
	public AppResult reset(Map<String,Object> map){
		SetValParams params=new SetValParams();
		params.setProjectId(JwtToken.getProjectId(String.valueOf(map.get("token"))));
		params.setFactoryId(Integer.parseInt(String.valueOf(map.get("factory_id"))));
		String[] temp = String.valueOf(map.get("tagName")).split(":");
		params.setTgName(temp[0]);
		params.setTag(temp[1]+"_di");
		params.setVal("1");
		//下置复位
		commonService.setVal(params);

		//处理全部
		Map<String, Object> paramsMap=new HashMap<>();
		paramsMap.put("msg","火灾探测器下置复位");
		paramsMap.put("uname", JwtToken.getUserName(String.valueOf(map.get("token"))));
		paramsMap.put("device_id",map.get("device_id"));
		paramsMap.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		int i = alermDataService.alermDisposeAll(paramsMap);
		return  AppResult.success(i);
	}


/////////----------------巡检----------------
	/**
	 * 探测器-巡检集合
	 * @param
	 * @return
	 */
	@Override
	public AppResult patrolList(Map<String,Object> map){
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
		Map<String, Object> result=new HashMap<>();
		try{
			int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
			map.put("fromNum",fromNum);
			map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
			map.remove("token");
			int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
			int total=fireMonitorDao.patrolListCount(map);
			if((fromNum+pageSize)>=total){
				result.put("is_lastPage",true);
			}else{
				result.put("is_lastpage",false);
			}
			result.put("patrolList",fireMonitorDao.patrolList(map));
		}catch (Exception e){
			System.out.println(e);
		}
		return  AppResult.success(result);
	}
	/**
	 * 探测器-新增巡检任务
	 * @param
	 * @return
	 */
	@Transactional
	@Override
	public AppResult addPatrol(Map<String,Object> map){
        if(map.get("is_implement")==null||map.get("patrol_type")==null||map.get("begin_time")==null){
			return AppResult.error("1003");
		}
		if("1".equals(String.valueOf(map.get("is_implement")))){ //是立即执行
			int type=Integer.parseInt(String.valueOf(map.get("patrol_type")));
			Date beginTime = DateUtil.parseStrToDate(map.get("begin_time")+" 00:00:00", DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS); //巡检开始时间
			Date endTime = DateUtil.parseStrToDate(map.get("end_time")+" 23:59:59", DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS); //巡检结束时间
            if(dateUtil.isEffectiveDate(new Date(),beginTime,endTime,DateUtil.DATE_FORMAT_YYYY_MM_DD)){
                map.put("is_create_log",1);
            }else{
                map.put("is_create_log",0);
            }
			if(type==1){ //日
				String date=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
				String week=dateUtil.getDayWeekOfDate1(new Date());
				map.put("patrol_date",date+" "+week);
			}
			if(type==2){ //周
				String date=DateUtil.parseDateToStr(dateUtil.getFirstDayOfWeek(new Date()),DateUtil.DATE_FORMAT_YYYY_MM_DD);
				map.put("patrol_date",date+" 星期一");
			}
			if(type==3){ //月
				String date=DateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYYMM);
				int year=Integer.parseInt(date.substring(0,4));
				int month=Integer.parseInt(date.substring(4,6));
				date=dateUtil.getFirstDayOfMonth(year,month);
				String week=dateUtil.getDayWeekOfDate1(DateUtil.parseStrToDate(date,DateUtil.DATE_FORMAT_YYYY_MM_DD));
				map.put("patrol_date",date+" "+week);
			}
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
		int result= fireMonitorDao.addPatrol(map);
		if("1".equals(String.valueOf(map.get("is_implement")))&&"1".equals(String.valueOf(map.get("is_create_log")))){ //立即执行并且在日期之间
			Map<String,Object> param=new HashMap<>();
			param.put("patrol_id",map.get("patrol_id"));
			param.put("patrol_date",map.get("patrol_date"));
			List<Map> list=new ArrayList<>();
			list.add(param);
			fireMonitorDao.addAllPatrolLog(list);
		}
		if(result == 0){
		    return AppResult.error("1010");
        }else{
            return  AppResult.success();
        }
	}
	/**
	 * 探测器-巡检详情
	 * @param
	 * @return
	 */
	@Override
	public AppResult patrolDetail(Map<String,Object> map){
        map.put("patrol_log_id",map.get("patrol_log_id"));
		Map<String, Object> result=fireMonitorDao.patrolDetail(map);
		result.put("malfunction_number",fireMonitorDao.malfunctionListCount(map));
        if("0".equals(String.valueOf(result.get("state")))||result.get("patrol_file")==null){
            result.put("patrol_file",new ArrayList<>());
        }else{
            result.put("patrol_file",String.valueOf(result.get("patrol_file")).split(","));
        }
		return  AppResult.success(result);
	}

	/**
	 * 探测器-巡检完成
	 * @param
	 * @return
	 */
	@Override
	public AppResult patrolComplete(Map<String,Object> map){
	    map.put("create_by", JwtToken.getUserId(String.valueOf(map.get("token"))));
        map.remove("token");
		Map<String,Object> params=new HashMap<>();
		params.put("patrol_log_id",map.get("patrol_log_id"));
		Map<String, Object> result=fireMonitorDao.patrolDetail(map);
		result.put("malfunction_number",fireMonitorDao.malfunctionListCount(map));
		//今天日期
		String date=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
		String patrol_date=String.valueOf(result.get("patrol_date")).substring(0,10);
		int type=Integer.parseInt(String.valueOf(result.get("patrol_type")));
		if(type==1){ //日
			if(patrol_date.equals(date)){
				map.put("state",1);
			}else{
				map.put("state",2);
			}
		}
		if(type==2){ //周
			Date patrolDate = DateUtil.parseStrToDate(String.valueOf(result.get("patrol_date")).substring(0, 10), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
			Date endTime=DateUtil.addDate(patrolDate,0,0,6,0,0,0,0);//周日
			if(dateUtil.isEffectiveDate(new Date(),patrolDate,endTime,DateUtil.DATE_FORMAT_YYYY_MM_DD)){
				map.put("state",1);
			}else{
				map.put("state",2);
			}

		}
		if(type==3){ //月
			String patrol_month=patrol_date.substring(0,7);
			String month=date.substring(0,7);
			if(patrol_month.equals(month)){
				map.put("state",1);
			}else{
				map.put("state",2);
			}
		}

		//完成巡检
		int i = fireMonitorDao.patrolComplete(map);
		//判断是否成功,并返回最新巡检信息
        if(i == 0){
            return AppResult.error("1010");
        }else{
			result=fireMonitorDao.patrolDetail(map);
			result.put("malfunction_number",fireMonitorDao.malfunctionListCount(map));
            if("0".equals(String.valueOf(result.get("state")))||result.get("patrol_file")==null){
                result.put("patrol_file",new ArrayList<>());
            }else{
                result.put("patrol_file",String.valueOf(result.get("patrol_file")).split(","));
            }
            return  AppResult.success(result);
        }
	}
	/**
	 * 探测器-隐患上报
	 * @param
	 * @return
	 */
	@Override
	public AppResult addMalfunction(Map<String,Object> map){
	    map.put("create_by", JwtToken.getUserName(String.valueOf(map.get("token"))));
	    map.put("is_deal",0);
        map.remove("token");
		int i = fireMonitorDao.addMalfunction(map);
		if(i == 0){
            return AppResult.error("1010");
        }else{
            return  AppResult.success();
        }
	}

	/**
	 * 探测器-隐患处理
	 * @param
	 * @return
	 */
	@Override
	public AppResult malfunctionDeal(Map<String,Object> map){
        map.put("deal_user", JwtToken.getUserName(String.valueOf(map.get("token"))));
        map.put("is_deal",1);
        map.remove("token");
		//探测器
		int data=fireMonitorDao.malfunctionDeal(map);
        if(data==0){
            return AppResult.error("1010");
        }else{
            Map<String,Object> result= fireMonitorDao.malfunctionDetail(map);
            if(result.get("malfunction_file")==null){
                result.put("malfunction_file",new ArrayList<>());
            }else{
                result.put("malfunction_file",String.valueOf(result.get("malfunction_file")).split(","));
            }
            if("0".equals(String.valueOf(result.get("is_deal")))||result.get("deal_file")==null){
                result.put("deal_file",new ArrayList<>());
            }else{
                result.put("deal_file",String.valueOf(result.get("deal_file")).split(","));
            }
            return  AppResult.success(result);
        }
	}


	/**
	 * 探测器-隐患管理列表
	 * @param
	 * @return
	 */
	@Override
	public AppResult malfunctionList(Map<String,Object> map){
		if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
			map.put("pageNum",1);
			map.put("pageSize",20);
		}
		int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
		map.put("fromNum",fromNum);
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		Map<String, Object> result=new HashMap<>();
		int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
		int total=fireMonitorDao.malfunctionListCount(map);
		if((fromNum+pageSize)>=total){
			result.put("is_lastPage",true);
		}else{
			result.put("is_lastpage",false);
		}
		result.put("malfunctionList",fireMonitorDao.malfunctionList(map));
		return  AppResult.success(result);
	}
	/**
	 * 探测器-隐患详情
	 * @param
	 * @return
	 */
	@Override
	public AppResult malfunctionDetail(Map<String,Object> map){
		map.remove("token");
		Map<String,Object> result= fireMonitorDao.malfunctionDetail(map);
		if(result.get("malfunction_file")==null){
			result.put("malfunction_file",new ArrayList<>());
		}else{
			result.put("malfunction_file",String.valueOf(result.get("malfunction_file")).split(","));
		}
		if("0".equals(String.valueOf(result.get("is_deal")))||result.get("deal_file")==null){
			result.put("deal_file",new ArrayList<>());
		}else{
			result.put("deal_file",String.valueOf(result.get("deal_file")).split(","));
		}
		return  AppResult.success(result);
	}

	@Cacheable("getAppFireTag")
	public ArrayList<Map<String, Object>> getAppFireTag(Map<String, Object> map) {
		return fireMonitorDao.getAppFireTag(map);
	}

	@Cacheable("getAlermByDeviceId")
	public ArrayList<Map<String, Object>> getAlermByDeviceId(Map<String, Object> map) {
		return fireMonitorDao.getAlermByDeviceId(map);
	}

	@Cacheable("getAppDetectorList")
	public ArrayList<Map<String, Object>> getAppDetectorList(Map<String, Object> map) {
		return fireMonitorDao.detectorList(map);
	}

	/**
	 * 获取APP实时数据
	 * @param map
	 * @return
	 */
	public Map<String, Object> getAppDataByDeviceList(Map<String, Object> map){
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> devices = getAppDetectorList(map);
		map.put("list",devices);
		ArrayList<Map<String, Object>> tagMap = getAppFireTag(map);
		resultMap.put("tagList",tagMap);
		map.put("is_redis",1);
		tagMap = getAppFireTag(map);
		ArrayList<Map<String, Object>> alerm=getAlermByDeviceId(map);
		int size=alerm.size();
		Map<String, Object> dataListMap=new HashMap<String, Object>();
		try {
			if (tagMap == null || tagMap.size() <= 0){
				return resultMap;
			}
			DecimalFormat df = new DecimalFormat("0"); //保留整数
			ArrayList<Map<String, Object>> devicesResult = new ArrayList<Map<String, Object>>();
			Gson gson = new Gson();
			Set<byte[]> keySet = new HashSet<byte[]>();
			for (int d = 0; d < devices.size(); d++) {
				Map<String, Object> device = devices.get(d);
				if (device == null || device.get("tagName") == null){
					continue;
				}
				for (int m = 0; m < tagMap.size(); m++) {
					String _key = String.valueOf(device.get("tagName")) + ":" + String.valueOf(tagMap.get(m).get("tag_name"));
					keySet.add(_key.getBytes());
				}
			}
			if (keySet.size() < 1){
				return resultMap;
			}
			byte[][] values = redisPoolUtil.mget(keySet);
			byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
			// key-value对
			for (int d = 0; d < devices.size(); d++) {
				if (keys.length < 1){
					continue;
				}
				for (int i = 0; i < keySet.size(); ++i) {
					dataMap = new HashMap<String, Object>();
					dataMap.put("is_alarm",false);
					String key = new String(keys[i], "utf-8");
					String key2=null;
					if (key.split(":").length >= 3) {
						key2=key.split(":")[1] + "_" + key.split(":")[2];
						if (values[i] == null) {
							dataMap.put("val","——");
						}else{
							String val = new String(values[i], "utf-8");
							dataMap.put("val",df.format(Double.parseDouble(gson.fromJson(val, dataMap.getClass()).get("val").toString())));
                                /*if(key.split(":")[2].contains("i")){
                                    dataMap.put("val",Integer.parseInt(String.valueOf(dataMap.get("val")))*1000); //转换成mA
                                }*/
						}
					}
					if(size>0){
						if (alerm.toString().contains(String.valueOf(dataMap.get("name")))){
							dataMap.put("is_alarm",true);
						}
					}
					dataListMap.put(key2,dataMap);
				}
			}
		} catch (Exception e) {
		}
		resultMap.put("dataListMap",dataListMap);
		return resultMap;
	}



}
