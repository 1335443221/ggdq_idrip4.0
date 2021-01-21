package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sl.common.config.ConstantConfig;
import com.sl.common.entity.SjfEpFees;
import com.sl.common.entity.SjfYesterdayData;
import com.sl.common.entity.UIDataVo;
import com.sl.common.exception.AppMyException;
import com.sl.common.service.CommonService;
import com.sl.common.service.UIDataDaoImpl;
import com.sl.common.txgljdao.TxgljBaseDao;
import com.sl.common.utils.*;
import com.sl.idripapp.dao.SjfAdminDao;
import com.sl.idripapp.dao.SjfUserDao;
import com.sl.idripapp.service.SjfAdminService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Service("sjfAdminServiceImpl")
public class SjfAdminServiceImpl implements SjfAdminService {

	@Autowired
	private SjfAdminDao sjfAdminDao;
	@Autowired
	private SjfUserDao sjfUserDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private RedisPoolUtil redisPoolUtil;
	@Autowired
	private DateUtil dateUtil;
	@Autowired
	UIDataDaoImpl uIDataDaoImpl;
	@Autowired
	private TxgljBaseDao txgljBaseDao;
	@Autowired
	private ConstantConfig constantConfig;
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 用电查询-查询列表
	 *
	 * @param
	 * @return
	 */
	@Override
	public AppResult getHouseEpList(Map<String, Object> map) {
		if (map.get("begin_time") == null || map.get("end_time") == null) {
			//默认这个月第一天--到昨天
			String firstDay = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, 0);
			String yesterday = DateUtil.parseDateToStr(DateUtil.addDate(new Date(), 0, 0, -1, 0, 0, 0, 0), DateUtil.DATE_FORMAT_YYYY_MM_DD);
			String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
			//如果今天是第一天 默认上一个月
			if (today.equals(firstDay)) {
				firstDay = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, -1);
			}
			map.put("begin_time", firstDay);
			map.put("end_time", yesterday);
		}
		//如果没有厂区 则默认厂区
		if (map.get("factory_id") == null && map.get("building_id") == null) {
			map.put("factory_id", JwtToken.getFactoryId(String.valueOf(map.get("token"))));
		}
		if ("0".equals(String.valueOf(map.get("factory_id")))) {
			map.remove("factory_id");
		}

		//如果没有页码 则默认第一页 每页20行
		if (map.get("pageNum") == null || map.get("pageSize") == null) {
			map.put("pageNum", 1);
			map.put("pageSize", 20);
		}
		int fromNum = (Integer.parseInt(String.valueOf(map.get("pageNum"))) - 1) * Integer.parseInt(String.valueOf(map.get("pageSize")));
		map.put("fromNum", fromNum);
		map.remove("token");
		Map<String, Object> result = new HashMap<>();
		int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
		int total = sjfAdminDao.getHouseEpListCount(map);
		if ((fromNum + pageSize) >= total) {
			result.put("is_lastpage", true);
		} else {
			result.put("is_lastpage", false);
		}
		List<Map<String, Object>> houseList = sjfAdminDao.getHouseEpList(map);
		if (houseList.size() == 0) {
			result.put("recordList", houseList);
			return AppResult.success(result);
		}

		map.put("list", houseList);
		//把eplist转换map 然后存到houseList中
		List<Map<String, Object>> epList = sjfAdminDao.getEpListByList(map);
		Map<Object, Object> epMap = new HashMap<>();
		//DecimalFormat df   = new DecimalFormat("######0.00");
		for (int i = 0; i < epList.size(); i++) {
			epMap.put(epList.get(i).get("house_id"), epList.get(i).get("ep"));
		}
		double ep = 0.00;
		for (int i = 0; i < houseList.size(); i++) {
			ep = epMap.get(houseList.get(i).get("house_id")) == null ? 0.00 : Double.parseDouble(String.valueOf(epMap.get(houseList.get(i).get("house_id"))));
			houseList.get(i).put("ep", ep);
		}
		result.put("recordList", houseList);
		return AppResult.success(result);
	}

	/**
	 * 用电查询-用电详情
	 *
	 * @param
	 * @return
	 */
	@Override
	public AppResult getHouseEpDetail(Map<String, Object> map) {
		if (map.get("house_id") == null) {
			return AppResult.error("1003");
		}
		if (map.get("begin_time") == null || map.get("end_time") == null) {
			//默认这个月第一天--到昨天
			String firstDay = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, 0);
			String yesterday = DateUtil.parseDateToStr(DateUtil.addDate(new Date(), 0, 0, -1, 0, 0, 0, 0), DateUtil.DATE_FORMAT_YYYY_MM_DD);
			String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
			//如果今天是第一天 默认上一个月
			if (today.equals(firstDay)) {
				firstDay = dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, -1);
			}
			map.put("begin_time", firstDay);
			map.put("end_time", yesterday);
		}
		map.remove("token");
		//基础信息
		Map<String, Object> house = sjfUserDao.getHouseDetail(map);
		String begin_time1=map.get("begin_time").toString();
		String check_in_time1=house.get("check_in_time").toString();
		Date begin_time=DateUtil.parseStrToDate(begin_time1,DateUtil.DATE_FORMAT_YYYY_MM_DD);
		Date check_in_time=DateUtil.parseStrToDate(check_in_time1,DateUtil.DATE_FORMAT_YYYY_MM_DD);
		if(begin_time.compareTo(check_in_time)>=0){
			map.put("begin_time",begin_time1);
		}else{
			map.put("begin_time",check_in_time1); //起始时间=入住时间
		}
		//循环信息
		ArrayList<SjfEpFees> epFees = sjfUserDao.getHouseEpFees(map);
		//求和
		SjfEpFees ep = new SjfEpFees();
		for (int i = 0; i < epFees.size(); i++) {
			ep.setPeakEp(ep.getPeakEp() + epFees.get(i).getPeakEp());
			ep.setPlainEp(ep.getPlainEp() + epFees.get(i).getPlainEp());
			ep.setValleyEp(ep.getValleyEp() + epFees.get(i).getValleyEp());
			ep.setFirstEp(ep.getFirstEp() + epFees.get(i).getFirstEp());
			ep.setSecondEp(ep.getSecondEp() + epFees.get(i).getSecondEp());
			ep.setThirdEp(ep.getThirdEp() + epFees.get(i).getThirdEp());
			ep.setTotalPower(ep.getTotalPower() + epFees.get(i).getTotalPower());
			ep.setTotalFees(ep.getTotalFees() + epFees.get(i).getTotalFees());
		}
		house.put("begin_time",begin_time);
		house.put("end_time", map.get("end_time"));
		house.put("all_ep", ep.getTotalPower());
		house.put("peak_ep", ep.getPeakEp());
		house.put("plain_ep", ep.getPlainEp());
		house.put("valley_ep", ep.getValleyEp());
		house.put("first_ep", ep.getFirstEp());
		house.put("second_ep", ep.getSecondEp());
		house.put("third_ep", ep.getThirdEp());
		house.put("elec_fees", ep.getTotalFees());
		return AppResult.success(house);
	}

	/**
	 * 缴费记录-缴费记录列表
	 *
	 * @param
	 * @return
	 */
	@Override
	public AppResult getHousePaymentRecord(Map<String, Object> map) {
		if (map.get("begin_time") == null || map.get("end_time") == null) {
			map.put("begin_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD)+" 00:00:00");
			map.put("end_time", dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD, 0)+" 24:00:00");
		}else{
			map.put("begin_time",map.get("begin_time")+" 00:00:00");
			map.put("end_time",map.get("end_time")+" 24:00:00");
		}
		if (map.get("pageNum") == null || map.get("pageSize") == null) {
			map.put("pageNum", 1);
			map.put("pageSize", 20);
		}
		int fromNum = (Integer.parseInt(String.valueOf(map.get("pageNum"))) - 1) * Integer.parseInt(String.valueOf(map.get("pageSize")));
		map.put("fromNum", fromNum);
		//如果没有厂区 则默认厂区
		if (map.get("factory_id") == null && map.get("building_id") == null && map.get("house_id") == null) {
			map.put("factory_id", JwtToken.getFactoryId(String.valueOf(map.get("token"))));
		}
		if ("0".equals(String.valueOf(map.get("factory_id")))) {
			map.remove("factory_id");
		}
		map.remove("token");
		Map<String, Object> result = new HashMap<>();
		int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
		int total = sjfAdminDao.getHousePaymentRecordCount(map);
		if ((fromNum + pageSize) >= total) {
			result.put("is_lastpage", true);
		} else {
			result.put("is_lastpage", false);
		}
		List<Map<String, Object>> recordList = sjfAdminDao.getHousePaymentRecord(map);
		List<Map<String, Object>> recordList2 = sjfAdminDao.getHousePaymentRecord2(map);
		double income = 0;
		double expenditure = 0;
		for (int i = 0; i < recordList2.size(); i++) {
			if (String.valueOf(recordList2.get(i).get("amount")).contains("+")) {
				income += Double.parseDouble(String.valueOf(recordList2.get(i).get("amount")).replace("+", ""));
			}
			if (String.valueOf(recordList2.get(i).get("amount")).contains("-")) {
				expenditure += Double.parseDouble(String.valueOf(recordList2.get(i).get("amount")).replace("-", ""));
			}
		}
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		result.put("income",new BigDecimal(nf.format(income)));
		result.put("expenditure",new BigDecimal(nf.format(expenditure)));
		result.put("recordList", recordList);
		return AppResult.success(result);
	}

	/**
	 * 缴费记录-缴费记录詳情
	 *
	 * @param
	 * @return
	 */
	@Override
	public AppResult getHouseRecordDetail(Map<String, Object> map) {
		if (map.get("record_id") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		Map<String, Object> record = sjfAdminDao.getHouseRecordDetail(map);
		Map<String, Object> unit = sjfUserDao.getChargeUnit(map);
		record.put("unit_name", unit.get("unit_name"));
		record.put("logo", unit.get("logo"));
		return AppResult.success(record);
	}


	@Override
	public AppResult getHouseList(Map<String, Object> map) {
		//如果没有厂区 则默认厂区
		if (map.get("factory_id") == null && map.get("building_id") == null) {
			map.put("factory_id", JwtToken.getFactoryId(String.valueOf(map.get("token"))));
		}
		if ("0".equals(String.valueOf(map.get("factory_id")))) {
			map.remove("factory_id");
		}
		if (map.get("pageNum") == null || map.get("pageSize") == null) {
			map.put("pageNum", 1);
			map.put("pageSize", 20);
		}
		int fromNum = (Integer.parseInt(String.valueOf(map.get("pageNum"))) - 1) * Integer.parseInt(String.valueOf(map.get("pageSize")));
		int fromEnd = Integer.parseInt(String.valueOf(map.get("pageNum"))) * Integer.parseInt(String.valueOf(map.get("pageSize")));
		map.put("fromNum", fromNum);
		map.put("fromEnd", fromEnd);
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		int arrears = 0;
		if (map.get("arrears") != null && String.valueOf(map.get("arrears")).equals("1")) {
			arrears = 1; //查询欠费用户
		}
		double lower_balance = 0;
		if (map.get("lower_balance") != null) {
			lower_balance = Double.parseDouble(String.valueOf(map.get("lower_balance")));
		} else {
			lower_balance = -1;
		}

		List<Map<String, Object>> houseList = sjfAdminDao.getHouseList(map);
		//如果没有用户入住
		if (houseList.size() == 0) {
			Map<String, Object> result = new HashMap<>();
			result.put("total", 0);
			result.put("houseList", houseList);
			return AppResult.success(result);
		}
		//存入余额
		houseList=addBalanceToHouseList(houseList,map);
		List<Map<String, Object>> resultlist=new ArrayList<>();
		for (int i = 0; i < houseList.size(); i++) {
			Double balance=Double.parseDouble(String.valueOf(houseList.get(i).get("balance")));
			//如果是欠费用户
			if (arrears == 1 && balance > 0) {
				continue;
			}
			//如果是高于此余额的用户
			if (lower_balance != -1 && balance >= lower_balance) {
				continue;
			}
			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("house_id", houseList.get(i).get("house_id"));
			resultMap.put("house_name", houseList.get(i).get("house_name"));
			resultMap.put("elec_meter_number", houseList.get(i).get("elec_meter_number"));
			resultMap.put("address", houseList.get(i).get("address"));
			resultMap.put("phone", houseList.get(i).get("phone"));
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			resultMap.put("balance",new BigDecimal(nf.format((double)Math.round(balance*100)/100)));
			resultlist.add(resultMap);
		}

        /*
        Map<String, Integer> ladder = sjfUserDao.getLadder(map);
        int first_ladder = ladder.get("first_ladder");  //一阶梯临界值
        int second_ladder = ladder.get("second_ladder"); //二阶梯临界值
        Map<String, Object> project =sjfAdminDao.getAllProject(map).get(0);
        //一月一号凌晨数据
        Map<String, Object> fisrtDayMongoDBData=getFirstDayMongoDBData(houseList,String.valueOf(project.get("code_name")));
        //今日凌晨数据
        Map<String, Object> todayMongoDBData=getTodayDayMongoDBData(houseList,String.valueOf(project.get("code_name")));
        //今日redis-凌晨数据(今日能耗)
        Map<String, Map<String, Object>> todayEpMap=getTodayFpgData(houseList,String.valueOf(project.get("code_name")));


        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);*/
      /*  for (int i = 0; i < houseList.size(); i++) {
            Map<String, Object> house=houseList.get(i);
            double all_ep = 0; //总电量
            double all_fees = 0;  //总费用
            double balance = 0; //余额
            double parity_price = Double.parseDouble(String.valueOf(house.get("parity_price"))); //平价
            double peak_price = Double.parseDouble(String.valueOf(house.get("peak_price"))); //峰
            double plain_price = Double.parseDouble(String.valueOf(house.get("plain_price"))); //平
            double valley_price = Double.parseDouble(String.valueOf(house.get("valley_price"))); //谷
            double second_price = Double.parseDouble(String.valueOf(house.get("second_price"))); //二阶梯加价价格
            double third_price = Double.parseDouble(String.valueOf(house.get("third_price"))); //三阶梯加价价格
            double today_peak = 0; //今日峰电量
            double today_plain = 0;//今日平电量
            double today_valley = 0;//今日谷电量
            double today_power = 0;//今日总耗电量
            double now_all_ep = 0;//截止到今日总电量
            double cumulative_amount = Double.parseDouble(String.valueOf(house.get("cumulative_amount"))); //累计充值金额
            int charge_type = Integer.parseInt(String.valueOf(house.get("charge_type")));  //收费方式  1平价 2分时 3阶梯 4分时+阶梯

            //今日数据
            Map<String, Object> todayMap = new HashMap<>();
            if (todayEpMap.get(String.valueOf(house.get("elec_meter_id"))) != null) {
                todayMap = todayEpMap.get(String.valueOf(house.get("elec_meter_id")));  //当日的峰平谷
                today_peak = Double.parseDouble(String.valueOf(todayMap.get("peak")));
                today_plain = Double.parseDouble(String.valueOf(todayMap.get("plain")));
                today_valley = Double.parseDouble(String.valueOf(todayMap.get("valley")));
                today_power = Double.parseDouble(String.valueOf(todayMap.get("power")));
            }

            //今年总数据
            Map<String, Object> epMap = new HashMap<>();
            epMap.put("house_id", house.get("house_id"));
            epMap.put("begin_time", house.get("check_in_time")); //起始时间=入住时间
            epMap.put("end_time", today); //结束时间 今天
            Map<String, Object>  epFees = sjfUserDao.getHouseSumEpFees(epMap);
            if (epFees != null) {
                all_ep = Double.parseDouble(String.valueOf(epFees.get("total_power")));
                all_fees = Double.parseDouble(String.valueOf(epFees.get("total_fees")));
            }
            String key=String.valueOf(house.get("tg")) + "_" +String.valueOf(house.get("device_name"));
            if (fisrtDayMongoDBData.get(key)!= null &&todayMongoDBData.get(key)!= null) {
                all_ep = Double.parseDouble(String.valueOf(todayMongoDBData.get(key))) - Double.parseDouble(String.valueOf(fisrtDayMongoDBData.get(key)));
            }

            balance = cumulative_amount - all_fees;
            switch (charge_type) {
                case 1:
                    balance = balance - (today_power * parity_price);
                    break;
                case 2:
                    //余额=昨日余额-今日峰-今日平-今日谷
                    balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price);
                    break;
                case 3:
                    balance = balance - (today_power * parity_price); //平价的  再减去阶段的
                    now_all_ep = today_power + all_ep;
                    //今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
                    //今日之前一阶梯  当前二阶梯  余额=昨日余额-今日超过一阶梯的电量*二阶梯加价
                    if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - ((today_power - first_ladder) * second_price);
                        //今日之前一阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
                    } else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
                        //今日之前二阶梯  当前二阶梯  余额=昨日余额-今日的电量*二阶梯加价
                    } else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - (today_power * second_price);
                        //今日之前二阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
                    } else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
                        //今日之前三阶段  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价
                    } else if (all_ep > second_ladder) {
                        balance = balance - (today_power * third_price);
                    }
                    break;
                case 4:
                    balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price); //先按峰平谷算出余额
                    now_all_ep = today_power + all_ep;
                    //今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
                    //今日之前一阶梯  当前二阶梯  余额=余额-今日超过一阶梯的电量*二阶梯加价
                    if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - ((today_power - first_ladder) * second_price);
                        //今日之前一阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
                    } else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
                        //今日之前二阶梯  当前二阶梯  余额=余额-今日的电量*二阶梯加价
                    } else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - (today_power * second_price);
                        //今日之前二阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
                    } else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
                        //今日之前三阶段  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价
                    } else if (all_ep > second_ladder) {
                        balance = balance - (today_power * third_price);
                    }
                    break;
            }
            //如果是欠费用户
            if (arrears == 1 && balance > 0) {
                continue;
            }
            //如果是高于此余额的用户
            if (lower_balance != -1 && balance >= lower_balance) {
                continue;
            }
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("house_id", houseList.get(i).get("house_id"));
            resultMap.put("house_name", houseList.get(i).get("house_name"));
            resultMap.put("elec_meter_number", houseList.get(i).get("elec_meter_number"));
            resultMap.put("address", houseList.get(i).get("address"));
            resultMap.put("phone", houseList.get(i).get("phone"));
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            resultMap.put("balance",new BigDecimal(nf.format((double)Math.round(balance*100)/100)));
            resultlist.add(resultMap);
        }*/
		Map<String, Object> result = new HashMap<>();
		int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
		int total = resultlist.size();

		List<Map<String, Object>> fanlist = new ArrayList<>();
		for (int i = fromNum; i < fromEnd; i++) {
			if (i >= resultlist.size()) {
				break;
			}
			fanlist.add(resultlist.get(i));
		}
		if ((fromNum + pageSize) >= total) {
			result.put("is_lastpage", true);
		} else {
			result.put("is_lastpage", false);
		}
		result.put("total", total);
		result.put("houseList", fanlist);
		return AppResult.success(result);
	}

	@Override
	public AppResult getHouseType(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<Map<String, Object>> houseType = sjfAdminDao.getHouseType(map);
		return AppResult.success(houseType);
	}

	@Transactional
	@Override
	public AppResult updateHouse(Map<String, Object> map) {
		if (map.get("house_id") == null || map.get("is_cleared") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		//清零的情况
		if ("1".equals(String.valueOf(map.get("is_cleared")))) {
			map.put("is_check_in", 1); //是否入住改为1
			map.put("supplement_amount", 0); //补加金额改为0
			map.put("cumulative_amount", 0); //累计充值金额改为0
			map.put("purchase_power_time", 0); //购电次数改为0
			map.put("check_in_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD)); //入住时间 当天
		}
		int result=0;
		if (String.valueOf(map.get("is_cleared")).equals("1")) {
			//获取redis ep实时数据
			Map<String, Object> elec_meter = sjfAdminDao.getHouseElecMeter(map); //电表信息
			String _key = String.valueOf(elec_meter.get("tg_id")) + ":" + String.valueOf(elec_meter.get("device_name")) + ":ep";
			String value = redisPoolUtil.get(_key);
			if(value==null||value.equals("")){
				map.put("check_in_ep",0);
			}else{
				map.put("check_in_ep", Double.parseDouble(JSONObject.parseObject(value).get("val").toString()));
			}
			result=sjfAdminDao.updateHouse(map);

			if(result>0){  //修改成功
				sjfInsertDayData();  //插入一遍
				Map<String, Object> pMap=new HashMap<>();
				pMap.put("house_id",elec_meter.get("house_id"));
				pMap.put("project_id",map.get("project_id"));
				Map<String, Object> houseMap=sjfAdminDao.getHouseDetail(pMap);
				List<Map<String, Object>> houseList=new ArrayList<>();
				houseList.add(houseMap);
				houseList=addBalanceToHouseList(houseList,pMap); //余额
				Double balance=Double.parseDouble(String.valueOf(houseList.get(0).get("balance")));
				if(balance<0){
					pMap.put("cumulative_amount", Math.abs(balance)); //累计充值金额改为欠费金额;
					sjfAdminDao.updateHouse(pMap);
				}
			}
		}else{
			result=sjfAdminDao.updateHouse(map);
		}

		if (result == 0) {
			return AppResult.error("1010");
		} else {
			return AppResult.success();
		}
	}

	@Override
	public AppResult getHouseDetail(Map<String, Object> map) {
		if (map.get("house_id") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> house = sjfAdminDao.getHouseDetail(map);
		List<Map<String, Object>> houseList = new ArrayList();
		houseList.add(house);
		houseList = addBalanceToHouseList(houseList, map);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("house_id", houseList.get(0).get("house_id"));
		resultMap.put("house_name", houseList.get(0).get("house_name"));
		resultMap.put("phone", houseList.get(0).get("phone"));
		resultMap.put("building_name", houseList.get(0).get("building_name"));
		resultMap.put("house_number", houseList.get(0).get("house_number"));
		resultMap.put("type_name", houseList.get(0).get("type_name"));
		resultMap.put("type_id", houseList.get(0).get("type_id"));
		resultMap.put("balance", houseList.get(0).get("balance"));
		resultMap.put("elec_meter_number", houseList.get(0).get("elec_meter_number"));
		return AppResult.success(resultMap);
	}

	@Override
	public AppResult deleteHouse(Map<String, Object> map) {
		if (map.get("house_id") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));

		map.put("is_check_in", 0); //是否入住改为0
		map.put("supplement_amount", 0); //补加金额改为0
		map.put("cumulative_amount", 0); //累计充值金额改为0
		map.put("purchase_power_time", 0); //购电次数改为0
		map.put("house_name", null);
		map.put("type_id", null);
		map.put("contact", null);
		map.put("phone", null);
		map.put("remark", null);
		map.put("check_in_time", null);
		map.put("check_in_ep", 0);
		int result = sjfAdminDao.deleteHouse(map);
		if (result == 0) {
			return AppResult.error("1010");
		} else {
			return AppResult.success();
		}
	}

	@Transactional
	@Override
	public AppResult addHouse(Map<String, Object> map) {
		if (map.get("house_name") == null || map.get("phone") == null ||  map.get("elec_meter_number") == null || map.get("type_id") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> elec_meter = sjfAdminDao.getHouseElecMeter(map); //电表信息
		//获取redis ep实时数据
		String _key = String.valueOf(elec_meter.get("tg_id")) + ":" + String.valueOf(elec_meter.get("device_name")) + ":ep";
		String value = redisPoolUtil.get(_key);
		if(value==null||value.equals("")){
			map.put("check_in_ep",0);
		}else{
			map.put("check_in_ep", Double.parseDouble(JSONObject.parseObject(value).get("val").toString()));
		}

		map.put("is_check_in", 1); //是否入住改为1
		map.put("supplement_amount", 0); //补加金额改为0
		map.put("cumulative_amount", 0); //累计充值金额改为0
		map.put("purchase_power_time", 0); //购电次数改为0
		map.put("check_in_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD)); //入住时间 当天
		int result= sjfAdminDao.updateHouse(map);


		if(result>0){  //修改成功
			sjfInsertDayData();  //插入一遍
			Map<String, Object> pMap=new HashMap<>();
			pMap.put("house_id",elec_meter.get("house_id"));
			pMap.put("project_id",map.get("project_id"));
			Map<String, Object> houseMap=sjfAdminDao.getHouseDetail(pMap);
			List<Map<String, Object>> houseList=new ArrayList<>();
			houseList.add(houseMap);
			houseList=addBalanceToHouseList(houseList,pMap); //余额
			Double balance=Double.parseDouble(String.valueOf(houseList.get(0).get("balance")));
			if(balance<0){
				pMap.put("cumulative_amount", Math.abs(balance)); //累计充值金额改为欠费金额;
				sjfAdminDao.updateHouse(pMap);
			}
		}

		if (result == 0) {
			return AppResult.error("1010");
		} else {
			return AppResult.success();
		}
	}

	@Override
	public AppResult moveHouseToType(Map<String, Object> map) {
		if (map.get("house_id_list") == null || map.get("type_id") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		List<String> list = Arrays.asList(String.valueOf(map.get("house_id_list")).split(","));
		if (list.size() == 0) {
			return AppResult.error("1010");
		}
		map.put("list", list);
		int result = sjfAdminDao.updateHouseByIds(map);
		if (result == 0) {
			return AppResult.error("1010");
		} else {
			return AppResult.success();
		}
	}
	@Override
	public AppResult moveAllHouseToType(Map<String, Object> map) {
		if ( map.get("move_type_id") == null) {
			return AppResult.error("1003");
		}
		if (map.get("factory_id") == null && map.get("building_id") == null) {
			map.put("factory_id", JwtToken.getFactoryId(String.valueOf(map.get("token"))));
		}
		if ("0".equals(String.valueOf(map.get("factory_id")))) {
			map.remove("factory_id");
		}

		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		int arrears = 0;
		if (map.get("arrears") != null && String.valueOf(map.get("arrears")).equals("1")) {
			arrears = 1; //查询欠费用户
		}
		double lower_balance = 0;
		if (map.get("lower_balance") != null) {
			lower_balance = Double.parseDouble(String.valueOf(map.get("lower_balance")));
		} else {
			lower_balance = -1;
		}
		List<Map<String, Object>> houseList = sjfAdminDao.getHouseList(map);
		if (houseList.size() == 0) {
			return AppResult.success();
		}
		//存入余额
		houseList=addBalanceToHouseList(houseList,map);
		List<Map<String, Object>> resultlist=new ArrayList<>();
		for (int i = 0; i < houseList.size(); i++) {
			Double balance=Double.parseDouble(String.valueOf(houseList.get(i).get("balance")));
			//如果是欠费用户
			if (arrears == 1 && balance > 0) {
				continue;
			}
			//如果是高于此余额的用户
			if (lower_balance != -1 && balance > lower_balance) {
				continue;
			}
			resultlist.add(houseList.get(i));
		}



       /* Map<String, Object> project =sjfAdminDao.getAllProject(map).get(0);
        Map<String, Integer> ladder = sjfUserDao.getLadder(map);
        int first_ladder = ladder.get("first_ladder");  //一阶梯临界值
        int second_ladder = ladder.get("second_ladder"); //二阶梯临界值
        //一月一号凌晨数据
        Map<String, Object> fisrtDayMongoDBData=getFirstDayMongoDBData(houseList,String.valueOf(project.get("code_name")));
        //今日凌晨数据
        Map<String, Object> todayMongoDBData=getTodayDayMongoDBData(houseList,String.valueOf(project.get("code_name")));
        //今日redis-凌晨数据(今日能耗)
        Map<String, Map<String, Object>> todayEpMap=getTodayFpgData(houseList,String.valueOf(project.get("code_name")));

        List<Map<String, Object>> resultlist=new ArrayList<>();
        String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        for (int i = 0; i < houseList.size(); i++) {
            Map<String, Object> house=houseList.get(i);
            double all_ep = 0; //总电量
            double all_fees = 0;  //总费用
            double balance = 0; //余额
            double parity_price = Double.parseDouble(String.valueOf(house.get("parity_price"))); //平价
            double peak_price = Double.parseDouble(String.valueOf(house.get("peak_price"))); //峰
            double plain_price = Double.parseDouble(String.valueOf(house.get("plain_price"))); //平
            double valley_price = Double.parseDouble(String.valueOf(house.get("valley_price"))); //谷
            double second_price = Double.parseDouble(String.valueOf(house.get("second_price"))); //二阶梯加价价格
            double third_price = Double.parseDouble(String.valueOf(house.get("third_price"))); //三阶梯加价价格
            double today_peak = 0; //今日峰电量
            double today_plain = 0;//今日平电量
            double today_valley = 0;//今日谷电量
            double today_power = 0;//今日总耗电量
            double now_all_ep = 0;//截止到今日总电量
            double cumulative_amount = Double.parseDouble(String.valueOf(house.get("cumulative_amount"))); //累计充值金额
            int charge_type = Integer.parseInt(String.valueOf(house.get("charge_type")));  //收费方式  1平价 2分时 3阶梯 4分时+阶梯

            //今日数据
            Map<String, Object> todayMap = new HashMap<>();
            if (todayEpMap.get(String.valueOf(house.get("elec_meter_id"))) != null) {
                todayMap = todayEpMap.get(String.valueOf(house.get("elec_meter_id")));  //当日的峰平谷
                today_peak = Double.parseDouble(String.valueOf(todayMap.get("peak")));
                today_plain = Double.parseDouble(String.valueOf(todayMap.get("plain")));
                today_valley = Double.parseDouble(String.valueOf(todayMap.get("valley")));
                today_power = Double.parseDouble(String.valueOf(todayMap.get("power")));
            }

            //今年总数据
            Map<String, Object> epMap = new HashMap<>();
            epMap.put("house_id", house.get("house_id"));
            epMap.put("begin_time", house.get("check_in_time")); //起始时间=入住时间
            epMap.put("end_time", today); //结束时间 今天
            Map<String, Object>  epFees = sjfUserDao.getHouseSumEpFees(epMap);
            if (epFees != null) {
                all_ep = Double.parseDouble(String.valueOf(epFees.get("total_power")));
                all_fees = Double.parseDouble(String.valueOf(epFees.get("total_fees")));
            }
            String key=String.valueOf(house.get("tg")) + "_" +String.valueOf(house.get("device_name"));
            if (fisrtDayMongoDBData.get(key)!= null &&todayMongoDBData.get(key)!= null) {
                all_ep = Double.parseDouble(String.valueOf(todayMongoDBData.get(key))) - Double.parseDouble(String.valueOf(fisrtDayMongoDBData.get(key)));
            }

            balance = cumulative_amount - all_fees;
            switch (charge_type) {
                case 1:
                    balance = balance - (today_power * parity_price);
                    break;
                case 2:
                    //余额=昨日余额-今日峰-今日平-今日谷
                    balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price);
                    break;
                case 3:
                    balance = balance - (today_power * parity_price); //平价的  再减去阶段的
                    now_all_ep = today_power + all_ep;
                    //今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
                    //今日之前一阶梯  当前二阶梯  余额=昨日余额-今日超过一阶梯的电量*二阶梯加价
                    if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - ((today_power - first_ladder) * second_price);
                        //今日之前一阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
                    } else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
                        //今日之前二阶梯  当前二阶梯  余额=昨日余额-今日的电量*二阶梯加价
                    } else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - (today_power * second_price);
                        //今日之前二阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
                    } else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
                        //今日之前三阶段  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价
                    } else if (all_ep > second_ladder) {
                        balance = balance - (today_power * third_price);
                    }
                    break;
                case 4:
                    balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price); //先按峰平谷算出余额
                    now_all_ep = today_power + all_ep;
                    //今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
                    //今日之前一阶梯  当前二阶梯  余额=余额-今日超过一阶梯的电量*二阶梯加价
                    if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - ((today_power - first_ladder) * second_price);
                        //今日之前一阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
                    } else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
                        //今日之前二阶梯  当前二阶梯  余额=余额-今日的电量*二阶梯加价
                    } else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
                        balance = balance - (today_power * second_price);
                        //今日之前二阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
                    } else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
                        balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
                        //今日之前三阶段  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价
                    } else if (all_ep > second_ladder) {
                        balance = balance - (today_power * third_price);
                    }
                    break;
            }
            //如果是欠费用户
            if (arrears == 1 && balance > 0) {
                continue;
            }
            //如果是高于此余额的用户
            if (lower_balance != -1 && balance > lower_balance) {
                continue;
            }
            resultlist.add(houseList.get(i));
        }*/
		List<String> list = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();
		for (int i = 0; i < resultlist.size(); i++) {
			list.add(String.valueOf(resultlist.get(i).get("house_id")));
		}
		if (list.size() == 0) {
			return AppResult.error("1010");
		}
		result.put("list", list);
		result.put("type_id", map.get("move_type_id"));
		int i = sjfAdminDao.updateHouseByIds(result);
		if (i == 0) {
			return AppResult.error("1010");
		} else {
			return AppResult.success();
		}
	}

	@Override
	public AppResult getHouseElecMeterNumber(Map<String, Object> map) {
		if (map.get("building_id") == null || map.get("house_number") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> houseElecMeterNumber = sjfAdminDao.getHouseElecMeterNumber(map);
		if (houseElecMeterNumber == null || houseElecMeterNumber.size() == 0) {
			return AppResult.error("1024");
		}
		//已经有人入住
		if ("1".equals(String.valueOf(houseElecMeterNumber.get("is_check_in")))) {
			return AppResult.error("1023");
		}
		houseElecMeterNumber.remove("is_check_in");
		return AppResult.success(houseElecMeterNumber);
	}

	@Override
	public AppResult supplementFees(Map<String, Object> map) {
		if (map.get("house_id") == null || map.get("supplement_amount") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> house = sjfUserDao.getChargeHouseDetail(map);
		Double supplement_amount = Double.parseDouble(String.valueOf(house.get("supplement_amount")));
		Double new_supplement_amount = supplement_amount + Double.parseDouble(String.valueOf(map.get("supplement_amount")));
		params.put("house_id", map.get("house_id"));
		params.put("supplement_amount", new_supplement_amount);
		int result = sjfAdminDao.updateHouse(params);
		if (result == 0) {
			return AppResult.error("1010");
		} else {
			return AppResult.success();
		}
	}

	@Transactional
	@Override
	public AppResult updateFpg(Map<String, Object> map) {
		if (map.get("peak") == null||map.get("plain") == null||map.get("valley") == null||map.get("set_time") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("create_by", JwtToken.getUserName(String.valueOf(map.get("token"))));
		Map<String, Object> fpgMap=new HashMap<>();
		fpgMap.put("project_id",map.get("project_id"));
		sjfAdminDao.deleteAllFpgFuture(fpgMap);
		int result = sjfAdminDao.addFpgFutrue(map);
		if (result == 0) {
			return AppResult.error("1010");
		}
		return AppResult.success();
	}

	@Transactional
	@Override
	public AppResult updateElecSettingByType(Map<String, Object> map) {
		if (map.get("type_id") == null||map.get("charge_type") == null||map.get("set_time") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("create_by", JwtToken.getUserName(String.valueOf(map.get("token"))));
		if (map.get("type_name") != null && !String.valueOf(map.get("type_name")).equals("")) {
			Map<String, Object> typeMap = new HashMap<>();
			typeMap.put("type_id", map.get("type_id"));
			typeMap.put("project_id", map.get("project_id"));
			Map<String, Object> elecSetting = sjfAdminDao.getElecSettingByType(typeMap);
			if(!String.valueOf(elecSetting.get("type_name")).equals(String.valueOf(map.get("type_name")))){
				Map<String, Object> typeMap2 = new HashMap<>();
				typeMap2.put("type_name", map.get("type_name"));
				typeMap2.put("project_id", map.get("project_id"));
				Map<String, Object> elecSetting2 = sjfAdminDao.getElecSettingByType(typeMap2);
				if(elecSetting2!=null){
					return AppResult.error("1027");
				}
				typeMap.put("type_name", map.get("type_name"));
				sjfAdminDao.updateElecSetting(typeMap);
			}

		}
		Map<String, Object> typeMap=new HashMap<>();
		typeMap.put("type_id",map.get("type_id"));
		sjfAdminDao.deleteAllElecSettingFuture(typeMap);
		int result = sjfAdminDao.addElecSettingFuture(map);
		if (result==0) {
			return AppResult.error("1010");
		}

		return AppResult.success();
	}

	@Transactional
	@Override
	public AppResult updateLadder(Map<String, Object> map) {
		if (map.get("first_ladder") == null||map.get("second_ladder") == null||map.get("set_time") == null) {
			return AppResult.error("1003");
		}
		if(Double.parseDouble(String.valueOf(map.get("second_ladder")))-Double.parseDouble(String.valueOf(map.get("first_ladder")))<=0){
			return AppResult.error("1033");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("create_by", JwtToken.getUserName(String.valueOf(map.get("token"))));
		Map<String, Object> ladderMap=new HashMap<>();
		ladderMap.put("project_id",map.get("project_id"));
		sjfAdminDao.deleteAllLadderFuture(ladderMap);
		int result = sjfAdminDao.addLadderFutrue(map);
		if (result == 0) {
			return AppResult.error("1010");
		}
		return AppResult.success();
	}

	@Override
	public AppResult deleteFpgFuture(Map<String, Object> map) {
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		int result = sjfAdminDao.deleteFpgFuture(map);
		if (result == 0) {
			return AppResult.error("1010");
		}
		return AppResult.success();

	}
	@Override
	public AppResult deleteElecSettingFuture(Map<String, Object> map) {
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		int result = sjfAdminDao.deleteElecSettingFuture(map);
		if (result == 0) {
			return AppResult.error("1010");
		}
		return AppResult.success();

	}

	@Override
	public AppResult deleteLadderFuture(Map<String, Object> map) {
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		int result = sjfAdminDao.deleteLadderFuture(map);
		if (result == 0) {
			return AppResult.error("1010");
		}
		return AppResult.success();
	}

	@Override
	public AppResult addElecSetting(Map<String, Object> map) {
		if (map.get("type_name") == null||map.get("charge_type") == null) {
			return AppResult.error("1003");
		}
		map.put("set_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> elecSetting = sjfAdminDao.getElecSettingByType(map);
		int result=0;
		if (elecSetting != null) {
			return AppResult.error("1027");
		} else {
			result = sjfAdminDao.addElecSetting(map);
		}
		if (result==0) {
			return AppResult.error("1010");
		}
		return AppResult.success();
	}

	@Transactional
	@Override
	public AppResult deleteElecSetting(Map<String, Object> map) {
		if (map.get("type_id") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		ArrayList<Map<String, Object>> houseList = sjfAdminDao.getHouseList(map);
		int result=0;
		if (houseList.size() > 0) {
			return AppResult.error("1030");
		} else {
			sjfAdminDao.deleteAllElecSettingFuture(map); //删除待生效
			result = sjfAdminDao.deleteElecSetting(map); //删除正在使用
		}
		if (result==0) {
			return AppResult.error("1010");
		}
		return AppResult.success();
	}

	@Override
	public AppResult checkFpgFuture(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		Map<String, Object> fpgFuture = sjfAdminDao.getFpgFuture(map);
		if (fpgFuture == null || fpgFuture.size() == 0) {
			return AppResult.error("1025");
		}
		Map<String,Object> result=new HashMap<>();
		result.put("set_time",fpgFuture.get("set_time"));
		return AppResult.success(result);
	}

	@Override
	public AppResult checkElecSettingFuture(Map<String, Object> map) {
		if (map.get("type_id") == null) {
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		Map<String, Object> stringObjectMap = sjfAdminDao.checkElecSettingFuture(map);
		if (stringObjectMap == null || stringObjectMap.size() == 0) {
			return AppResult.error("1025");
		}
		return AppResult.success(stringObjectMap);
	}

	@Override
	public AppResult checkLadderFuture(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		Map<String, Object> ladderFuture = sjfAdminDao.getLadderFuture(map);
		if (ladderFuture == null || ladderFuture.size() == 0) {
			return AppResult.error("1025");
		}
		Map<String,Object> result=new HashMap<>();
		result.put("set_time",ladderFuture.get("set_time"));
		return AppResult.success(result);
	}

	@Override
	public AppResult getFpg(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> fpg = sjfAdminDao.getFpg(map);
		return AppResult.success(fpg);
	}

	@Override
	public AppResult getFpgFuture(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		Map<String, Object> fpgFuture = sjfAdminDao.getFpgFuture(map);
		return AppResult.success(fpgFuture);
	}

	@Override
	public AppResult getElecSettingByFuture(Map<String, Object> map){
		if(map.get("type_id")==null){
		return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		Map<String, Object> elecSetting = sjfAdminDao.getElecSettingByFuture(map);
		if (elecSetting != null) {
			elecSetting.putAll(sjfAdminDao.getFpg(map)); //峰平谷
			elecSetting.putAll(sjfAdminDao.getLadder(map)); //阶梯
			Map<String, Object> unit = sjfUserDao.getChargeUnit(map);
			elecSetting.put("unit_name", unit.get("unit_name"));
			elecSetting.put("public_account_number", unit.get("public_account_number"));
		}
		return AppResult.success(elecSetting);
}

	@Override
	public AppResult getElecSettingByType(Map<String, Object> map) {
		if(map.get("type_id")==null){
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> elecSetting = sjfAdminDao.getElecSettingByType(map);
		elecSetting.putAll(sjfAdminDao.getFpg(map)); //峰平谷
		elecSetting.putAll(sjfAdminDao.getLadder(map)); //阶梯
		Map<String, Object> unit = sjfUserDao.getChargeUnit(map);
		elecSetting.put("unit_name", unit.get("unit_name"));
		elecSetting.put("public_account_number", unit.get("public_account_number"));
		return AppResult.success(elecSetting);
	}

	@Override
	public AppResult getLadder(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		Map<String, Object> ladder = sjfAdminDao.getLadder(map);
		return AppResult.success(ladder);
	}

	@Override
	public AppResult getLadderFuture(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("now_time", DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD));
		Map<String, Object> ladderFuture = sjfAdminDao.getLadderFuture(map);
		return AppResult.success(ladderFuture);
	}
	@Override
	public AppResult setAllVal(Map<String, Object> map) {
		if(map.get("project_id")==null){
			return AppResult.error("1003");
		}
		map.put("is_all",1);
		List<Map<String, Object>> houseList= sjfAdminDao.getMeterDataByProject(map);
		Map<String, Object> fpg=sjfAdminDao.getFpg(map);
		String peak = String.valueOf(fpg.get("peak")); //峰
		String plain =String.valueOf(fpg.get("plain")); //平
		String valley =String.valueOf(fpg.get("valley")); //谷
		String fpgValue=getFpgSetVal(peak,plain,valley);
		String dayValue="0101:1231:01";
		int result = 0;
		for(int h=0;h<houseList.size();h++){
			String lpBoxSN=String.valueOf(houseList.get(h).get("sn"));
			String timelpName=houseList.get(h).get("device_name")+"_"+constantConfig.getSjfTimeTag();
			String daylpName=houseList.get(h).get("device_name")+"_"+constantConfig.getSjfDayTag();
			result=commonService.webServiceSetVal(daylpName,lpBoxSN,dayValue);  //下置
			System.out.println("下置day:"+result);
			result=commonService.webServiceSetVal(timelpName,lpBoxSN,fpgValue);  //下置
			System.out.println("下置time:"+result);
			System.out.println("==========================");

		}

		return AppResult.success(result);
	}

	/**
	 * 向houseList中存入余额
	 *
	 * @param houseList
	 * @return
	 */
	public List<Map<String, Object>> addBalanceToHouseList(List<Map<String, Object>> houseList, Map<String, Object> map) {
		int year = DateUtil.getYear(new Date());
		Map<String, Object> ladder = sjfUserDao.getLadder(map);
		Map<String, Object> project =sjfAdminDao.getAllProject(map).get(0);
		String code_name=String.valueOf(project.get("code_name"));
		double first_ladder = Double.parseDouble(String.valueOf(ladder.get("first_ladder")));  //一阶梯临界值
		double second_ladder =Double.parseDouble(String.valueOf(ladder.get("second_ladder"))); //二阶梯临界值

		//今年一年的数据
		Map<String, Object> yearData=getThisYearData(houseList);

		//今天一天的数据 (redis-mongodb)  或者mysql
		Map<String, Map<String, Object>> todayFpgData=getNowTodayFpgData(houseList,code_name);

		// String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
		String yesterday = DateUtil.parseDateToStr(DateUtil.addDate(new Date(),0,0,-1,0,0,0,0), DateUtil.DATE_FORMAT_YYYY_MM_DD);
		for (int i = 0; i < houseList.size(); i++) {
			Map<String, Object> house=houseList.get(i);
			double all_ep = 0; //总电量
			double all_fees = 0;  //总费用
			double balance = 0; //余额
			double parity_price = Double.parseDouble(String.valueOf(house.get("parity_price"))); //平价
			double peak_price = Double.parseDouble(String.valueOf(house.get("peak_price"))); //峰
			double plain_price = Double.parseDouble(String.valueOf(house.get("plain_price"))); //平
			double valley_price = Double.parseDouble(String.valueOf(house.get("valley_price"))); //谷
			double second_price = Double.parseDouble(String.valueOf(house.get("second_price"))); //二阶梯加价价格
			double third_price = Double.parseDouble(String.valueOf(house.get("third_price"))); //三阶梯加价价格
			double today_peak = 0; //今日峰电量
			double today_plain = 0;//今日平电量
			double today_valley = 0;//今日谷电量
			double today_power = 0;//今日总耗电量
			double now_all_ep = 0;//截止到今日总电量
			double cumulative_amount = Double.parseDouble(String.valueOf(house.get("cumulative_amount"))); //累计充值金额
			int charge_type = Integer.parseInt(String.valueOf(house.get("charge_type")));  //收费方式  1平价 2分时 3阶梯 4分时+阶梯

			//今日数据
			Map<String, Object> todayMap = new HashMap<>();
			if (todayFpgData.get(String.valueOf(house.get("elec_meter_id"))) != null) {
				todayMap = todayFpgData.get(String.valueOf(house.get("elec_meter_id")));  //当日的峰平谷
				today_peak = Double.parseDouble(String.valueOf(todayMap.get("peak")));
				today_plain = Double.parseDouble(String.valueOf(todayMap.get("plain")));
				today_valley = Double.parseDouble(String.valueOf(todayMap.get("valley")));
				today_power = Double.parseDouble(String.valueOf(todayMap.get("power")));
			}

			//今年总数据
			Map<String, Object> epMap = new HashMap<>();
			epMap.put("house_id", house.get("house_id"));

			Date begin_time=DateUtil.parseStrToDate(year+"-01-01",DateUtil.DATE_FORMAT_YYYY_MM_DD);
			Date check_in_time=DateUtil.parseStrToDate(house.get("check_in_time").toString(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
			if(begin_time.compareTo(check_in_time)>=0){
				epMap.put("begin_time", year+"-01-01");
			}else{
				epMap.put("begin_time", house.get("check_in_time")); //起始时间=入住时间
			}
			epMap.put("end_time", yesterday); //结束时间 今天
			Map<String, Object>  epFees = sjfUserDao.getHouseSumEpFees(epMap);
			if (epFees != null) {
				all_ep = Double.parseDouble(String.valueOf(epFees.get("total_power")));
				all_fees = Double.parseDouble(String.valueOf(epFees.get("total_fees")));
			}
			String key=String.valueOf(house.get("elec_meter_id"));

			if (yearData.get(key)!= null) {
				all_ep = Double.parseDouble(String.valueOf(yearData.get(key)));
			}

			balance = cumulative_amount - all_fees;   //截止到凌晨时 余额
			switch (charge_type) {
				case 1:
					balance = balance - (today_power * parity_price);
					break;
				case 2:
					//余额=昨日余额-今日峰-今日平-今日谷
					balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price);
					break;
				case 3:
					balance = balance - (today_power * parity_price); //平价的  再减去阶段的
					now_all_ep = today_power + all_ep;
					//今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
					//今日之前一阶梯  当前二阶梯  余额=昨日余额-今日超过一阶梯的电量*二阶梯加价
					if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
						balance = balance - ((today_power - first_ladder) * second_price);
						//今日之前一阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
					} else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
						balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
						//今日之前二阶梯  当前二阶梯  余额=昨日余额-今日的电量*二阶梯加价
					} else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
						balance = balance - (today_power * second_price);
						//今日之前二阶梯  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
					} else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
						balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
						//今日之前三阶段  当前三阶梯  余额=昨日余额-今日超过二阶梯的电量*三阶梯加价
					} else if (all_ep > second_ladder) {
						balance = balance - (today_power * third_price);
					}
					break;
				case 4:
					balance = balance - (today_peak * peak_price) - (today_plain * plain_price) - (today_valley * valley_price); //先按峰平谷算出余额
					now_all_ep = today_power + all_ep;
					//今日之前一阶梯  当前一阶梯  此情况无加价 余额不变
					//今日之前一阶梯  当前二阶梯  余额=余额-今日超过一阶梯的电量*二阶梯加价
					if (all_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
						balance = balance - ((today_power - first_ladder) * second_price);
						//今日之前一阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯到三阶梯电量*二阶梯加价
					} else if (all_ep <= first_ladder && now_all_ep > second_ladder) {
						balance = balance - ((today_power - second_ladder) * third_price) - ((second_ladder - first_ladder) * second_price);
						//今日之前二阶梯  当前二阶梯  余额=余额-今日的电量*二阶梯加价
					} else if (all_ep > first_ladder && now_all_ep <= second_ladder) {
						balance = balance - (today_power * second_price);
						//今日之前二阶梯  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价-二阶梯剩余电量*二阶梯加价
					} else if (all_ep > first_ladder && all_ep <= second_ladder && now_all_ep > second_ladder) {
						balance = balance - ((second_ladder - all_ep) * second_price) - ((now_all_ep - second_ladder) * third_price);
						//今日之前三阶段  当前三阶梯  余额=余额-今日超过二阶梯的电量*三阶梯加价
					} else if (all_ep > second_ladder) {
						balance = balance - (today_power * third_price);
					}
					break;
			}
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			houseList.get(i).put("balance",new BigDecimal(nf.format((double)Math.round(balance*100)/100)));
		}
		return houseList;
	}

	/**
	 * 获取今年一年的数据
	 *
	 * @throws UnsupportedEncodingException
	 * @throws JsonSyntaxException
	 * @throws NumberFormatException
	 */
	public Map<String, Object> getThisYearData(List<Map<String, Object>> devices){
		Map<String, Object> yearMap=new HashMap<>();
		int year = DateUtil.getYear(new Date());
		yearMap.put("year",year);
		yearMap.put("list",devices);
		List<Map<String, Object>> yearList  =sjfAdminDao.getYearDataByList(yearMap);
		Map<String, Object> result=new HashMap<>();
		for(int i=0;i<yearList.size();i++){
			result.put(String.valueOf(yearList.get(i).get("meter_id")),yearList.get(i).get("power"));
		}
		return result;
	}

	/**
	 * 获取今日mysql 峰平谷数据
	 *
	 * @throws UnsupportedEncodingException
	 * @throws JsonSyntaxException
	 * @throws NumberFormatException
	 */
	public Map<String, Map<String, Object>> getNowTodayFpgData(List<Map<String, Object>> devices,String code_name){
		//redis-今日零点的数据
		Map<String, Map<String, Object>> fpgData= getTodayFpgData(devices,code_name);



		String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);  //当前年月日
		Map<String, Object> epMap = new HashMap<>();
		epMap.put("date", today);
		epMap.put("list", devices);
		List<Map<String, Object>> todayEpList = sjfUserDao.getTodayPower(epMap);  //今日电量
		Map<String,Map<String, Object>> todayEpMap = new HashMap<>();  //把今日电量转换为 key-value
		Map<String, Object> todayMap = new HashMap<>();
		//最近一个小时的值  redis-最近小时整点的值
		//  Map<String, Map<String, Object>> todayHourFpgData=getNowHourFpgData(devices,code_name);
		Map<String, Object> hourMap = new HashMap<>();
		//如果redis-今日零点的数据  为空  则返回mysql中 day-data中的数据
		for (int i = 0; i < todayEpList.size(); i++) {
            /*if(todayHourFpgData.get(String.valueOf(todayEpList.get(i).get("meter_id")))!=null){
                hourMap =todayHourFpgData.get(String.valueOf(todayEpList.get(i).get("meter_id")));
                todayMap.put("peak", Double.parseDouble(String.valueOf(todayEpList.get(i).get("peak")))+Double.parseDouble(String.valueOf(hourMap.get("peak")))); //峰
                todayMap.put("plain", Double.parseDouble(String.valueOf(todayEpList.get(i).get("plain")))+Double.parseDouble(String.valueOf(hourMap.get("plain")))); //平
                todayMap.put("valley", Double.parseDouble(String.valueOf(todayEpList.get(i).get("valley")))+Double.parseDouble(String.valueOf(hourMap.get("valley")))); //谷
                todayMap.put("power", Double.parseDouble(String.valueOf(todayEpList.get(i).get("power")))+Double.parseDouble(String.valueOf(hourMap.get("power")))); //总

            }else{*/
			if(fpgData.get(String.valueOf(todayEpList.get(i).get("meter_id")))!=null){
				todayEpMap.put(String.valueOf(todayEpList.get(i).get("meter_id")), fpgData.get(String.valueOf(todayEpList.get(i).get("meter_id"))));
			}else{
				todayMap = new HashMap<>();
				todayMap.put("peak", todayEpList.get(i).get("peak")); //峰
				todayMap.put("plain", todayEpList.get(i).get("plain")); //平
				todayMap.put("valley", todayEpList.get(i).get("valley")); //谷
				todayMap.put("power", todayEpList.get(i).get("power")); //总
				todayEpMap.put(String.valueOf(todayEpList.get(i).get("meter_id")), todayMap);
			}

//            }

		}





		return todayEpMap;
	}

	//===================================///

	/**
	 * 获取今日峰平谷数据
	 *
	 * @throws UnsupportedEncodingException
	 * @throws JsonSyntaxException
	 * @throws NumberFormatException
	 */
	public Map<String, Map<String, Object>> getTodayFpgData(List<Map<String, Object>> devices,String code_name){
		ArrayList<Map<String, Object>> tagList=new ArrayList<>();
		//峰
		Map<String, Object> tagMap=new HashMap<>();
		tagMap.put("tag",constantConfig.getSjfFTag());
		tagMap.put("type","peak");
		tagList.add(tagMap);
		//平
		tagMap=new HashMap<>();
		tagMap.put("tag",constantConfig.getSjfPTag());
		tagMap.put("type","plain");
		tagList.add(tagMap);
		//谷
		tagMap=new HashMap<>();
		tagMap.put("tag",constantConfig.getSjfGTag());
		tagMap.put("type","valley");
		tagList.add(tagMap);
		//ep
		tagMap=new HashMap<>();
		tagMap.put("tag",constantConfig.getSjfEpTag());
		tagMap.put("type","power");
		tagList.add(tagMap);
		//所有redis实时数据
		HashMap<String, Object>  redisData= getRedisData(devices,tagList);


		ArrayList<String> tagMongoDbList=new ArrayList<>();
		ArrayList<String> txgljTagList=new ArrayList<>();
		//ep标签 在MongoDB中
		if(constantConfig.getSjfEpTag().equals("ep")){
			tagMongoDbList.add(constantConfig.getSjfEpTag());
		}else{
			txgljTagList.add(constantConfig.getSjfEpTag());
		}
		//其他标签 在txglj中
		txgljTagList.add(constantConfig.getSjfFTag());
		txgljTagList.add(constantConfig.getSjfPTag());
		txgljTagList.add(constantConfig.getSjfGTag());

		String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
		//今日凌晨 MongoDB数据
		Map<String, Object>  mongoMap = new HashMap<>();
		String bdate=today + " 00:00:00";
		String edate=today + " 00:00:59";
		mongoMap.put("bdate",bdate);  //起始日期
		mongoMap.put("edate",edate);  //结束日期
		mongoMap.put("code_name", code_name);  //MongoDB库
		mongoMap.put("deviceList",getListMongoDBData(devices));  //
		mongoMap.put("tagList",tagMongoDbList);  //

		List<UIDataVo> mongoList = uIDataDaoImpl.getSjfFpgData(mongoMap);
		List<UIDataVo> txgljList = txgljBaseDao.getBaseDataList(code_name,bdate,edate,null,getListMongoDBData(devices),txgljTagList);
		mongoList.addAll(txgljList);

		HashMap<String, Object>  mongoDBData=new HashMap<>();
		for(int i=0;i<mongoList.size();i++){
			UIDataVo uIDataVo=mongoList.get(i);
			mongoDBData.put(uIDataVo.getTg()+"_"+uIDataVo.getDevice()+"_"+uIDataVo.getTag(),uIDataVo.getVal());
		}
		if(redisData.size()==0||mongoList.size()==0){
			Map<String, Map<String, Object>> todayEpMap=new HashMap<>();
			return todayEpMap;
		}


		Map<String, Map<String, Object>> todayEpMap=new HashMap<>();
		for(int d=0;d<devices.size();d++){
			Map<String, Object> device=devices.get(d);
			Map<String, Object> todayMap=new HashMap<>();
			for(int t=0;t<tagList.size();t++){
				String _key = String.valueOf(device.get("tg")) + "_" +String.valueOf(device.get("device_name")) + "_"+ String.valueOf(tagList.get(t).get("tag"));
				if(redisData.get(_key)==null||mongoDBData.get(_key)==null){
					todayMap=new HashMap<>();
					continue;
				}else{
					double redis=Double.parseDouble(String.valueOf(redisData.get(_key)));
					double mongoDB=Double.parseDouble(String.valueOf(mongoDBData.get(_key))); // mongoDBData.get(_key)==null?0:
					todayMap.put(String.valueOf(tagList.get(t).get("type")),redis-mongoDB);
				}
			}
			todayEpMap.put(String.valueOf(device.get("elec_meter_id")),todayMap);
		}
		return todayEpMap;
	}

	/**
	 * 获取redis实时数据
	 *
	 * @throws UnsupportedEncodingException
	 * @throws JsonSyntaxException
	 * @throws NumberFormatException
	 */
	public HashMap<String, Object> getRedisData(List<Map<String, Object>> devices, ArrayList<Map<String, Object>> tagMap) {
		HashMap<String, Object> result = new HashMap<>();
		try {
			DecimalFormat df = new DecimalFormat("0.00");
			Gson gson = new Gson();
			Set<byte[]> keySet = new HashSet<byte[]>();
			for (int d = 0; d < devices.size(); d++) {
				Map<String, Object> device = devices.get(d);
				if (device == null || device.get("tg") == null|| device.get("device_name") == null) {
					continue;
				}
				for (int m = 0; m < tagMap.size(); m++) {
					String _key = String.valueOf(device.get("tg")) + ":" +String.valueOf(device.get("device_name")) + ":"+ String.valueOf(tagMap.get(m).get("tag"));
					keySet.add(_key.getBytes());
				}
			}
			byte[][] values = redisPoolUtil.mget(keySet);
			byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
			for (int i = 0; i < keySet.size(); ++i) {
				if (values[i] == null) continue;
				String val = new String(values[i], "utf-8");
				String key = new String(keys[i], "utf-8");
				result.put(key.split(":")[0]+ "_" +key.split(":")[1] + "_" + key.split(":")[2], df.format(Double.parseDouble(gson.fromJson(val, result.getClass()).get("val").toString())));
			}
		}catch (Exception e){
			throw new AppMyException("401","redis数据异常");
		}
		return result;
	}

	public ArrayList<String> getListMongoDBData(List<Map<String, Object>> devices){
		ArrayList<String>  deviceList=new ArrayList<>();
		for(int i=0;i<devices.size();i++){
			deviceList.add(String.valueOf(devices.get(i).get("device_name")));
		}
		return deviceList;
	}

	//=====================脚本========================//

	/**
	 * 每小时写入消费情况 10分左右写入
	 * @param
	 * @return
	 */
	@Transactional
	public void sjfInsertDayData() {
		String today = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD); //今天
		String yesterday = DateUtil.parseDateToStr(DateUtil.addDate(new Date(),0,0,-1,0,0,0,0), DateUtil.DATE_FORMAT_YYYY_MM_DD);
		int year = DateUtil.getYear(new Date());
		List<SjfEpFees> insertList = new ArrayList<>();//所有表今日消费数据  插入数据
		SjfEpFees sjfEpFees = new SjfEpFees(); //单个消费
		SjfYesterdayData sjfYesterdayData = new SjfYesterdayData(); //单个昨日
		int chargeType = 0; //缴费类型
		Map<String, Object> mongoMap = new HashMap<>();
		float all_yesterday_ep = 0; //总电量
		float now_all_ep = 0; //总电量
		float first_ladder = 0;  //一阶梯临界值
		float second_ladder = 0; //二阶梯临界值
		float second_price = 0; //二阶梯加价价格
		float third_price = 0; //三阶梯加价价格
		float totalFees = 0; //总费用
		float today_power = 0; //总费用
		String meter_id=""; //电表编号
		//所有项目 遍历项目是因为满足去不同的MongoDB库取数据
		List<Map<String, Object>> projectList = sjfAdminDao.getAllProject(new HashMap<>());
		for (int i = 0; i < projectList.size(); i++) {
			//项目下所有表数据
			Map<String, Object> map  = new HashMap<>();
			map.put("today", today);
			map.put("project_id", projectList.get(i).get("project_id"));
			List<SjfYesterdayData> yesterdayList = sjfAdminDao.getAllMeterData(map); //所有表昨日数据

			//年数据
			map.put("is_all",1);
			List<Map<String, Object>> houseList = sjfAdminDao.getMeterDataByProject(map);
			Map<String, Object> yearData=getThisYearData(houseList);

			//拼写 插入数据
			for (int j = 0; j < yesterdayList.size(); j++) {
				sjfYesterdayData = yesterdayList.get(j);
				meter_id=String.valueOf(sjfYesterdayData.getElecMeterId());  //电表编号
				chargeType = sjfYesterdayData.getChargeType();
				sjfEpFees = new SjfEpFees();
				sjfEpFees.setDate(today); //日期
				sjfEpFees.setHouseId(sjfYesterdayData.getHouseId()); //house_id
				sjfEpFees.setTotalPower(sjfYesterdayData.getPower()); //总用电量

				// 阶梯  峰平谷 消费情况
				sjfEpFees.setParityFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower()); //平价费用 平价电费*总电量
				sjfEpFees.setPeakEp(sjfYesterdayData.getPeak()); //峰电量
				sjfEpFees.setPlainEp(sjfYesterdayData.getPlain()); //平电量
				sjfEpFees.setValleyEp(sjfYesterdayData.getValley()); //谷电量
				sjfEpFees.setPeakFees(sjfYesterdayData.getPeakPrice() * sjfYesterdayData.getPeak()); //峰电费*峰电量
				sjfEpFees.setPlainFees(sjfYesterdayData.getPlainPrice() * sjfYesterdayData.getPlain()); //平电费*平电量
				sjfEpFees.setValleyFees(sjfYesterdayData.getValleyPrice() * sjfYesterdayData.getValley()); //谷电费*谷电量

				if (yearData.get(meter_id)!= null) {   //如果年统计表中有就用统计表
					all_yesterday_ep = Float.parseFloat(String.valueOf(yearData.get(meter_id)));
				}else{   //没有就用累加
					Map<String, Object> epMap = new HashMap<>();
					epMap.put("house_id", sjfYesterdayData.getHouseId());
					if(sjfYesterdayData.getCheckInTime()==null){
						epMap.put("begin_time",yesterday); //起始时间=入住时间
					}else{
						Date begin_time=DateUtil.parseStrToDate(year+"-01-01",DateUtil.DATE_FORMAT_YYYY_MM_DD);
						Date check_in_time=DateUtil.parseStrToDate(sjfYesterdayData.getCheckInTime(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
						if(begin_time.compareTo(check_in_time)>=0){
							epMap.put("begin_time", year+"-01-01");
						}else{
							epMap.put("begin_time", sjfYesterdayData.getCheckInTime()); //起始时间=入住时间
						}
					}
					epMap.put("end_time", yesterday); //结束时间 昨天
					Map<String, Object> epFees = sjfUserDao.getHouseSumEpFees(epMap);
					if(epFees!=null){
						all_yesterday_ep = Float.parseFloat(String.valueOf(epFees.get("total_power")));
					}
				}


				today_power = sjfYesterdayData.getPower();
				now_all_ep = all_yesterday_ep + sjfYesterdayData.getPower();
				first_ladder = sjfYesterdayData.getFirstLadder();
				second_ladder = sjfYesterdayData.getSecondLadder();
				second_price = sjfYesterdayData.getSecondPrice();
				third_price = sjfYesterdayData.getThirdPrice();
				totalFees = sjfEpFees.getPeakFees() + sjfEpFees.getPlainFees() + sjfEpFees.getValleyFees(); //总费用
				//昨日之前一阶梯  当前一阶梯
				if (now_all_ep <= first_ladder && all_yesterday_ep <= first_ladder) {
					sjfEpFees.setFirstEp(sjfYesterdayData.getPower());
					//今日之前一阶梯  当前二阶梯
				} else if (all_yesterday_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
					sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
					sjfEpFees.setSecondEp(today_power - first_ladder);
					//今日之前一阶梯  当前三阶梯
				} else if (all_yesterday_ep <= first_ladder && now_all_ep > second_ladder) {
					sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
					sjfEpFees.setSecondEp(second_ladder - first_ladder);
					sjfEpFees.setThirdEp(now_all_ep - second_ladder);
					//今日之前二阶梯  当前二阶梯
				} else if (all_yesterday_ep > first_ladder && now_all_ep <= second_ladder) {
					sjfEpFees.setSecondEp(today_power);
					//今日之前二阶梯  当前三阶梯
				} else if (all_yesterday_ep > first_ladder && all_yesterday_ep <= second_ladder && now_all_ep > second_ladder) {
					sjfEpFees.setSecondEp(second_ladder - all_yesterday_ep);
					sjfEpFees.setThirdEp(now_all_ep - second_ladder);
					//今日之前三阶段  当前三阶梯
				} else if (all_yesterday_ep > second_ladder) {
					sjfEpFees.setThirdEp(today_power);
				}

				sjfEpFees.setSecondIncrementFees(sjfEpFees.getSecondEp() * second_price);
				sjfEpFees.setThirdIncrementFees(sjfEpFees.getThirdEp() * third_price);
				if(chargeType==1){
					sjfEpFees.setTotalFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower()); //总费用 平价电费*总电量
				}
				if(chargeType==2){
					sjfEpFees.setTotalFees(sjfEpFees.getPeakFees() + sjfEpFees.getPlainFees() + sjfEpFees.getValleyFees());//总费用 峰+平+谷
				}
				if(chargeType==3){
					sjfEpFees.setTotalFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower() + sjfEpFees.getSecondIncrementFees() + sjfEpFees.getThirdIncrementFees());
				}
				if(chargeType==4){
					sjfEpFees.setTotalFees(totalFees + sjfEpFees.getSecondIncrementFees() + sjfEpFees.getThirdIncrementFees());
				}

				insertList.add(sjfEpFees);


				//分为四种收费方式

              /*  switch (chargeType) {
                    //平价
                    case 1:
                        sjfEpFees.setParityFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower()); //平价费用 平价电费*总电量
                        sjfEpFees.setTotalFees(sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower()); //总费用 平价电费*总电量
                        insertList.add(sjfEpFees);
                        break;
                    //峰平谷
                    case 2:
                        sjfEpFees.setPeakEp(sjfYesterdayData.getPeak()); //峰电量
                        sjfEpFees.setPlainEp(sjfYesterdayData.getPlain()); //平电量
                        sjfEpFees.setValleyEp(sjfYesterdayData.getValley()); //谷电量
                        sjfEpFees.setPeakFees(sjfYesterdayData.getPeakPrice() * sjfYesterdayData.getPeak()); //峰电费*峰电量
                        sjfEpFees.setPlainFees(sjfYesterdayData.getPlainPrice() * sjfYesterdayData.getPlain()); //平电费*平电量
                        sjfEpFees.setValleyFees(sjfYesterdayData.getValleyPrice() * sjfYesterdayData.getValley()); //谷电费*谷电量
                        sjfEpFees.setTotalFees(sjfEpFees.getPeakFees() + sjfEpFees.getPlainFees() + sjfEpFees.getValleyFees());//总费用 峰+平+谷

                        insertList.add(sjfEpFees);
                        break;
                    //阶梯
                    case 3:
                        if (yearData.get(meter_id)!= null) {   //如果年统计表中有就用统计表
                            all_yesterday_ep = Float.parseFloat(String.valueOf(yearData.get(meter_id)));
                        }else{   //没有就用累加
                            Map<String, Object> epMap = new HashMap<>();
                            epMap.put("house_id", sjfYesterdayData.getHouseId());
                            if(sjfYesterdayData.getCheckInTime()==null){
                                epMap.put("begin_time",yesterday); //起始时间=入住时间
                            }else{
                                epMap.put("begin_time", sjfYesterdayData.getCheckInTime()); //起始时间=入住时间
                            }
                            epMap.put("end_time", yesterday); //结束时间 昨天
                            Map<String, Object> epFees = sjfUserDao.getHouseSumEpFees(epMap);
                            if(epFees!=null){
                                all_yesterday_ep = Float.parseFloat(String.valueOf(epFees.get("total_power")));
                            }
                        }

                        today_power = sjfYesterdayData.getPower();
                        now_all_ep = all_yesterday_ep + sjfYesterdayData.getPower();
                        first_ladder = sjfYesterdayData.getFirstLadder();
                        second_ladder = sjfYesterdayData.getSecondLadder();
                        second_price = sjfYesterdayData.getSecondPrice();
                        third_price = sjfYesterdayData.getThirdPrice();

                        totalFees = sjfYesterdayData.getParityPrice() * sjfYesterdayData.getPower(); //总费用 平价电费*总电量;
                        //昨日之前一阶梯  当前一阶梯
                        if (now_all_ep <= first_ladder && all_yesterday_ep <= first_ladder) {
                            sjfEpFees.setFirstEp(sjfYesterdayData.getPower());
                            //今日之前一阶梯  当前二阶梯
                        } else if (all_yesterday_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                            sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
                            sjfEpFees.setSecondEp(today_power - first_ladder);
                            //今日之前一阶梯  当前三阶梯
                        } else if (all_yesterday_ep <= first_ladder && now_all_ep > second_ladder) {
                            sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
                            sjfEpFees.setSecondEp(second_ladder - first_ladder);
                            sjfEpFees.setThirdEp(now_all_ep - second_ladder);
                            //今日之前二阶梯  当前二阶梯
                        } else if (all_yesterday_ep > first_ladder && now_all_ep <= second_ladder) {
                            sjfEpFees.setSecondEp(today_power);
                            //今日之前二阶梯  当前三阶梯
                        } else if (all_yesterday_ep > first_ladder && all_yesterday_ep <= second_ladder && now_all_ep > second_ladder) {
                            sjfEpFees.setSecondEp(second_ladder - all_yesterday_ep);
                            sjfEpFees.setThirdEp(now_all_ep - second_ladder);
                            //今日之前三阶段  当前三阶梯
                        } else if (all_yesterday_ep > second_ladder) {
                            sjfEpFees.setThirdEp(today_power);
                        }

                        sjfEpFees.setSecondIncrementFees(sjfEpFees.getSecondEp() * second_price);
                        sjfEpFees.setThirdIncrementFees(sjfEpFees.getThirdEp() * third_price);
                        sjfEpFees.setTotalFees(totalFees + sjfEpFees.getSecondIncrementFees() + sjfEpFees.getThirdIncrementFees());
                        insertList.add(sjfEpFees);
                        break;
                    //阶梯+峰平谷
                    case 4:
                        sjfEpFees.setPeakEp(sjfYesterdayData.getPeak()); //峰电量
                        sjfEpFees.setPlainEp(sjfYesterdayData.getPlain()); //平电量
                        sjfEpFees.setValleyEp(sjfYesterdayData.getValley()); //谷电量
                        sjfEpFees.setPeakFees(sjfYesterdayData.getPeakPrice() * sjfYesterdayData.getPeak()); //峰电费*峰电量
                        sjfEpFees.setPlainFees(sjfYesterdayData.getPlainPrice() * sjfYesterdayData.getPlain()); //平电费*平电量
                        sjfEpFees.setValleyFees(sjfYesterdayData.getValleyPrice() * sjfYesterdayData.getValley()); //谷电费*谷电量

                        if (yearData.get(meter_id)!= null) {   //如果年统计表中有就用统计表
                            all_yesterday_ep = Float.parseFloat(String.valueOf(yearData.get(meter_id)));
                        }else{   //没有就用累加
                            Map<String, Object> epMap = new HashMap<>();
                            epMap.put("house_id", sjfYesterdayData.getHouseId());
                            if(sjfYesterdayData.getCheckInTime()==null){
                                epMap.put("begin_time",yesterday); //起始时间=入住时间
                            }else{
                                epMap.put("begin_time", sjfYesterdayData.getCheckInTime()); //起始时间=入住时间
                            }
                            epMap.put("end_time", yesterday); //结束时间 昨天
                            Map<String, Object> epFees = sjfUserDao.getHouseSumEpFees(epMap);
                            if(epFees!=null){
                                all_yesterday_ep = Float.parseFloat(String.valueOf(epFees.get("total_power")));
                            }
                        }


                        today_power = sjfYesterdayData.getPower();
                        now_all_ep = all_yesterday_ep + sjfYesterdayData.getPower();
                        first_ladder = sjfYesterdayData.getFirstLadder();
                        second_ladder = sjfYesterdayData.getSecondLadder();
                        second_price = sjfYesterdayData.getSecondPrice();
                        third_price = sjfYesterdayData.getThirdPrice();
                        totalFees = sjfEpFees.getPeakFees() + sjfEpFees.getPlainFees() + sjfEpFees.getValleyFees(); //总费用
                        //昨日之前一阶梯  当前一阶梯
                        if (now_all_ep <= first_ladder && all_yesterday_ep <= first_ladder) {
                            sjfEpFees.setFirstEp(sjfYesterdayData.getPower());
                            //今日之前一阶梯  当前二阶梯
                        } else if (all_yesterday_ep <= first_ladder && now_all_ep > first_ladder && now_all_ep <= second_ladder) {
                            sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
                            sjfEpFees.setSecondEp(today_power - first_ladder);
                            //今日之前一阶梯  当前三阶梯
                        } else if (all_yesterday_ep <= first_ladder && now_all_ep > second_ladder) {
                            sjfEpFees.setFirstEp(first_ladder - all_yesterday_ep);
                            sjfEpFees.setSecondEp(second_ladder - first_ladder);
                            sjfEpFees.setThirdEp(now_all_ep - second_ladder);
                            //今日之前二阶梯  当前二阶梯
                        } else if (all_yesterday_ep > first_ladder && now_all_ep <= second_ladder) {
                            sjfEpFees.setSecondEp(today_power);
                            //今日之前二阶梯  当前三阶梯
                        } else if (all_yesterday_ep > first_ladder && all_yesterday_ep <= second_ladder && now_all_ep > second_ladder) {
                            sjfEpFees.setSecondEp(second_ladder - all_yesterday_ep);
                            sjfEpFees.setThirdEp(now_all_ep - second_ladder);
                            //今日之前三阶段  当前三阶梯
                        } else if (all_yesterday_ep > second_ladder) {
                            sjfEpFees.setThirdEp(today_power);
                        }

                        sjfEpFees.setSecondIncrementFees(sjfEpFees.getSecondEp() * second_price);
                        sjfEpFees.setThirdIncrementFees(sjfEpFees.getThirdEp() * third_price);
                        sjfEpFees.setTotalFees(totalFees + sjfEpFees.getSecondIncrementFees() + sjfEpFees.getThirdIncrementFees());
                        insertList.add(sjfEpFees);
                        break;
                    default :
                        insertList.add(sjfEpFees);
                        break;
                }*/

			}
		}

		Map<String,Object> deleteParam=new HashMap<>();
		deleteParam.put("date",today);
		List<String> deleteHosue=new ArrayList<>();
		if(insertList.size()!=0){
			for(int i=0;i<insertList.size();i++){
				deleteHosue.add(String.valueOf(insertList.get(i).getHouseId()));
			}
			deleteParam.put("list",deleteHosue);
			sjfAdminDao.deleteAllSjfData(deleteParam);
			sjfAdminDao.insertAllSjfData(insertList);
		}
		logger.info(today + "写入电费情况成功!");
	}

	public String getFpgSetVal(String peak,String plain,String valley) {
		//14:00-17:00,19:00-22:00
		List<Map<String,String>> list=new ArrayList<>();
		Map<String,String> map=new HashMap<>();
		String[] splits = peak.split(","); //峰
		for (int i = 0; i < splits.length; i++) {
			String split = splits[i];
			String key1 = split.split("-")[0].split(":")[0]+split.split("-")[0].split(":")[1];
			String key2 = split.split("-")[1].split(":")[0]+split.split("-")[1].split(":")[1];
			if (key2.equals("0000")) {
				key2 = "2400";
			}
			map=new HashMap<>();
			map.put("key1",key1);
			map.put("key2",key2);
			map.put("key3","2");  //峰
			list.add(map);
		}

		splits =plain.split(","); //平
		for (int i = 0; i < splits.length; i++) {
			String split = splits[i];
			String key1 = split.split("-")[0].split(":")[0]+split.split("-")[0].split(":")[1];
			String key2 = split.split("-")[1].split(":")[0]+split.split("-")[1].split(":")[1];
			if (key2.equals("0000")) {
				key2 = "2400";
			}
			map=new HashMap<>();
			map.put("key1",key1);
			map.put("key2",key2);
			map.put("key3","3");  //平
			list.add(map);
		}


		splits =valley.split(","); //谷
		for (int i = 0; i < splits.length; i++) {
			String split = splits[i];
			String key1 = split.split("-")[0].split(":")[0]+split.split("-")[0].split(":")[1];
			String key2 = split.split("-")[1].split(":")[0]+split.split("-")[1].split(":")[1];
			if (key2.equals("0000")) {
				key2 = "2400";
			}
			map=new HashMap<>();
			map.put("key1",key1);
			map.put("key2",key2);
			map.put("key3","4");  //谷
			list.add(map);
		}

		//排序
		Collections.sort(list, new Comparator<Map>() {
			@Override
			public int compare(Map o1, Map o2) {
				try {
					int key1 = Integer.parseInt(o1.get("key1").toString());
					int key2 = Integer.parseInt(o2.get("key1").toString());
					if (key1 > key2) {
						return 1;
					} else if (key1 < key2) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
		StringBuffer result1 = new StringBuffer("");
		StringBuffer result2 = new StringBuffer("");
		StringBuffer result3 = new StringBuffer("");
		for(int i=0;i<list.size();i++){
			result1.append(list.get(i).get("key1")+",");
			result2.append(list.get(i).get("key2")+",");
			result3.append(list.get(i).get("key3")+",");
		}

		if (result1.toString().substring(result1.toString().length() - 1).equals(",")) {
			result1.deleteCharAt(result1.length() - 1);
		}
		if (result2.toString().substring(result2.toString().length() - 1).equals(",")) {
			result2.deleteCharAt(result2.length() - 1);
		}
		if (result3.toString().substring(result3.toString().length() - 1).equals(",")) {
			result3.deleteCharAt(result3.length() - 1);
		}

		return result1.append(":").append(result2).append(":").append(result3).toString();

	}

}
