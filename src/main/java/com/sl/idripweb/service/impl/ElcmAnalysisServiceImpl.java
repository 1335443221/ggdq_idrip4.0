package com.sl.idripweb.service.impl;

import com.sl.idripweb.dao.ElcmAnalysisDao;
import com.sl.idripweb.service.ElcmAnalysisService;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("elcmAnalysisServiceImpl")
public class ElcmAnalysisServiceImpl implements ElcmAnalysisService {

	@Autowired
	private ElcmAnalysisDao elcmAnalysisDao;
	@Autowired
	DateUtil dateUtil;

	/**
	 * 故障分析首页
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult malAnalysisIndex(Map<String, Object> map) {
		if (map.get("type")==null||String.valueOf(map.get("type")).equals("")) {
			return WebResult.error(201,"请选择统计方式!");
		}
		if(map.get("begin_time")!=null&&!String.valueOf(map.get("begin_time")).equals("")){
			map.put("begin_time",map.get("begin_time")+" 00:00:00");
		}
		if(map.get("end_time")!=null&&!String.valueOf(map.get("end_time")).equals("")){
			map.put("end_time",map.get("end_time")+" 23:59:59");
		}

		if (map.get("device_id")!=null&&!String.valueOf(map.get("device_id")).equals("")){
			map.put("device_list", Arrays.asList(String.valueOf(map.get("device_id")).split(",")));
		}
		if (map.get("device_type_id")!=null&&!String.valueOf(map.get("device_type_id")).equals("")){
			map.put("device_type_list",Arrays.asList(String.valueOf(map.get("device_type_id")).split(",")));
		}

		Map<String, Object> result=new HashMap<>();
		//故障次数
		Map<String, Object> malCount=getMalCount(map);
		result.put("malCount",malCount);
		//故障程度饼图
		Map<String, Object> urgencyCount=getMalUrgencyCount(map);
		result.put("urgencyCount",urgencyCount);
		//设备-故障  设备类型-故障 柱状图
		Map<String, Object> malDeviceCount=getMalDeviceCount(map);
		result.put("malDeviceCount",malDeviceCount);
		return WebResult.success(result);
	}
	/**
	 * 故障分析 折线图
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult malCountByCycle(Map<String, Object> map) {
		if (map.get("cycle_type")==null||String.valueOf(map.get("cycle_type")).equals("")) {
			return WebResult.error(201,"请选择统计周期!");
		}
		if(map.get("begin_time")!=null&&!String.valueOf(map.get("begin_time")).equals("")){
			map.put("begin_time",map.get("begin_time")+" 00:00:00");
		}
		if(map.get("end_time")!=null&&!String.valueOf(map.get("end_time")).equals("")){
			map.put("end_time",map.get("end_time")+" 23:59:59");
		}
		if (map.get("device_id")!=null&&!String.valueOf(map.get("device_id")).equals("")){
			map.put("device_list",Arrays.asList(String.valueOf(map.get("device_id")).split(",")));
		}
		if (map.get("device_type_id")!=null&&!String.valueOf(map.get("device_type_id")).equals("")){
			map.put("device_type_list",Arrays.asList(String.valueOf(map.get("device_type_id")).split(",")));
		}

		Map<String, Object> result=new HashMap<>();
		List<String> legend=new ArrayList<>();
		legend.add("故障次数");
		legend.add("转维修次数");
		//====
		String cycle_type=String.valueOf(map.get("cycle_type"));  //周期
		int year=DateUtil.getYear(new Date());
		String begin_time=year+" 01-01";
		String end_time=year+" 12-31";
		if (map.get("begin_time")!=null&&!String.valueOf(map.get("begin_time")).equals("")&&
				map.get("end_time")!=null&&!String.valueOf(map.get("end_time")).equals("")){
			begin_time=String.valueOf(map.get("begin_time"));
			end_time=String.valueOf(map.get("end_time"));
		}

		List<String> xAxis=new ArrayList<>();  //X轴
		List<String> xAxisData=new ArrayList<>();  //X轴返回数据
		List<Map<String, Object>> series =new ArrayList<>();  //数据
		Map<String, Object> malSeries =new HashMap<>();  //故障数据
		List<Integer> malData =new ArrayList<>();  //故障数据
		Map<String, Object> repairSeries =new HashMap<>();  //转维修数据
		List<Integer> repairData =new ArrayList<>();  //转维修数据
		List<Map<String, Object>> repairList=new ArrayList<>();
		List<Map<String, Object>> malList=new ArrayList<>();
		switch (cycle_type){
			case "周":
				//获取所有周
				Map<String,String> weekData=DateUtil.getAllWeeks(begin_time,end_time);
				xAxisData =new ArrayList(weekData.keySet());

				for (int i=0;i<xAxisData.size();i++){
					xAxis.add("第"+(i+1)+"周"+"("+weekData.get(xAxisData.get(i))+")");
				}
				//数据
				map.put("is_repair","1");
				repairList=elcmAnalysisDao.getMalWeekCount(map);
				map.remove("is_repair");
				malList=elcmAnalysisDao.getMalWeekCount(map);
				break;
			case "月":
				//获取所有月份
				xAxis=dateUtil.getMonthListOfDate(begin_time,end_time);
				xAxisData=xAxis;
				//数据
				map.put("is_repair","1");
				repairList=elcmAnalysisDao.getMalMonthCount(map);
				map.remove("is_repair");
				malList=elcmAnalysisDao.getMalMonthCount(map);
				break;
			case "年":
				//获取所有年份
				xAxis=DateUtil.getYearList(begin_time,end_time);
				for (int i=0;i<xAxis.size();i++){
					xAxisData.add(xAxis.get(i).replace("年",""));
				}
				//数据
				map.put("is_repair","1");
				repairList=elcmAnalysisDao.getMalYearCount(map);
				map.remove("is_repair");
				malList=elcmAnalysisDao.getMalYearCount(map);
				break;
		}
		//转维修数据
		Map<String, Object> repairMap=new HashMap<>();
		for (int i=0;i<repairList.size();i++){
			repairMap.put(String.valueOf(repairList.get(i).get("time")),repairList.get(i).get("count"));
		}
		//故障数据
		Map<String, Object> malMap=new HashMap<>();
		for (int i=0;i<malList.size();i++){
			malMap.put(String.valueOf(malList.get(i).get("time")),malList.get(i).get("count"));
		}

		for (int i=0;i<xAxisData.size();i++){
			if (malMap.get(xAxisData.get(i))==null){
				malData.add(0);
			}else{
				malData.add(Integer.parseInt(String.valueOf(malMap.get(xAxisData.get(i)))));
			}
			if (repairMap.get(xAxisData.get(i))==null){
				repairData.add(0);
			}else{
				repairData.add(Integer.parseInt(String.valueOf(repairMap.get(xAxisData.get(i)))));
			}
		}

		result.put("legend",legend);
		malSeries.put("name","故障次数");
		malSeries.put("data",malData);
		repairSeries.put("name","转维修次数");
		repairSeries.put("data",repairData);
		series.add(malSeries);
		series.add(repairSeries);
		result.put("series",series);
		result.put("xAxis",xAxis);
		return WebResult.success(result);
	}

	/**
	 * 故障分析 故障次数
	 * @param
	 * @param map
	 * @return
	 */
	public Map<String, Object> getMalCount(Map<String, Object> map) {
		//故障次数
		Map<String, Object> malCount=new HashMap<>();
		int mal_count=elcmAnalysisDao.getMalCount(map);
		map.put("is_repair","1");
		int repair_count=elcmAnalysisDao.getMalCount(map);
		map.remove("is_repair");
		malCount.put("mal_count",mal_count);
		malCount.put("repair_count",repair_count);
		//平均时间
		double averageTime=elcmAnalysisDao.getMalRepairAverageTime(map);
		malCount.put("average_time",(double)Math.round(averageTime*100)/100);
		//维修各状态次数
		List<Map<String, Object>> repairStatusCount=elcmAnalysisDao.getMalRepairStatusCount(map);
		int repairing_count=0; //维修中
		int complete_count=0; //已完成
		int cancel_count=0; //已取消
		for(int i=0;i<repairStatusCount.size();i++){
			int repair_status= Integer.parseInt(String.valueOf(repairStatusCount.get(i).get("repair_status")));
			int status_count= Integer.parseInt(String.valueOf(repairStatusCount.get(i).get("status_count")));
			if (repair_status==6){  //已完成
				complete_count=complete_count+status_count;
			}else if (repair_status==7){  //已取消
				cancel_count=cancel_count+status_count;
			}else{
				repairing_count=repairing_count+status_count;
			}
		}
		malCount.put("repairing_count",repairing_count);
		malCount.put("complete_count",complete_count);
		malCount.put("cancel_count",cancel_count);
		return malCount;

	}

