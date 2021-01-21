package com.sl.idripapp.service.impl;

import com.sl.common.config.ConstantConfig;
import com.sl.common.entity.SjfEpFees;
import com.sl.common.service.CommonService;
import com.sl.common.service.UIDataDaoImpl;
import com.sl.common.utils.*;
import com.sl.idripapp.dao.SjfAdminDao;
import com.sl.idripapp.dao.SjfUserDao;
import com.sl.idripapp.service.SjfAdminService;
import com.sl.idripapp.service.SjfUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

@Service("sjfUserServiceImpl")
public class SjfUserServiceImpl implements SjfUserService {

	@Autowired
	private SjfUserDao sjfDao;
	@Autowired
	private RedisPoolUtil redisPoolUtil;
	@Autowired
	private DateUtil dateUtil;
	@Autowired
	UIDataDaoImpl uIDataDaoImpl;
	@Autowired
	SjfAdminDao sjfAdminDao;
	@Autowired
	SjfAdminService sjfAdminService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private ConstantConfig constantConfig;

	/**
	 * 通用-获取厂区/楼列表
	 * @param
	 * @return
	 */
	@Override
	public AppResult getFactoryList(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		List<Map<String, Object>>  factoryList=sjfDao.getFactoryList(map);
		for(int i=0;i<factoryList.size();i++){
			factoryList.get(i).put("building_list",sjfDao.getBuildingList(factoryList.get(i)));
		}
		return AppResult.success(factoryList);
	}


	/**
	 * 缴费-获取收费单位
	 * @param
	 * @return
	 */
	@Override
	public AppResult getChargeUnit(Map<String, Object> map) {
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		Map<String, Object> chargeUnit = sjfDao.getChargeUnit(map);
		return AppResult.success(chargeUnit);
	}

