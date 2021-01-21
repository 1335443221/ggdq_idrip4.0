package com.sl.idripweb.service.impl;

import com.sl.common.utils.DateUtil;
import com.sl.common.utils.PageUtil;
import com.sl.common.utils.ResultHandleUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.WebAccountStockDao;
import com.sl.idripweb.service.WebAccountStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("WebAccountStockServiceImpl")
public class WebAccountStockServiceImpl implements WebAccountStockService {

	@Autowired
	WebAccountStockDao webAccountStockDao;


	/**
	 * 获取所有类别
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getType(Map<String, Object> map) {
		ArrayList<Map<String, Object>> typeList = webAccountStockDao.getType(map);
		return WebResult.success(typeList);
	}


	/**
	 * 获取库存列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getStockList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		Map<String,Object> result=new HashMap<>();
		ArrayList<HashMap<String, Object>> stockList = webAccountStockDao.getStockList(map);
		int total = webAccountStockDao.getStockListCount(map);
		ResultHandleUtil.addOrderNumber(stockList, map);  //为结果集添加序号
		result.put("list",stockList);
		result.put("total",total);
		return WebResult.success(result);
	}


	/**
	 * 新增库存
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult addStock(Map<String, Object> map) {
		int type_id=Integer.parseInt(String.valueOf(map.get("type_id")));
		if(type_id==0){
			Map<String,Object> type=new HashMap<>();
			type.put("type_name",map.get("type_name"));
			type.put("project_id",map.get("project_id"));
			webAccountStockDao.insertType(type);
			map.put("type_id",type.get("type_id"));//返回的自增id
		}
		int result=webAccountStockDao.insertStock(map);
			if (result>0){
				return WebResult.success(200,"保存成功");
			}else{
				return WebResult.error(201);
			}
	}


	/**
	 * 修改库存
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult updateStock(Map<String, Object> map) {
		int type_id=Integer.parseInt(String.valueOf(map.get("type_id")));
		if(type_id==0){
			Map<String,Object> type=new HashMap<>();
			type.put("type_name",map.get("type_name"));
			type.put("project_id",map.get("project_id"));
			webAccountStockDao.insertType(type);
			map.put("type_id",type.get("type_id"));//返回的自增id
		}
		int result=webAccountStockDao.updateStock(map);
			if (result>0){
				return WebResult.success(200,"修改成功");
			}else{
				return WebResult.error(201);
			}
	}


	/**
	 * 删除库存
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult deleteStock(Map<String, Object> map) {
		List<String> list=Arrays.asList(map.get("ids").toString().split(","));
		int result=0;
		if (list.size()>0){
			result=webAccountStockDao.deleteStock(list);
		}
		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}

	//=================================================================//

	/**
	 * 下拉框 所有库存
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getAllStock(Map<String, Object> map) {
		ArrayList<HashMap<String, Object>> result=webAccountStockDao.getAllStock(map);
		return WebResult.success(result);
	}


	/**
	 * 出库列表
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getStockOutList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		Map<String,Object> result=new HashMap<>();
		ArrayList<HashMap<String, Object>> stockList = webAccountStockDao.getStockOutList(map);
		int total = webAccountStockDao.getStockOutListCount(map);
		ResultHandleUtil.addOrderNumber(stockList, map);  //为结果集添加序号
		result.put("list",stockList);
		result.put("total",total);
		return WebResult.success(result);
	}


	/**
	 * 新增出库单
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public WebResult addStockOut(Map<String, Object> map) {
		//计算库存
		int material_id=Integer.parseInt(String.valueOf(map.get("material_id")));
		int amount=webAccountStockDao.getStockAmount(material_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receive_amount")));
		if (receive_amount>amount){  //如果大于库存 返回错误码
			return WebResult.error(610);
		}
		int result=webAccountStockDao.insertStockOut(map);
		if (result>0){
			webAccountStockDao.outOrInStock(amount-receive_amount,material_id); //修改库存
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 修改出库
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public WebResult updateStockOut(Map<String, Object> map) {
		//计算库存  当前+之前出库-现在出库
		int material_id=Integer.parseInt(String.valueOf(map.get("material_id")));
		int amount=webAccountStockDao.getStockAmount(material_id);
		int receive_amount=Integer.parseInt(String.valueOf(map.get("receive_amount")));
		int receive_amount2=webAccountStockDao.getStockOutAmount(Integer.parseInt(String.valueOf(map.get("id"))));
		if (amount+receive_amount2-receive_amount<0){
			return WebResult.error(610);
		}
		int result=webAccountStockDao.updateStockOut(map);
		if (result>0){
			webAccountStockDao.outOrInStock(amount+receive_amount2-receive_amount,material_id); //修改库存
			return WebResult.success(200,"修改成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 删除出库
	 * @param map
	 * @return
	 */
	@Override
	public WebResult deleteStockOut(Map<String, Object> map) {
		List<String> list=Arrays.asList(map.get("ids").toString().split(","));
		int result=0;
		if (list.size()>0){
			result=webAccountStockDao.deleteStockOut(list);
		}
		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}

