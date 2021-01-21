package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.common.config.ConstantConfig;
import com.sl.common.service.CommonService;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.ExcelExportUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.WebCommonDao;
import com.sl.idripweb.dao.WebSjfReportDao;
import com.sl.idripweb.dao.WebUIDataDao;
import com.sl.idripweb.entity.WebUIDataVo;
import com.sl.idripweb.service.WebCommonService;
import com.sl.idripweb.service.WebSjfReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("webSjfReportServiceImpl")
public class WebSjfReportServiceImpl implements WebSjfReportService {

	@Autowired
    ConstantConfig constant;
	@Autowired
	DateUtil dateUtil;
	@Autowired
	WebSjfReportDao webSjfReportDao;
	@Autowired
	WebUIDataDao webUIDataDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	WebCommonDao webCommonDao;


	//获取厂区楼宇树形列表
	@Override
	public WebResult getCategoryTree(Map<String, Object> map) {
		ArrayList<Map<String, Object>> categoryTree = webSjfReportDao.getCategoryTree(map);
		Map<String,Map<String, Object>> parentMap = new HashMap<>();
		Map<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<>();
		Map<String, Object> resultData = new HashMap<>();
		ArrayList<Map<String, Object>> dataList = new ArrayList<>();
		ArrayList<String> checkList = new ArrayList<>();
		for(Map<String, Object> category : categoryTree){
			String parentId = String.valueOf(category.get("parent_id"));
			String categoryId = String.valueOf(category.get("id"));
			if(category.get("parent_id") == null){
				parentMap.put(categoryId, category);
			} else if(!category.get("parent_id").equals(0)){
				if(childrenMap.get(parentId) == null)
					childrenMap.put(parentId, new ArrayList<>());
				childrenMap.get(parentId).add(category);
			}
		}
		Iterator<Map.Entry<String, Map<String, Object>>> entries = parentMap.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry<String, Map<String, Object>> entry = entries.next();
			Map<String, Object> resultMap = new HashMap<>();
			ArrayList<Map<String, Object>> maps = childrenMap.get(entry.getKey());
			resultMap.put("id", entry.getKey());
			resultMap.put("text", entry.getValue().get("text"));
			resultMap.put("nodes", maps);
			dataList.add(resultMap);
			//封装选中的id
			checkList.add(entry.getKey());
			for(Map<String, Object> checkMap : maps){
				checkList.add(String.valueOf(checkMap.get("id")));
			}
		}
		resultData.put("check_list", checkList);
		resultData.put("data_list", dataList);
		return WebResult.success(resultData);
	}

	//通过厂区楼宇树形列表获取所有分户信息
	@Override
	public WebResult getHousesByTree(Map<String, Object> map) {
		if(!StringUtils.isEmpty(String.valueOf(map.get("categoryIds"))))
			map.put("categoryIds", Arrays.asList(String.valueOf(map.get("categoryIds")).split(",")));
		ArrayList<Map<String, Object>> housesByTree = webSjfReportDao.getHousesByTree(map);
		return WebResult.success(housesByTree);
	}

	//电费报表
	@Override
	public WebResult chargeReport(Map<String, Object> map) {
		//参数检查
		int charge = handleParamCheck(map, "charge");
		if(charge != 0) return WebResult.error(charge);
		Map<String, List<Map<String, Object>>> chargeReportData = getChargeReportData(map);
		return WebResult.success(chargeReportData);
	}

	//电费报表-》下载
	@Override
	public void chargeReportDownload(Map<String, Object> map, HttpServletResponse response) {
		//参数检查
		int charge = handleParamCheck(map, "charge");
		if(charge != 0) return;
		String fileName = "电费报表";
		Map<String, List<Map<String, Object>>> chargeReportData = getChargeReportData(map);
		downLoad(chargeReportData, map, response, fileName);
	}

	//电量报表
	@Override
	public WebResult powerReport(Map<String, Object> map) {
		//参数检查
		int power = handleParamCheck(map, "power");
		if(power != 0) return WebResult.error(power);
		Map<String, List<Map<String, Object>>> powerReportData = getPowerReportData(map);
		return WebResult.success(powerReportData);
	}

	//电量报表-》下载
	@Override
	public void powerReportDownload(Map<String, Object> map, HttpServletResponse response) {
		//参数检查
		int power = handleParamCheck(map, "power");
		if(power != 0) return;
		String fileName = "电量报表";
		Map<String, List<Map<String, Object>>> powerReportData = getPowerReportData(map);
		downLoad(powerReportData, map, response, fileName);
	}

	//财务报表
	@Override
	public WebResult financeReport(Map<String, Object> map) {
		//参数检查
		int finance = handleParamCheck(map, "finance");
		if(finance != 0) return WebResult.error(finance);
		Map<String, Object> financeReportData = getFinanceReportData(map);
		return WebResult.success(financeReportData);
	}

	//财务报表-》下载
	@Override
	public void financeReportDownload(Map<String, Object> map, HttpServletResponse response) {
		//参数检查
		int finance = handleParamCheck(map, "finance");
		if(finance != 0) return;
		String fileName = "财务报表";
		String columnStr = "[{'prop':'factory_name','label':'园区名称'},{'prop':'category_name','label':'楼宇名称'},{'prop':'house_name','label':'分户名称'}," +
				"{'prop':'charge_type','label':'收费方式'},{'prop':'total_fees','label':'电费总额'},{'prop':'startDate','label':'开始时间'}," +
				"{'prop':'endDate','label':'结束时间'},{'prop':'beginReadout','label':'开始读数'},{'prop':'endReadout','label':'结束读数'}," +
				"{'prop':'total_power','label':'用电总数'},{'prop':'peak_ep','label':'峰时段电度'},{'prop':'plain_ep','label':'平时段电度'}," +
				"{'prop':'valley_ep','label':'谷时段电度'},{'prop':'first_ep','label':'第一阶段电度'},{'prop':'second_ep','label':'第二阶段电度'}," +
				"{'prop':'third_ep','label':'第三阶段电度'}]";
		map.put("column", columnStr);
		Map<String, Object> financeReportData = getFinanceReportData(map);
		List<Map<String, Object>> recordList = (List<Map<String, Object>>) financeReportData.get("recordList");
		Map<String, List<Map<String, Object>>> resultData = new HashMap<>();
		resultData.put("recordList", recordList);
		downLoad(resultData, map, response, fileName);
	}

	//检查请求参数是否缺失
	private int handleParamCheck(Map<String, Object> map, String type) {
		int msg = 0;
		if((map.get("houseIds") == null || "".equals(map.get("houseIds"))) && (map.get("categoryIds") == null || "".equals(map.get("categoryIds")))){
			msg = 603;
		}
//		if("charge".equals(type) || "power".equals(type)){
//			String reportType = String.valueOf(map.get("reportType"));
//			String startDate = String.valueOf(map.get("startDate"));
//			String endDate = String.valueOf(map.get("endDate"));
//			List<String> dateList;
//			switch (reportType){
//				case "day":
//					dateList = DateUtil.getDayListOfDate(startDate, endDate);
//					if(dateList != null && dateList.size() > 31) msg = 601;
//					break;
//				case "month":
//					dateList = DateUtil.getMonthListOfDate(startDate, endDate);
//					if(dateList != null && dateList.size() > 12) msg = 602;
//					break;
//				default:
//					dateList = new ArrayList<>();
//			}
//		}
		return msg;
	}

	//获取首页数据
	@Override
	public WebResult getIndexData(Map<String, Object> map) {
		setDefaultFactoryAndTime(map);
		//结果集
		Map<String, Object> result=new HashMap<>();
		//获取缴费用户
		int houseCount = webSjfReportDao.getHouseCount(map);
		//获取首页缴费金额
		Map<String, ArrayList<String>> totalFees = getTotalFees(map);
		//获取首页欠费用户
		List<Map<String, Object>> arrearsHouses = getArrearsHouses(map);
		//获取园区下的所有分户
		ArrayList<Map<String, Object>> houses = webSjfReportDao.getHousesByFactoryId(map);
		//获取项目的峰平谷和阶梯设置
		Map<String, Object> chargeFpgAndLadder = webSjfReportDao.getChargeFpgAndLadder(map);
		//统计三个阶梯段用户数量
		List<String> ladderCount = getLadderCount(map);
		//统计电费、电量、缴费金额总计
		Map<String, Object> statisticsTotal = getStatistics(map);

		result.put("houseCount",houseCount);
		result.put("totalFees",totalFees);
		result.put("arrearsHouses",arrearsHouses);
		result.put("arrearsCount",arrearsHouses.size());
		result.put("houses",houses);
		result.put("chargeFpgAndLadder",chargeFpgAndLadder);
		result.put("ladderCount",ladderCount);
		result.put("statistics",statisticsTotal);
		return WebResult.success(result);
	}

	//首页根据选中分时用户获取峰平谷电费电度统计
	@Override
	public WebResult getFpgData(Map<String, Object> map) {
		setDefaultFactoryAndTime(map);
		if(map.get("ladder") == null) map.put("ladder", 1);
		Map<String, Object> fpgData = webSjfReportDao.getFpgData(map);
		if(fpgData == null){
			fpgData = new HashMap<>();
			fpgData.put("peak_ep","0.00");
			fpgData.put("plain_ep","0.00");
			fpgData.put("valley_ep","0.00");
			fpgData.put("peak_fees","0.00");
			fpgData.put("plain_fees","0.00");
			fpgData.put("valley_fees","0.00");
		}
		return WebResult.success(fpgData);
	}

	//获取某个阶梯下的用户、用电量和缴费金额
	@Override
	public WebResult getHouseByLadder(Map<String, Object> map) {
		setDefaultFactoryAndTime(map);
		//封装每个分户id下用电量数据
		Map<String, Object> housePowerMap = new HashMap<>();
		ArrayList<Map<String, Object>> powerAndFeesOfHouse = webSjfReportDao.getPowerAndFeesOfHouse(map);
		for(Map<String, Object> each : powerAndFeesOfHouse){
			housePowerMap.put(String.valueOf(each.get("id")), each.get("power"));
		}
		ArrayList<Map<String, Object>> houseByLadder = webSjfReportDao.getHouseByLadder(map);
		for(Map<String, Object> each : houseByLadder){
			String id = String.valueOf(each.get("id"));
			if(housePowerMap.get(id) != null) each.put("total_power", housePowerMap.get(id));
			else each.put("total_power", "0.00");
		}
		return WebResult.success(houseByLadder);
	}

	//获取分户分组下的电费和用电量
	@Override
	public WebResult getPowerAndFeesOfHouse(Map<String, Object> map) {
		setDefaultFactoryAndTime(map);
		ArrayList<Map<String, Object>> powerAndFeesOfHouse = webSjfReportDao.getPowerAndFeesOfHouse(map);
		Map<String, ArrayList<String>> resultMap = new HashMap<>();
		resultMap.put("houses", new ArrayList<>());
		resultMap.put("powers", new ArrayList<>());
		resultMap.put("fees", new ArrayList<>());
		if(powerAndFeesOfHouse.size() == 0){
			List<String> powerAndFeesOfHouseAll = webSjfReportDao.getPowerAndFeesOfHouseAll(map);
			for(String houseId : powerAndFeesOfHouseAll){
				resultMap.get("houses").add(houseId);
				resultMap.get("powers").add("0.00");
				resultMap.get("fees").add("0.00");
			}
		}else{
			for(Map<String, Object> each : powerAndFeesOfHouse){
				resultMap.get("houses").add(String.valueOf(each.get("house_name")));
				resultMap.get("powers").add(String.valueOf(each.get("power")));
				resultMap.get("fees").add(String.valueOf(each.get("fees")));
			}
		}
		return WebResult.success(resultMap);
	}

	//设置默认园区和时间
	public void setDefaultFactoryAndTime(Map<String, Object> map){
		ArrayList<Map<String, Object>> factories = webCommonDao.getFactory(map);
		Map factory = factories.get(0);
		Integer factory_id = Integer.parseInt(String.valueOf(factory.get("factory_id")));
		map.put("factory_id", factory_id);
		map.put("year", DateUtil.getYear(new Date()));
	}

	//封装下载逻辑
	public void downLoad(Map<String, List<Map<String, Object>>> jsonObject, Map<String, Object> map, HttpServletResponse response, String fileName){
		JSONArray column = null;
		List<Map> reusltData = null;
		if(jsonObject.get("column") == null){
			column = JSONArray.parseArray(String.valueOf(map.get("column")));
			if(jsonObject.get("recordList") == null) jsonObject.put("recordList", new ArrayList<>());
			reusltData = new ArrayList<>(jsonObject.get("recordList"));
			//收费方式映射
			JSONObject chargeType = JSONObject.parseObject("{'1':'平价','2':'分时','3':'阶梯','4':'分时+阶梯'}");
			for(Map result : reusltData){
				result.put("charge_type", chargeType.get(Integer.parseInt(String.valueOf(result.get("charge_type")))));
			}
		}else{
			column = JSONArray.parseArray(JSON.toJSONString(jsonObject.get("column")));
			reusltData = JSONArray.parseArray(JSON.toJSONString(jsonObject.get("data")), Map.class);
		}
		List<String> title = new ArrayList<>();//设置表格表头字段
		List<String> properties = new ArrayList<>();// 查询对应的字段
		for(int i=0;i<column.size();i++) {
			title.add(String.valueOf(column.getJSONObject(i).get("label")));
			properties.add(String.valueOf(column.getJSONObject(i).get("prop")));
		}
		ArrayList<HashMap<String, Object>> data = new ArrayList<>();
		for(Map each : reusltData){
			HashMap<String, Object> eachMap = new HashMap<>(each);
			data.add(eachMap);
		}
		ExcelExportUtil excelExportUtil = new ExcelExportUtil();
		excelExportUtil.setData(data);
		excelExportUtil.setHeardKey(properties);
		excelExportUtil.setFontSize(11);
		excelExportUtil.setSheetName(fileName);
		excelExportUtil.setFileName(fileName);
		excelExportUtil.setHeardList(title);
		try {
			excelExportUtil.exportExport(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//获取首页欠费用户
	private List<Map<String, Object>> getArrearsHouses(Map<String, Object> map) {
		ArrayList<Map<String, Object>> houses = webSjfReportDao.getHouses(map);
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<Map<String, Object>> maps = commonService.addBalanceToHouseList(houses, map);
		int order = 1;
		for(int i=0;i<maps.size();i++){
			Map<String, Object> each = maps.get(i);
			if(Double.parseDouble(String.valueOf(each.get("balance"))) < 0){
				Map<String, Object> eachData = new HashMap<>();
				eachData.put("order", order);
				eachData.put("house_name", each.get("house_name"));
				eachData.put("balance", each.get("balance"));
				eachData.put("userType", "断电");
				eachData.put("phone", each.get("phone"));
				resultList.add(eachData);
				order++;
			}
		}
		return resultList;
	}

	//获取首页缴费金额
	private Map<String, ArrayList<String>> getTotalFees(Map<String, Object> map) {
		//首页柱状图缴费金额
		Map<String, ArrayList<String>> totalFees = new HashMap<>();
		//今年缴费数据map
		Map<String, Object> thisYearFeesMap = new HashMap<>();
		//去年缴费数据map
		Map<String, Object> lastYearFeesMap = new HashMap<>();
		//获取今年缴费数据
		ArrayList<Map<String, Object>> thisYearFees = webSjfReportDao.getTotalFees(map);
		for(Map<String, Object> fees : thisYearFees){
			thisYearFeesMap.put(String.valueOf(fees.get("date")), fees.get("total_fees"));
		}
		//获取去年年份
		int thisYear = Integer.parseInt(String.valueOf(map.get("year")));
		int lastYear = thisYear - 1;
		map.put("year", lastYear);
		//获取去年缴费数据
		ArrayList<Map<String, Object>> lastYearFees = webSjfReportDao.getTotalFees(map);
		map.put("year", thisYear);
		for(Map<String, Object> fees : lastYearFees){
			lastYearFeesMap.put(String.valueOf(fees.get("date")), fees.get("total_fees"));
		}
		totalFees.put("thisYearFees", new ArrayList<>());
		totalFees.put("lastYearFees", new ArrayList<>());

		//缴费金额封装为每年下的月数据
		for(int i=0;i<12;i++){
			//封装今年缴费数据
			if(thisYearFeesMap.get(String.valueOf(i+1)) == null){
				totalFees.get("thisYearFees").add("0");
			}else{
				totalFees.get("thisYearFees").add(String.valueOf(thisYearFeesMap.get(String.valueOf(i+1))));
			}
			if(lastYearFeesMap.get(String.valueOf(i+1)) == null){
				totalFees.get("lastYearFees").add("0");
			}else{
				totalFees.get("lastYearFees").add(String.valueOf(lastYearFeesMap.get(String.valueOf(i+1))));
			}
		}
		return totalFees;
	}

	//统计三个阶梯段用户数量
	private List<String> getLadderCount(Map<String, Object> map) {
		List<String> ladderCount = new ArrayList<>();
		ArrayList<Map<String, Object>> housesOfLadder = webSjfReportDao.getHousesOfLadder(map);
		Map<String, String> dataMap = new HashMap<>();
		for (Map<String, Object> houseMap : housesOfLadder){
			dataMap.put(String.valueOf(houseMap.get("ladder")), String.valueOf(houseMap.get("count")));
		}
		for(int i=0;i<3;i++){
			if(dataMap.get(String.valueOf(i)) == null)
				ladderCount.add("0");
			else
				ladderCount.add(dataMap.get(String.valueOf(i)));
		}
		return ladderCount;
	}

	//统计电费、电量、缴费金额总计
	private Map<String, Object> getStatistics(Map<String, Object> map) {
		Map<String, Object> statisticsTotal = webSjfReportDao.getStatisticsTotal(map);
		Map<String, Object> statisticsAmount = webSjfReportDao.getStatisticsAmount(map);
		statisticsTotal.put("amount", statisticsAmount.get("amount"));
		return statisticsTotal;
	}

	//封装分户id获取逻辑
	public void setHouseIds(Map<String, Object> map){
		if(!StringUtils.isEmpty(String.valueOf(map.get("houseIds"))))//传入了分户id就用分户id
			map.put("houseIds", Arrays.asList(String.valueOf(map.get("houseIds")).split(",")));
		else{//没传入分户id，直接用楼id查询所有分户id
			if(!StringUtils.isEmpty(String.valueOf(map.get("categoryIds"))))
				map.put("categoryIds", Arrays.asList(String.valueOf(map.get("categoryIds")).split(",")));
			ArrayList<Map<String, Object>> housesByTree = webSjfReportDao.getHousesByTree(map);
			List<String> houseIds = new ArrayList<>();
			for(Map<String, Object> house : housesByTree){
				houseIds.add(String.valueOf(house.get("house_id")));
			}
			if(houseIds.size() == 0) houseIds.add("-1");
			map.put("houseIds", houseIds);
		}
	}

	//获取报表data数据
	public Map<String, List<Map<String, Object>>> getReportData(ArrayList<Map<String, Object>> maps, String field){
		//结果data
		Map<String, List<Map<String, Object>>> data = new HashMap<>();
		data.put("data", new ArrayList<>());//数据
		data.put("column", new ArrayList<>());//表头
		//所有日期下的电费汇总
		Map<String, List<Map<String, Object>>> allDateCharge = new LinkedHashMap<>();
		//所有分户下的电费汇总
		Map<String, List<Map<String, Object>>> allHouseCharge = new LinkedHashMap<>();
		//将日期和分户下数据进行汇总
		for(Map<String, Object> each : maps){
			//处理日期
			if(allDateCharge.get(String.valueOf(each.get("date"))) == null)
				allDateCharge.put(String.valueOf(each.get("date")), new ArrayList<>());
			allDateCharge.get(String.valueOf(each.get("date"))).add(each);
			//处理分户
			if(allHouseCharge.get(String.valueOf(each.get("house_id"))) == null)
				allHouseCharge.put(String.valueOf(each.get("house_id")), new ArrayList<>());
			allHouseCharge.get(String.valueOf(each.get("house_id"))).add(each);
		}

		//计算横向合计
		Map<String, Object> transverseTotal = new LinkedHashMap<>();
		transverseTotal.put("factory_name", "合计");
		transverseTotal.put("category_name", "--");
		transverseTotal.put("house_name", "--");

		//封装表头
		//园区名称
		Map<String, Object> factoryNameMap = new LinkedHashMap<>();
		factoryNameMap.put("prop", "factory_name");
		factoryNameMap.put("label", "园区名称");
		//楼宇名称
		Map<String, Object> categoryNameMap = new LinkedHashMap<>();
		categoryNameMap.put("prop", "category_name");
		categoryNameMap.put("label", "楼宇名称");
		//分户名称
		Map<String, Object> houseNameMap = new LinkedHashMap<>();
		houseNameMap.put("prop", "house_name");
		houseNameMap.put("label", "分户名称");
		//合计
		Map<String, Object> totalMap = new LinkedHashMap<>();
		totalMap.put("prop", "total");
		totalMap.put("label", "合计");

		data.get("column").add(factoryNameMap);
		data.get("column").add(categoryNameMap);
		data.get("column").add(houseNameMap);
		data.get("column").add(totalMap);
		Iterator<Map.Entry<String, List<Map<String, Object>>>> dateIterator = allDateCharge.entrySet().iterator();
		while (dateIterator.hasNext()){
			Map.Entry<String, List<Map<String, Object>>> next = dateIterator.next();
			//封装时间日期表头
			Map<String, Object> dateMap = new LinkedHashMap<>();
			String key = next.getKey();
			dateMap.put("prop", key);
			dateMap.put("label", key);
			data.get("column").add(dateMap);
			//计算按时间分组总金额
			List<Map<String, Object>> value = next.getValue();
			for(int i=0;i<value.size();i++){
				if(transverseTotal.get("total") == null) transverseTotal.put("total", 0.0);
				if(transverseTotal.get(key) == null) transverseTotal.put(next.getKey(), 0.0);
				if(value.get(i).get(field) != null){
					transverseTotal.put("total", String.format("%.2f", Double.parseDouble(String.valueOf(transverseTotal.get("total"))) + Double.parseDouble(String.valueOf(value.get(i).get(field)))));
					transverseTotal.put(next.getKey(), String.format("%.2f", Double.parseDouble(String.valueOf(transverseTotal.get(next.getKey()))) + Double.parseDouble(String.valueOf(value.get(i).get(field)))));
				}
			}
		}
		data.get("data").add(transverseTotal);

		//封装列表数据
		Iterator<Map.Entry<String, List<Map<String, Object>>>> houseIterator = allHouseCharge.entrySet().iterator();
		while(houseIterator.hasNext()){
			Map<String, Object> eachData = new LinkedHashMap<>();
			Map.Entry<String, List<Map<String, Object>>> entry = houseIterator.next();
			List<Map<String, Object>> value = entry.getValue();
			for(int i=0;i<value.size();i++){
				if(i == 0){
					eachData.put("factory_name", value.get(i).get("factory_name"));
					eachData.put("category_name", value.get(i).get("category_name"));
					eachData.put("house_name", value.get(i).get("house_name"));
				}
				//计算纵向合计
				if(eachData.get("total") == null) eachData.put("total", 0.0);
				if(value.get(i).get(field) != null){
					eachData.put("total", String.format("%.2f", Double.parseDouble(String.valueOf(eachData.get("total"))) + Double.parseDouble(String.valueOf(value.get(i).get(field)))));
				}
				eachData.put(String.valueOf(value.get(i).get("date")), String.valueOf(value.get(i).get(field)));
			}
			data.get("data").add(eachData);
		}

		return data;
	}

	//针对maps封装补充缺失的时间和分户
	private void supplyHouseAndTime(Map<String, Object> map, ArrayList<Map<String, Object>> maps, String type) {
		//遍历maps并封装出时间和分户下的数值
		Map<String, String> houseDateMap = new HashMap();
		for(Map<String, Object> each : maps){
			String date = String.valueOf(each.get("date"));
			String houseId = String.valueOf(each.get("house_id"));
			String totalData = String.valueOf(each.get(type));
			houseDateMap.put(date+houseId, totalData);
		}
		String reportType = String.valueOf(map.get("reportType"));
		String startDate = String.valueOf(map.get("startDate"));
		String endDate = String.valueOf(map.get("endDate"));
		//获取所有分户分组数据
		ArrayList<Map<String, Object>> allHouses = webSjfReportDao.getAllHouses(map);
		List<String> dayList = new ArrayList<>();
		String dateFormat = "";
		switch (reportType){
			case "day":
				//获取开始时间和结束时间中间的所有时间
				dayList = DateUtil.getDayList(startDate, endDate);
				dateFormat = "yyyy年MM月dd日";
				break;
			case "month":
				dayList = DateUtil.getMonthList(startDate, endDate);
				dateFormat = "yyyy年MM月";
				break;
			case "year":
				dayList = DateUtil.getYearList(startDate, endDate);
				dateFormat = "yyyy年";
				break;
			case "hour":
				for(int i=0;i<24;i++){
					dayList.add(i + "时");
				}
				dateFormat = "HH时";
		}
		for(int i=0;i<dayList.size();i++){
			for(int j=0;j<allHouses.size();j++){
				String date = dayList.get(i);
				String houseId = String.valueOf(allHouses.get(j).get("house_id"));
				if(houseDateMap.get(date+houseId) == null){
					Map<String, Object> allHousesMap = new HashMap<>();
					allHousesMap.putAll(allHouses.get(j));
					allHousesMap.put(type, "0.00");
					allHousesMap.put("date", date);
					maps.add(allHousesMap);
				}
			}
		}
		DateFormat df = new SimpleDateFormat(dateFormat);//创建日期转换对象HH:mm:ss为时分秒，年月日为yyyy-MM-dd
		//此处创建了一个匿名内部类
		Collections.sort(maps, (Comparator<Map<String, Object>>) (o1, o2) -> {
			int flag = 0;
			try {
				Long date1 = df.parse(String.valueOf(o1.get("date"))).getTime();
				Long date2 = df.parse(String.valueOf(o2.get("date"))).getTime();
				if(!date1.equals(date2))
					flag = date1 - date2 > 0 ? 1 : -1;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return flag;
		});
	}

	//补全财务报表所有分户
	private void supplyHouseAndTimeOfFinance(ArrayList<Map<String, Object>> maps, Map<String, Object> map) {
//        List<String> houseIds = new ArrayList<>();
//        for(Map<String, Object> each : maps){
//            String houseId = String.valueOf(each.get("house_id"));
//            houseIds.add(houseId);
//        }
//        map.put("houseIds", houseIds);
		//查询这些分户的数据
		ArrayList<Map<String, Object>> housesOfFinance = webSjfReportDao.financeReport(map);
		//通过分户id映射数据、
		Map<String, Map<String, Object>> houseIdMap = new HashMap<>();
		for(Map<String, Object> each : housesOfFinance){
			String houseId = String.valueOf(each.get("house_id"));
			houseIdMap.put(houseId, each);
		}
		Set<String> idsSet = houseIdMap.keySet();
		for(Map<String, Object> each : maps){
			String houseId = String.valueOf(each.get("house_id"));
			if(idsSet.contains(houseId)){
				each.put("total_fees",houseIdMap.get(houseId).get("total_fees"));
				each.put("total_power",houseIdMap.get(houseId).get("total_power"));
				each.put("peak_ep",houseIdMap.get(houseId).get("peak_ep"));
				each.put("plain_ep",houseIdMap.get(houseId).get("plain_ep"));
				each.put("valley_ep",houseIdMap.get(houseId).get("valley_ep"));
				each.put("first_ep",houseIdMap.get(houseId).get("first_ep"));
				each.put("second_ep",houseIdMap.get(houseId).get("second_ep"));
				each.put("third_ep",houseIdMap.get(houseId).get("third_ep"));
			}else{
				each.put("total_fees","0.00");
				each.put("total_power","0.00");
				each.put("peak_ep","0.00");
				each.put("plain_ep","0.00");
				each.put("valley_ep","0.00");
				each.put("first_ep","0.00");
				each.put("second_ep","0.00");
				each.put("third_ep","0.00");
			}
		}
	}

	public Map<String, List<Map<String, Object>>> getChargeReportData(Map<String, Object> map){
		setHouseIds(map);
		String reportType = String.valueOf(map.get("reportType"));
		ArrayList<Map<String, Object>> maps = null;
		switch (reportType){
			case "day":
				maps = webSjfReportDao.chargeReportOfDay(map);
				break;
			case "month":
				maps = webSjfReportDao.chargeReportOfMonth(map);
				break;
			case "year":
				maps = webSjfReportDao.chargeReportOfYear(map);
				break;
			default:
				maps = webSjfReportDao.chargeReportOfDay(map);
		}
		//针对maps封装补充缺失的时间和分户
		supplyHouseAndTime(map, maps, "total_fees");
		Map<String, List<Map<String, Object>>> total_fees = getReportData(maps, "total_fees");
		return total_fees;
	}

	public Map<String, List<Map<String, Object>>> getPowerReportData(Map<String, Object> map){
		setHouseIds(map);
		String reportType = String.valueOf(map.get("reportType"));
		ArrayList<Map<String, Object>> maps = new ArrayList<>();
		switch (reportType){
			case "day":
				maps = webSjfReportDao.powerReportOfDay(map);
				break;
			case "month":
				maps = webSjfReportDao.powerReportOfMonth(map);
				break;
			case "year":
				maps = webSjfReportDao.powerReportOfYear(map);
				break;
			case "hour":
				ArrayList<Map<String, Object>> result = webSjfReportDao.powerReportOfHour(map);
				for(int i=0;i<result.size();i++){
					Map<String, Object> each = result.get(i);
					String powerPerHour = String.valueOf(each.get("power_per_hour"));
					String[] split = powerPerHour.substring(1, powerPerHour.length() - 1).split(",");
					each.remove("power_per_hour");
					for(int j=0;j<split.length;j++){
						Map<String, Object> eachCopy = new HashMap<>();
						eachCopy.putAll(each);
						eachCopy.put("date", j + "时");
						eachCopy.put("total_power", split[j].trim());
						maps.add(eachCopy);
					}
				}
				break;
			default:
				maps = webSjfReportDao.powerReportOfDay(map);
		}
		//针对maps封装补充缺失的时间和分户
		supplyHouseAndTime(map, maps, "total_power");
		Map<String, List<Map<String, Object>>> total_power = getReportData(maps, "total_power");
		return total_power;
	}

	public Map<String, Object> getFinanceReportData(Map<String, Object> map){
		setHouseIds(map);
		ArrayList<Map<String, Object>> maps = webSjfReportDao.getHousesOfFinance(map);
		int total = webSjfReportDao.financeReportCount(map);
		//查询本页数据中所有分户，根据这些的分户id查询所有的电表名称和通讯机名称
		List<Integer> houseIds = new ArrayList<>();
		for(Map<String, Object> each : maps){
			houseIds.add(Integer.parseInt(String.valueOf(each.get("house_id"))));
		}
		ArrayList<Map<String, Object>> meterByHouse;
		if(houseIds.size() > 0){
			meterByHouse = webSjfReportDao.getMeterByHouse(houseIds);
		}else{
			meterByHouse = new ArrayList<>();
		}
		//结果集
		Map<String, Object> result=new HashMap<>();
		//从mongodb获取表读数
		Map<String,Object> dataMap = new HashMap<>();
		//所有设备
		ArrayList<String> deviceList = new ArrayList<>();
		//所有通讯机
		ArrayList<String> tgList = new ArrayList<>();
		//电表分户映射
		Map<String, String> meterHouseMap = new HashMap<>();
		for(Map<String, Object> meter : meterByHouse){
			deviceList.add(String.valueOf(meter.get("device_name")));
			tgList.add(String.valueOf(meter.get("tg_id")));
			meterHouseMap.put(String.valueOf(meter.get("house_id")), String.valueOf(meter.get("device_name"))+String.valueOf(meter.get("tg_id")));
		}

		dataMap.put("deviceList", deviceList);
		dataMap.put("tgList", tgList);
		dataMap.put("code_name", map.get("project_name"));
		//开始时间的mongodb数据
		String startDate = String.valueOf(map.get("startDate"));
		String endDate = String.valueOf(map.get("endDate"));
		dataMap.put("bdate", startDate + " 00:00:00");
		dataMap.put("edate", startDate + " 01:00:00");
		List<WebUIDataVo> sjfReadoutBegin = webUIDataDao.getSjfReadout(dataMap);
		//判断如果结束时间是当天，就拿(当前时间-5min)-(当前时间)的数据，mongodb数据5分钟入一次
		if(DateUtil.isNow(endDate)){
			endDate = DateUtil.Date2Str(new Date());
			dataMap.put("bdate", DateUtil.addDateMinut(endDate, -1));
			dataMap.put("edate", endDate);
		}else{
			dataMap.put("bdate", endDate + " 23:59:59");
			dataMap.put("edate", DateUtil.addDateMinut(endDate + " 23:59:59", 1));
		}
		List<WebUIDataVo> sjfReadoutEnd = webUIDataDao.getSjfReadout(dataMap);
		//根据电表获取分户下的电报读数map
		Map<String, String> beginReadout = new HashMap<>();
		Map<String, String> endReadout = new HashMap<>();
		for(WebUIDataVo vo : sjfReadoutBegin){
			beginReadout.put(vo.getDevice()+vo.getTg(), vo.getVal());
		}
		for(WebUIDataVo vo : sjfReadoutEnd){
			endReadout.put(vo.getDevice()+vo.getTg(), vo.getVal());
		}
		//为结果集添加开始读数和结束读数
		for(Map<String, Object> each : maps){
			String beginNum = beginReadout.get(meterHouseMap.get(String.valueOf(each.get("house_id"))));
			String endNum = endReadout.get(meterHouseMap.get(String.valueOf(each.get("house_id"))));
			each.put("beginReadout", beginNum == null ? "0.00" : beginNum);
			each.put("endReadout", endNum == null ? "0.00" : endNum);
		}
		//补全财务报表所有分户
		supplyHouseAndTimeOfFinance(maps, map);
		result.put("total",total);
		result.put("recordList",maps);
		return result;
	}

}
