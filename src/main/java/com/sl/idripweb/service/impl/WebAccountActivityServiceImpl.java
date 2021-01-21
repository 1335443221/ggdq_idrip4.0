package com.sl.idripweb.service.impl;

import com.sl.common.utils.DateUtil;
import com.sl.common.utils.PageUtil;
import com.sl.common.utils.ResultHandleUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripapp.entity.AccountActivityTypeTree;
import com.sl.idripweb.dao.WebAccountActivityDao;
import com.sl.idripweb.service.WebAccountActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("WebAccountActivityServiceImpl")
public class WebAccountActivityServiceImpl implements WebAccountActivityService {

	@Autowired
	WebAccountActivityDao webAccountActivityDao;


	/**
	 * 获取所有类别
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getType(Map<String, Object> map) {
        //所有树的数据
        List<AccountActivityTypeTree>  typeList = webAccountActivityDao.getType(map);
        AccountActivityTypeTree elcmDeviceTypeTree=new AccountActivityTypeTree();
        //递归获取树结构
        List<AccountActivityTypeTree> result=elcmDeviceTypeTree.getAccountActivityTreeByRecursion(0,typeList,0);
        return WebResult.success(result);
	}
	/**
	 * 新增类别
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult addType(Map<String, Object> map) {
        int result= webAccountActivityDao.addType(map);
		if (result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 修改类别
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult updateType(Map<String, Object> map) {
        int result= webAccountActivityDao.updateType(map);
		if (result>0){
			return WebResult.success(200,"编辑成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 删除类别
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public WebResult deleteType(Map<String, Object> map) {
		int type_id=Integer.parseInt(String.valueOf(map.get("type_id")));
		//所有树的数据
		List<AccountActivityTypeTree>  typeList = webAccountActivityDao.getType(map);
		AccountActivityTypeTree elcmDeviceTypeTree=new AccountActivityTypeTree();
		//递归获取树结构
		List<AccountActivityTypeTree> typeTree=elcmDeviceTypeTree.getAccountActivityTreeByRecursion(type_id,typeList,0);
		//需要删除的类型
		List<Integer> deleteType=new ArrayList<>();
		getTypeTreeList(deleteType,typeTree);
		deleteType.add(type_id);
        webAccountActivityDao.deleteActivityByType(deleteType);
        int result= webAccountActivityDao.deleteType(deleteType);

		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}

	//遍历类型 返回类型线性结构
	public void getTypeTreeList(List<Integer> typeList,List<AccountActivityTypeTree> typeTree){
		if (typeTree!=null&&typeTree.size()!=0){
			for (AccountActivityTypeTree type:typeTree){
				typeList.add(type.getId());
				getTypeTreeList(typeList,type.getChilds());
			}
		}
	}



	/**
	 * 新增活动
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult addActivity(Map<String, Object> map) {
        int result= webAccountActivityDao.addActivity(map);
		if (result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 活动列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getActivityList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		if (map.get("date")!=null&&!map.get("date").toString().equals("")){
			map.put("begin_time",map.get("date")+"-01-01 00:00:00");
			map.put("end_time",map.get("date")+"-12-31 23:59:59");
		}
		if (map.get("type_id")!=null&&!map.get("type_id").toString().equals("")){
			int type_id=Integer.parseInt(String.valueOf(map.get("type_id")));
			//所有树的数据
			List<AccountActivityTypeTree>  typeList = webAccountActivityDao.getType(map);
			AccountActivityTypeTree elcmDeviceTypeTree=new AccountActivityTypeTree();
			//递归获取树结构
			List<AccountActivityTypeTree> typeTree=elcmDeviceTypeTree.getAccountActivityTreeByRecursion(type_id,typeList,0);
			//需要查询的类型
			List<Integer> type=new ArrayList<>();
			getTypeTreeList(type,typeTree);
			type.add(type_id);
			map.put("type",type);
		}

		ArrayList<HashMap<String, Object>>  activityList= webAccountActivityDao.getActivityList(map);
		int  total= webAccountActivityDao.getActivityListCount(map);
		ResultHandleUtil.addOrderNumber(activityList, map);  //为结果集添加序号

		Map<String,Object> result=new HashMap<>(2);
		result.put("list",activityList);
		result.put("total",total);
        return WebResult.success(result);
	}

	/**
	 * 编辑活动
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult updateActivity(Map<String, Object> map) {
		int result= webAccountActivityDao.updateActivity(map);
		if (result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 删除活动
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult deleteActivity(Map<String, Object> map) {
		List<String> list=Arrays.asList(map.get("ids").toString().split(","));
		int result=0;
		if (list.size()>0){
			result=webAccountActivityDao.deleteActivity(list);
		}
		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}


//===============================回收站=========================================///

	/**
	 * 回收站
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRecycleBin(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		if (map.get("date")!=null&&!map.get("date").toString().equals("")){
			map.put("begin_time1",map.get("date")+"-01-01 00:00:00");
			map.put("end_time1",map.get("date")+"-12-31 23:59:59");
		}
		if (map.get("begin_time")!=null&&!map.get("begin_time").toString().equals("")){
			map.put("begin_time",map.get("begin_time")+" 00:00:00");
		}
		if (map.get("end_time")!=null&&!map.get("end_time").toString().equals("")){
			map.put("end_time",map.get("end_time")+" 23:59:59");
		}

		ArrayList<HashMap<String, Object>>  activityList= webAccountActivityDao.getRecycleBin(map);
		int  total= webAccountActivityDao.getRecycleBinCount(map);
		ResultHandleUtil.addOrderNumber(activityList, map);  //为结果集添加序号

		Map<String,Object> result=new HashMap<>(2);
		result.put("list",activityList);
		result.put("total",total);
		return WebResult.success(result);
	}


	/**
	 * 恢复
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult recovery(Map<String, Object> map) {
		List<String> list=Arrays.asList(map.get("ids").toString().split(","));
		map.put("list",list);
		int result= webAccountActivityDao.recovery(map);
		if (result>0){
			return WebResult.success();
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 彻底删除
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult deleteCompletely(Map<String, Object> map) {
		List<String> list=Arrays.asList(map.get("ids").toString().split(","));
		int result=0;
		if (list.size()>0){
			result=webAccountActivityDao.deleteCompletely(list);
		}
		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 添加查看和下载记录
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult addRecord(Map<String, Object> map) {
		//添加月份
		map.put("time",DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM));
		Map<String, Object> record=webAccountActivityDao.getRecord(map);
		if (record==null){
			//没存在  添加
			map.put("count",1); //默认1
			webAccountActivityDao.insertRecord(map);
		}else{
			//已经存在  修改
			map.put("id",record.get("id"));
			map.put("count",Integer.parseInt(record.get("count").toString())+1);
			webAccountActivityDao.updateRecord(map);
		}
		return WebResult.success();
	}



	/**
	 * 数据统计
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult statistics(Map<String, Object> map) {
		List<Map<String, Object>> activityTimeStatistics = webAccountActivityDao.getActivityTimeStatistics(map);
		List<Map<String, Object>> addStatistics = webAccountActivityDao.getActivityAddStatistics(map);
		List<Map<String, Object>> deleteStatistics = webAccountActivityDao.getActivityDeleteStatistics(map);
		Map<Integer, Integer> timeData=new HashMap<>();
		Map<Integer, Integer> addData=new HashMap<>();
		Map<Integer, Integer> deleteData=new HashMap<>();
		for (int i=0;i<activityTimeStatistics.size();i++){
			timeData.put(Integer.parseInt(activityTimeStatistics.get(i).get("time").toString()),Integer.parseInt(activityTimeStatistics.get(i).get("count").toString()));
		}
		for (int i=0;i<addStatistics.size();i++){
			addData.put(Integer.parseInt(addStatistics.get(i).get("time").toString()),Integer.parseInt(addStatistics.get(i).get("count").toString()));
		}
		for (int i=0;i<deleteStatistics.size();i++){
			deleteData.put(Integer.parseInt(deleteStatistics.get(i).get("time").toString()),Integer.parseInt(deleteStatistics.get(i).get("count").toString()));
		}


		String legend[]={"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};
		//饼图数据
		List<Map<String,Object>> pieList=new ArrayList<>();
		List<Integer> addChartData=new ArrayList<>();
		List<Integer> deleteChartData=new ArrayList<>();
		for (int i=1;i<13;i++){
			Map<String,Object> pieData=new HashMap<>();
			pieData.put("name",legend[i-1]);
			pieData.put("value",timeData.get(i)==null?0:timeData.get(i));
			pieList.add(pieData);

			addChartData.add(addData.get(i)==null?0:addData.get(i));
			deleteChartData.add(deleteData.get(i)==null?0:deleteData.get(i));
		}

		//折线图数据
		List<Map<String, Object>> series=new ArrayList<>();
		Map<String, Object> addSeries=new HashMap<>();
		addSeries.put("name","新增");
		addSeries.put("data",addChartData);
		series.add(addSeries);

		Map<String, Object> deleteSeries=new HashMap<>();
		deleteSeries.put("name","删除");
		deleteSeries.put("data",deleteChartData);
		series.add(deleteSeries);


		//饼图
		Map<String,Object> pie=new HashMap<>();
		String chartLegend[]={"新增","删除"};
		pie.put("legend",legend);
		pie.put("data",pieList);

		//折线图
		Map<String,Object> chart=new HashMap<>();
		chart.put("xAxis",legend);  //横坐标
		chart.put("legend",chartLegend);  //横坐标
		chart.put("series",series);  //横坐标


		Map<String,Object> result=new HashMap<>();
		result.put("pie",pie);
		result.put("chart",chart);

		return WebResult.success(result);
	}





	/**
	 * 查看和下载统计
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult recordStatistics(Map<String, Object> map) {
		String date=map.get("year")+"-"+map.get("month");
		map.put("date",DateUtil.parseStrToStr(date,DateUtil.DATE_FORMAT_YYYY_MM));
		List<Map<String, Object>> seeStatistics = webAccountActivityDao.getSeeStatistics(map);

		List<String> idList=new ArrayList<>();
		for (Map<String, Object> see:seeStatistics){
			idList.add(see.get("id").toString());
		}
		map.put("idList",idList);
		List<Map<String, Object>> downloadStatistics = webAccountActivityDao.getDownloadStatistics(map);

		Map<String, Object> downloadMap=new HashMap<>();
		for (Map<String, Object> download:downloadStatistics){
			downloadMap.put(download.get("id").toString(),download.get("count"));
		}

		//横坐标
		List<String>  xAxis=new ArrayList<>();
		List<String> seeData =new ArrayList<>(); //出库数据
		List<String> downloadData =new ArrayList<>();  //入库数据

		for (Map<String, Object> see:seeStatistics){
			xAxis.add(see.get("name").toString());
			seeData.add(see.get("count").toString());
			downloadData.add(downloadMap.get(see.get("id").toString())==null?"0":downloadMap.get(see.get("id").toString()).toString());
		}


		//标题
		String[] legend={"查看","下载"};
		Map<String,Object> result=new HashMap<>(4);
		result.put("legend",legend);
		result.put("xAxis",xAxis);
		result.put("seeData",seeData);
		result.put("downloadData",downloadData);
		return WebResult.success(result);
	}




}