	/**
	 * 故障分析 故障程度饼图
	 * @param
	 * @param map
	 * @return
	 */
	public Map<String, Object> getMalUrgencyCount(Map<String, Object> map) {
		Map<String, Object> result=new HashMap<>();
		Map<String, Object> urgencyMap=getMalUrgencyData(map);  //全部高中低
		map.put("is_repair","1");
		Map<String, Object> repairUrgencyMap=getMalUrgencyData(map);  //转维修高中低
		map.put("is_repair","0");
		Map<String, Object> notRepairUrgencyMap=getMalUrgencyData(map);  //不转维修部高中低
		map.remove("is_repair");
		//内圈
		Map<String, Object> innerRing=new HashMap<>();
		innerRing.put("high",urgencyMap.get("高")==null?0:urgencyMap.get("高"));
		innerRing.put("middle",urgencyMap.get("中")==null?0:urgencyMap.get("中"));
		innerRing.put("low",urgencyMap.get("低")==null?0:urgencyMap.get("低"));
		//外圈
		Map<String, Object> outerRing=new HashMap<>();
		outerRing.put("high_repair",repairUrgencyMap.get("高")==null?0:repairUrgencyMap.get("高"));
		outerRing.put("middle_repair",repairUrgencyMap.get("中")==null?0:repairUrgencyMap.get("中"));
		outerRing.put("low_repair",repairUrgencyMap.get("低")==null?0:repairUrgencyMap.get("低"));

		outerRing.put("high_not_repair",notRepairUrgencyMap.get("高")==null?0:notRepairUrgencyMap.get("高"));
		outerRing.put("middle_not_repair",notRepairUrgencyMap.get("中")==null?0:notRepairUrgencyMap.get("中"));
		outerRing.put("low_not_repair",notRepairUrgencyMap.get("低")==null?0:notRepairUrgencyMap.get("低"));

		result.put("innerRing",innerRing);
		result.put("outerRing",outerRing);
		return result;

	}
	//处理数据库数据 返回 高中低--count
	public Map<String, Object> getMalUrgencyData(Map<String, Object> map) {
		List<Map<String, Object>> urgencyCountList= elcmAnalysisDao.getMalUrgencyCount(map);
		Map<String, Object> urgencyMap=new HashMap<>();
		for (int i=0;i<urgencyCountList.size();i++){
			urgencyMap.put(String.valueOf(urgencyCountList.get(i).get("urgency")),urgencyCountList.get(i).get("urgency_count"));
		}
		return urgencyMap;
	}

	// 设备-故障--柱状图
	public Map<String, Object> getMalDeviceCount(Map<String, Object> map) {
		Map<String, Object> result=new HashMap<>();
		List<Map<String, Object>> list=new ArrayList<>();
		if (String.valueOf(map.get("type")).equals("1")){
			list= elcmAnalysisDao.getDeviceTypeMalCount(map);

		}
		if (String.valueOf(map.get("type")).equals("2")){
			list= elcmAnalysisDao.getDeviceMalCount(map);
		}
		List<String> xAxis=new ArrayList<>();
		List<String> yAxis=new ArrayList<>();
		for (int i=0;i<list.size();i++){
			xAxis.add(String.valueOf(list.get(i).get("xAxis")));
			yAxis.add(String.valueOf(list.get(i).get("yAxis")));
		}
		result.put("xAxis",xAxis);
		result.put("series",yAxis);
		return result;
	}


}