	/**
	 * 分户管理-验证分户是否存在
	 * @param
	 * @return
	 */
	@Override
	public AppResult checkHouseNumber(Map<String, Object> map) {
		if(map.get("elec_meter_number")==null){
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		String factory_id="";
		if(map.get("factory_id")!=null){
			factory_id=String.valueOf(map.get("factory_id"));
			map.remove("factory_id");
		}
		ArrayList<Map<String, Object>> list=sjfDao.getAllHouse(map);
		if(list.size()==0){
			return AppResult.error("1018");
		}else{
			if(!"".equals(factory_id)){
				if(factory_id.equals(String.valueOf(list.get(0).get("factory_id")))){
					return AppResult.success();
				}else{
					return AppResult.error("1026");
				}
			}else{
				return AppResult.success();
			}
		}
	}


	/**
	 * 分户管理-获取分户分组列表
	 * @param
	 * @return
	 */
	@Override
	public AppResult getGroupList(Map<String, Object> map) {
		map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
		map.remove("token");
		ArrayList<Map<String, Object>> groupList = sjfDao.getGroupList(map);
		return AppResult.success(groupList);
	}

	/**
	 * 分户管理-缴费分户列表
	 * @param
	 * @return
	 */
	@Override
	public AppResult getUserHouseList(Map<String, Object> map) {
		map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
		map.remove("token");
		List<Map<String,Object>> groupList= sjfDao.getGroupList(map);
		for(int i=0;i<groupList.size();i++){
			groupList.get(i).put("house_list",sjfDao.getUserHouseList(groupList.get(i)));
		}
		return AppResult.success(groupList);
	}
	/**
	 * 分户管理-新增分组
	 * @param
	 * @return
	 */
	@Override
	public AppResult addGroup(Map<String, Object> map) {
		if(map.get("group_name")==null){
			return AppResult.error("1003");
		}
		map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
		map.remove("token");
		//校验分组名称是否重复，重复返回-1
		int countForAdd = sjfDao.getCountForAdd(map);
		if(countForAdd > 0) AppResult.error("1028");
		int result= sjfDao.addGroup(map);
		if(result>0){
			Map<String, Object> stringObjectMap = sjfDao.getGroupList(map).get(0);
			return AppResult.success(stringObjectMap);
		}else{
			return AppResult.error("1010");
		}

	}

	/**
	 * 分户管理-编辑分组信息
	 * @param
	 * @return
	 */
	@Override
	public AppResult updateGroup(Map<String, Object> map) {
		if(map.get("group_name")==null||map.get("group_id")==null){
			return AppResult.error("1003");
		}
		map.remove("token");
		//校验分组名称是否重复，重复返回-1
		int countForUpdate = sjfDao.getCountForUpdate(map);
		int result = 0;
		if(countForUpdate > 0) AppResult.error("1028");
		else result = sjfDao.updateGroup(map);
		if(result == 0){
			return AppResult.error("1010");
		}else{
			return AppResult.success();
		}
	}

	/**
	 * 删除分组
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public AppResult deleteGroup(Map<String, Object> map) {
		if(map.get("group_id")==null){
			return AppResult.error("1003");
		}
		map.remove("token");
		int result=sjfDao.deleteGroup(map);
		if(result>0){
			sjfDao.deleteUserHouseByGroup(map);
		}
		if(result == 0){
			return AppResult.error("1010");
		}else{
			return AppResult.success();
		}
	}

	/**
	 * 删除分户
	 * @param map
	 * @return
	 */
	@Override
	public AppResult deleteHouse(Map<String, Object> map) {
		if(map.get("house_id")==null){
			return AppResult.error("1003");
		}
		map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
		map.remove("token");
		int result = sjfDao.deleteHouse(map);
		if(result == 0){
			return AppResult.error("1010");
		}else{
			return AppResult.success();
		}
	}

	/**
	 * 修改分户
	 * @param map
	 * @return
	 */
	@Override
	public AppResult updateHouse(Map<String, Object> map) {
		if(map.get("house_id")==null||map.get("elec_meter_number")==null){
			return AppResult.error("1003");
		}
		map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
		map.remove("token");

		String factory_id="";
		if(map.get("factory_id")!=null){
			factory_id=String.valueOf(map.get("factory_id"));
			map.remove("factory_id");
		}

		ArrayList<Map<String, Object>> list=sjfDao.getAllHouse(map);
		if(list.size()==0){
			return AppResult.error("1018");
		}else{
			if(!"".equals(factory_id)){
				if(factory_id.equals(list.get(0).get("factory_id"))){
					map.put("new_house_id",list.get(0).get("house_id"));
					int result = sjfDao.updateUserHouse(map);
					if(result == 0){
						return AppResult.error("1010");
					}else{
						return AppResult.success();
					}
				}else{
					return AppResult.error("1026");
				}
			}else{
				map.put("new_house_id",list.get(0).get("house_id"));
				int result = sjfDao.updateUserHouse(map);
				if(result == 0){
					return AppResult.error("1010");
				}else{
					return AppResult.success();
				}
			}
		}
	}

    /**
     * 余额-分户余额列表
     * @param
     * @return
     */
	@Override
	public AppResult getHouseBalance(Map<String, Object> map) {
        map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
        map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
		List<Map<String, Object>> houseList=sjfDao.getBalanceUserHouseList(map);
		if(houseList.size()==0){
			return AppResult.success(houseList);
		}
		List<Map<String, Object>> resultlist=new ArrayList<>();
		houseList=commonService.addBalanceToHouseList(houseList,map);
		for (int i = 0; i < houseList.size(); i++) {
			Double balance=Double.parseDouble(String.valueOf(houseList.get(i).get("balance")));
			Map<String,Object> resultMap=new HashMap<>();
			resultMap.put("house_id",houseList.get(i).get("house_id"));
			resultMap.put("house_name",houseList.get(i).get("house_name"));
			resultMap.put("elec_meter_number",houseList.get(i).get("elec_meter_number"));
			resultMap.put("address",houseList.get(i).get("address"));
			resultMap.put("group_id",houseList.get(i).get("group_id"));
			resultMap.put("factory_id",houseList.get(i).get("factory_id"));
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			resultMap.put("balance",new BigDecimal(nf.format((double)Math.round(balance*100)/100)));
			if(balance<50){
				resultMap.put("balance_state","余额不足");
			}
			if(balance<0){
				resultMap.put("balance_state","已欠费");
			}
			resultlist.add(resultMap);
		}

        return AppResult.success(resultlist);
	}

    /**
     * 用电查询-查询列表
     * @param
     * @return
     */
	@Override
	public AppResult getHouseEpList(Map<String, Object> map) {
        if(map.get("begin_time")==null||map.get("end_time")==null){
            //默认这个月第一天--到昨天
			String firstDay=dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD,0);
			String yesterday=DateUtil.parseDateToStr(DateUtil.addDate(new Date(),0,0,-1,0,0,0,0),DateUtil.DATE_FORMAT_YYYY_MM_DD);
            String today=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
            //如果今天是第一天 默认上一个月
            if(today.equals(firstDay)){
                firstDay=dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD,-1);
            }
            map.put("begin_time",firstDay);
			map.put("end_time",yesterday);
        }
        map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
        map.remove("token");
		List<Map<String, Object>> houseList=sjfDao.getUserHouseList(map);
		if(houseList.size()==0){
			return AppResult.success(houseList);
		}
		map.put("list",houseList);
		//把eplist转换map 然后存到houseList中
		List<Map<String, Object>> epList=sjfDao.getEpListByList(map);
		Map<Object,Object> epMap=new HashMap<>();
		//DecimalFormat df   = new DecimalFormat("######0.00");
		for(int i=0;i<epList.size();i++){
			epMap.put(epList.get(i).get("house_id"),epList.get(i).get("ep"));
		}
		double ep=0.00;
		for(int i=0;i<houseList.size();i++){
			ep=epMap.get(houseList.get(i).get("house_id"))==null?0.00:Double.parseDouble(String.valueOf(epMap.get(houseList.get(i).get("house_id"))));
			houseList.get(i).put("ep",ep);
		}
        return AppResult.success(houseList);
	}

	/**
	 * 询-用电详情
	 * @param
	 * @return
	 */
	@Override
	public AppResult getHouseDetail(Map<String, Object> map) {
        if(map.get("house_id")==null){
            return AppResult.error("1003");
        }
        if(map.get("begin_time")==null||map.get("end_time")==null){
            //默认这个月第一天--到昨天
            String firstDay=dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD,0);
            String yesterday=DateUtil.parseDateToStr(DateUtil.addDate(new Date(),0,0,-1,0,0,0,0),DateUtil.DATE_FORMAT_YYYY_MM_DD);
            String today=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
            //如果今天是第一天 默认上一个月
            if(today.equals(firstDay)){
                firstDay=dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD,-1);
            }
			map.put("begin_time",firstDay);
			map.put("end_time",yesterday);
		}
		map.remove("token");
		//基础信息
		Map<String, Object> house=sjfDao.getHouseDetail(map);

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
		ArrayList<SjfEpFees> epFees=sjfDao.getHouseEpFees(map);
		//求和
		SjfEpFees ep=new SjfEpFees();
		for(int i=0;i<epFees.size();i++){
			ep.setPeakEp(ep.getPeakEp()+epFees.get(i).getPeakEp());
			ep.setPlainEp(ep.getPlainEp()+epFees.get(i).getPlainEp());
			ep.setValleyEp(ep.getValleyEp()+epFees.get(i).getValleyEp());
			ep.setFirstEp(ep.getFirstEp()+epFees.get(i).getFirstEp());
			ep.setSecondEp(ep.getSecondEp()+epFees.get(i).getSecondEp());
			ep.setThirdEp(ep.getThirdEp()+epFees.get(i).getThirdEp());
			ep.setTotalPower(ep.getTotalPower()+epFees.get(i).getTotalPower());
			ep.setTotalFees(ep.getTotalFees()+epFees.get(i).getTotalFees());
		}
		house.put("begin_time",map.get("begin_time"));
		house.put("end_time",map.get("end_time"));
		house.put("all_ep",ep.getTotalPower());
		house.put("peak_ep",ep.getPeakEp());
		house.put("plain_ep",ep.getPlainEp());
		house.put("valley_ep",ep.getValleyEp());
		house.put("first_ep",ep.getFirstEp());
		house.put("second_ep",ep.getSecondEp());
		house.put("third_ep",ep.getThirdEp());
		house.put("elec_fees",ep.getTotalFees());
		return AppResult.success(house);
	}

	/**
	 *  缴费记录-缴费记录列表
	 * @param
	 * @return
	 */
	@Override
	public AppResult getHousePaymentRecord(Map<String, Object> map) {
		if(map.get("begin_time")==null||map.get("end_time")==null){
			map.put("begin_time",DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD)+" 00:00:00");
			map.put("end_time",dateUtil.getMonthFirstDay(DateUtil.DATE_FORMAT_YYYY_MM_DD,0)+" 24:00:00");
		}else{
			map.put("begin_time",map.get("begin_time")+" 00:00:00");
			map.put("end_time",map.get("end_time")+" 24:00:00");
		}
		if(map.get("pageNum")==null||map.get("pageSize")==null){
			map.put("pageNum", 1);
			map.put("pageSize", 20);
		}
		int fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
		map.put("fromNum",fromNum);
		map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
		map.remove("token");
		Map<String, Object> result=new HashMap<>();
		int pageSize=Integer.parseInt(String.valueOf(map.get("pageSize")));
		int total=sjfDao.getHousePaymentRecordCount(map);
		if((fromNum+pageSize)>=total){
			result.put("is_lastpage",true);
		}else{
			result.put("is_lastpage",false);
		}
		List<Map<String, Object>> recordList=sjfDao.getHousePaymentRecord(map);
		List<Map<String, Object>> recordList2=sjfDao.getHousePaymentRecord2(map);
		double income=0;
		double expenditure=0;
		for(int i=0;i<recordList2.size();i++){
			if(String.valueOf(recordList2.get(i).get("amount")).contains("+")){
				income +=Double.parseDouble(String.valueOf(recordList2.get(i).get("amount")).replace("+","")) ;
			}
			if(String.valueOf(recordList2.get(i).get("amount")).contains("-")){
				expenditure += Double.parseDouble(String.valueOf(recordList2.get(i).get("amount")).replace("-",""));
			}
		}
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		result.put("income",new BigDecimal(nf.format(income)));
		result.put("expenditure",new BigDecimal(nf.format(expenditure)));
		result.put("recordList",recordList);
		return AppResult.success(result);
	}

	/**
	 * 缴费记录-缴费记录詳情
	 * @param
	 * @return
	 */
	@Override
	public AppResult getHouseRecordDetail(Map<String, Object> map) {
		if(map.get("record_id")==null){
			return AppResult.error("1003");
		}
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.remove("token");
		Map<String, Object> record=sjfDao.getHouseRecordDetail(map);
		Map<String, Object> unit= sjfDao.getChargeUnit(map);
		record.put("unit_name",unit.get("unit_name"));
		record.put("logo",unit.get("logo"));
		return AppResult.success(record);
	}
	/**
	 * 缴费-获取缴费用户信息(点击下一步时)
	 * @param
	 * @return
	 */
	@Override
	public AppResult getChargeHouseDetail(Map<String, Object> map) {
		if(map.get("elec_meter_number")==null||map.get("group_id")==null||map.get("factory_id")==null){
			return AppResult.error("1003");
		}
		ArrayList<Map<String, Object>> list=sjfDao.getAllHouse(map);
		if(list.size()==0){
			return AppResult.error("1018");
		}

		map.put("house_id",list.get(0).get("house_id"));
		map.put("user_id", JwtToken.getUserId(String.valueOf(map.get("token"))));
		map.put("project_id", JwtToken.getProjectId(String.valueOf(map.get("token"))));
		map.put("type","电费"); //默认电费
		map.remove("token");
		List<Map<String, Object>> userHostList=sjfDao.checkUserHouse(map);
		if(userHostList.size()==0){
			sjfDao.addUserHouse(map);
		}else{
			map.put("user_house_id",userHostList.get(0).get("user_house_id"));
		}
		Map<String, Object> result=sjfDao.getChargeHouseDetail(map);
		Map<String, Object> unit=sjfDao.getChargeUnit(map);

		result.putAll(unit); //缴费单位
		result.put("deduction_amount",0.00); //减扣金额(欠费金额  查余额!!)
		//应缴金额 欠费-补交的
		result.put("payable_amount",Double.parseDouble(result.get("deduction_amount").toString())-Double.parseDouble(result.get("supplement_amount").toString()));
		result.remove("check_in_ep");
		result.remove("check_in_time");
		result.remove("cumulative_amount");
		return AppResult.success(result);
	}
}