	//========================入库========================================//
	/**
	 * 入库列表
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getStockInList(Map<String, Object> map) {
		PageUtil.setPageInfo(map); //页码
		Map<String,Object> result=new HashMap<>();
		ArrayList<HashMap<String, Object>> stockList = webAccountStockDao.getStockInList(map);
		int total = webAccountStockDao.getStockInListCount(map);
		ResultHandleUtil.addOrderNumber(stockList, map);  //为结果集添加序号
		result.put("list",stockList);
		result.put("total",total);
		return WebResult.success(result);
	}

	/**
	 * 新增入库单
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public WebResult addStockIn(Map<String, Object> map) {
		int result=webAccountStockDao.insertStockIn(map);
		if (result>0){
		//修改库存
		int material_id=Integer.parseInt(String.valueOf(map.get("material_id")));
		int amount=webAccountStockDao.getStockAmount(material_id);
		int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousing_amount")));
		webAccountStockDao.outOrInStock(amount+warehousing_amount,material_id);

		return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 修改入库
	 * @param map
	 * @return
	 */
	@Override
	public WebResult updateStockIn(Map<String, Object> map) {
		//修改库存
		int material_id=Integer.parseInt(String.valueOf(map.get("material_id")));
		int amount=webAccountStockDao.getStockAmount(material_id);
		int warehousing_amount=Integer.parseInt(String.valueOf(map.get("warehousing_amount")));
		int warehousing_amount2=webAccountStockDao.getStockInAmount(Integer.parseInt(String.valueOf(map.get("id"))));

		if (amount-warehousing_amount2+warehousing_amount<0){
			 return WebResult.error(620);
		}

		int result=webAccountStockDao.updateStockIn(map);
		if (result>0){
			webAccountStockDao.outOrInStock(amount-warehousing_amount2+warehousing_amount,material_id);
			return WebResult.success(200,"修改成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 删除入库
	 * @param map
	 * @return
	 */
	@Override
	public WebResult deleteStockIn(Map<String, Object> map) {
		List<String> list=Arrays.asList(map.get("ids").toString().split(","));
		int result=0;
		if (list.size()>0){
			result=webAccountStockDao.deleteStockIn(list);
		}
		if (result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}




	/**
	 * 数据统计
	 * @param map
	 * @return
	 */
	@Override
	public WebResult statistics(Map<String, Object> map) {
		//处理数据
		String project_id=map.get("project_id").toString();
		String date=map.get("year").toString();
		List<Map<String, Object>> addList=webAccountStockDao.getAddStatistics(project_id,date);
		List<Map<String, Object>> outList=webAccountStockDao.getOutStatistics(project_id,date);
		List<Map<String, Object>> inList=webAccountStockDao.getInStatistics(project_id,date);
		Map<Integer, Integer> addMap=new HashMap<>();
		Map<Integer, Integer> outMap=new HashMap<>();
		Map<Integer, Integer> inMap=new HashMap<>();
		int addCount=0;
		int outCount=0;
		int inCount=0;
		for (int i=0;i<addList.size();i++){
			addCount+=Integer.parseInt(addList.get(i).get("count").toString());
			addMap.put(Integer.parseInt(addList.get(i).get("time").toString()),Integer.parseInt(addList.get(i).get("count").toString()));
		}
		for (int i=0;i<outList.size();i++){
			outCount+=Integer.parseInt(outList.get(i).get("count").toString());
			outMap.put(Integer.parseInt(outList.get(i).get("time").toString()),Integer.parseInt(outList.get(i).get("count").toString()));
		}
		for (int i=0;i<inList.size();i++){
			inCount+=Integer.parseInt(inList.get(i).get("count").toString());
			inMap.put(Integer.parseInt(inList.get(i).get("time").toString()),Integer.parseInt(inList.get(i).get("count").toString()));
		}



		//饼图
		Map<String,Object> pie=new HashMap<>();
		List<Map<String,Object>> pieDataList=new ArrayList<>();
		Map<String,Object> pieData=new HashMap<>();
		pieData.put("name","新增");
		pieData.put("value",addCount);
		pieDataList.add(pieData);

		pieData=new HashMap<>();
		pieData.put("name","出库");
		pieData.put("value",outCount);
		pieDataList.add(pieData);

		pieData=new HashMap<>();
		pieData.put("name","入库");
		pieData.put("value",inCount);
		pieDataList.add(pieData);

		String[] pieLegend={"新增","出库","入库"};
		pie.put("legend",pieLegend);
		pie.put("data",pieDataList);


		//趋势图
		Map<String,Object> chart=new HashMap<>();
		//标题
		String[] chartLegend={"新增","出库","入库"};
		//横坐标
		List<String> chartXAxis=new ArrayList<>();
		//数据
		List<Map<String,Object>> chartSeries=new ArrayList<>();

		List<Integer> addChartData=new ArrayList<>();
		List<Integer> outChartData=new ArrayList<>();
		List<Integer> inChartData=new ArrayList<>();
		for (int i=1;i<13;i++){
			chartXAxis.add(i+"月");
			addChartData.add(addMap.get(i)==null?0:addMap.get(i));
			outChartData.add(outMap.get(i)==null?0:outMap.get(i));
			inChartData.add(inMap.get(i)==null?0:inMap.get(i));
		}

		Map<String,Object> addChartSeries=new HashMap<>();
		addChartSeries.put("name","新增");
		addChartSeries.put("data",addChartData);

		Map<String,Object> outChartSeries=new HashMap<>();
		outChartSeries.put("name","出库");
		outChartSeries.put("data",outChartData);

		Map<String,Object> inChartSeries=new HashMap<>();
		inChartSeries.put("name","入库");
		inChartSeries.put("data",inChartData);
		chartSeries.add(addChartSeries);
		chartSeries.add(outChartSeries);
		chartSeries.add(inChartSeries);

		chart.put("legend",chartLegend);
		chart.put("xAxis",chartXAxis);
		chart.put("series",chartSeries);


		Map<String,Object> result=new LinkedHashMap<>();
		result.put("pie",pie); //饼图
		result.put("chart",chart);//折线图
		return WebResult.success(result);
	}




	//柱状图
	@Override
	public WebResult outAndInRanking(Map<String, Object> map){
		String project_id=map.get("project_id").toString();
		String date=map.get("year")+"-"+map.get("month");
		date=DateUtil.parseStrToStr(date,DateUtil.DATE_FORMAT_YYYY_MM);

		List<Map<String, Object>> outRanking = webAccountStockDao.getOutRanking(project_id, date);
		List<String> idList=new ArrayList<>();
		for (int i=0;i<outRanking.size();i++){
			idList.add(outRanking.get(i).get("id").toString());
		}

		//入库数据
		List<Map<String, Object>> inRanking =new ArrayList<>();
		if (idList.size()>0){
			inRanking=webAccountStockDao.getInRanking(project_id, date,idList);
		}
		Map<String, Object> inRankingMap=new HashMap<>();
		for (int i=0;i<inRanking.size();i++){
			inRankingMap.put(inRanking.get(i).get("id").toString(),inRanking.get(i).get("amount"));
		}

		//横坐标
		List<String>  xAxis=new ArrayList<>();
		List<String> outData =new ArrayList<>(); //出库数据
		List<String> inData =new ArrayList<>();  //入库数据
		for (int i=0;i<outRanking.size();i++){
			xAxis.add(outRanking.get(i).get("name").toString()); //横坐标
			outData.add(outRanking.get(i).get("amount").toString());  //出库数据
			inData.add(inRankingMap.get(outRanking.get(i).get("id").toString())==null?"0":inRankingMap.get(outRanking.get(i).get("id").toString()).toString());  //出库数据
		}

		//标题
		String[] legend={"出库","入库"};

		Map<String,Object> result=new HashMap<>(4);
		result.put("legend",legend);
		result.put("xAxis",xAxis);
		result.put("outData",outData);
		result.put("inData",inData);
		return WebResult.success(result);
	}


}
