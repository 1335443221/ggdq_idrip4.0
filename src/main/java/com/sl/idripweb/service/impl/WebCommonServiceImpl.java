package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.common.config.ConstantConfig;
import com.sl.common.service.CommonService;
import com.sl.common.utils.DateUtil;
import com.sl.common.utils.WebResult;
import com.sl.idripweb.config.MySession;
import com.sl.idripweb.dao.WebCommonDao;
import com.sl.idripweb.service.WebCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service("webCommonServiceImpl")
public class WebCommonServiceImpl implements WebCommonService {

	@Autowired
	WebCommonDao webCommonDao;
	@Autowired
    DateUtil dateUtil;
	@Autowired
	MySession mySession;
	@Autowired
	CommonService commonService;

	/**
	 * 园区-接口
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getFactory(Map<String, Object> map) {
		ArrayList<Map<String, Object>> factory = webCommonDao.getFactory(map);
		return WebResult.success(factory);
	}
	/**
	 * 建筑接口
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getBuilding(Map<String, Object> map) {
		ArrayList<Map<String, Object>> building = webCommonDao.getBuilding(map);
		return WebResult.success(building);
	}
	/**
	 * 分户接口
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getSjfHouse(Map<String, Object> map) {
		ArrayList<Map<String, Object>> sjfHouse = webCommonDao.getSjfHouse(map);
		return WebResult.success(sjfHouse);
	}

	/**
	 * user列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getUserList(Map<String, Object> map) {
		ArrayList<Map<String, Object>> userList = webCommonDao.getUserList(map);
		return WebResult.success(userList);
	}

	/**
	 * 当前登录用户信息
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getUserInfo(Map<String, Object> map, HttpServletRequest request) {
		Map<String, Object> userInfo = mySession.getUserInfo(request);
		return WebResult.success(userInfo);
	}

	/**
	 * 向houseList中存入余额
	 * houseList中必传参数:
	 *  sjf_type表中费率值  parity_price   peak_price  plain_price  valley_price  second_price  third_price  charge_type
	 *  sjf_house表中累计充值金额 :cumulative_amount    house_id   入住时间  check_in_time
	 *  elec_meter表中电表id:  elec_meter_id  通讯机 tg   设备名  device_name
	 *
	 * map中必传参数:  project_id
	 * @param
	 * @return
	 */
	@Override
	public List<Map<String, Object>> addBalanceToHouseList(List<Map<String, Object>> houseList, Map<String, Object> map) {
		return commonService.addBalanceToHouseList(houseList, map);
	}


	/**
	 * 报废 BF  /维保 WB  /维修WX /计划JH   等单号  获取
	 * @param type 报废 BF  /维保 WB  /维修WX /计划JH
	 * @return
	 */
	@Override
	public String getElcmOddNumber(String type,String project_id) {
		String oddNumber=type+DateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYMMDD);  //年年月月日日  前缀
		String oddNumberSuffix="";  //后缀
		switch (type){
			case "BF":  //报废
				oddNumberSuffix= webCommonDao.elcmScrapNumber(oddNumber,project_id);
				break;
			case "JH":  //维保计划
				oddNumberSuffix= webCommonDao.elcmTaskNumber(oddNumber,project_id);
				break;
			case "WB":  //维保工单
				oddNumberSuffix=webCommonDao.elcmTaskRecordNumber(oddNumber,project_id);
				break;
			case "WX":  //维修工单
				oddNumberSuffix= webCommonDao.elcmRepairNumber(oddNumber,project_id);
				break;
			case "BX":  //保修单
				oddNumberSuffix= webCommonDao.elcmMalfunctionNumber(oddNumber,project_id);
				break;
			case "RK":  //入库单
				oddNumberSuffix= webCommonDao.elcmSeparepartsNumberIn(oddNumber,project_id);
				break;
			case "CK":  //出库单
				oddNumberSuffix= webCommonDao.elcmSeparepartsNumberOut(oddNumber,project_id);
				break;
			case "BJ":  //备件号
				oddNumberSuffix= webCommonDao.elcmSeparepartsNumber(oddNumber,project_id);
				break;
			default:
				break;
		}
		if(oddNumberSuffix==null||oddNumberSuffix.equals("")){
			oddNumber=oddNumber+"001";   //没有后缀的时候 为 001第一号
		}else{
			int number=Integer.parseInt(oddNumberSuffix)+1;
			oddNumber=oddNumber+String.format("%03d",number);
		}
		return oddNumber;
	}


	/**
	 * 申请记录
	 * @param
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getElcmApprovalRecord(String oddNumber,String project_id) {
		return webCommonDao.getApprovalRecord(oddNumber,project_id);
	}


	/**
	 * 备件申请情况
	 * @param
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getSparepartsByNumber(String oddNumber,String type_id,String project_id) {
		return webCommonDao.getSparepartsByNumber(oddNumber,type_id,project_id);
	}


	/**
	 * 新增审批
	 * @param
	 * @return
	 */
	@Override
	public int addElcmApproval(String odd_number,String type,String approval_name,String apply_by,String project_id) {
		Map<String, Object> approval_id=new HashMap<>();
		webCommonDao.addElcmApproval(odd_number,type,approval_name,apply_by,project_id,approval_id);
		return Integer.parseInt(String.valueOf(approval_id.get("id")));
	}


	/**
	 * 新增审批记录
	 * @param
	 * @return
	 */
	@Override
	public int addElcmApprovalRecord(String approval_id,String user_id,String content,String status) {

		return webCommonDao.addElcmApprovalRecord(approval_id,user_id,content,status);
	}

	/**
	 * 修改审批状态
	 * @param
	 * @return
	 */
	@Override
	public int updateApproveStatus(String approval_id,String status) {

		return webCommonDao.updateApproveStatus(approval_id,status);
	}

	/**
	 * 修改审批
	 * @param
	 * @return
	 */
	@Override
	public int updateApprove(String approval_id,String approval_name,String apply_at,String apply_by) {

		return webCommonDao.updateApprove(approval_id,approval_name,apply_at,apply_by);
	}


	/**
	 * 修改审批  相关的单号状态
	 * @param type_id  审批类型id
	 * @param odd_number  单号
	 * @param status  状态
	 * @return
	 */
	@Override
	public int updateElcmStatus(String type_id,String odd_number,String status,String project_id) {
		int result=0;
		switch (type_id){
			case "1" :    //维修工单
				if(status.equals("2")){  //已驳回
					webCommonDao.updateMalfunctionStatus(odd_number,null,"5",project_id);
				}
				if(status.equals("3")) {   //已通过
					webCommonDao.updateMalfunctionStatus(odd_number,"2","6",project_id);
				}
				break;
			case "2" :    //维保工单
				if(status.equals("2")){  //已驳回
					webCommonDao.updateTaskRecordStatus(odd_number,"5",project_id);
				}
				if(status.equals("3")) {   //已通过
					webCommonDao.updateTaskRecordStatus(odd_number,"6",project_id);
				}
				break;
			case "3" :    //维保计划
				if(status.equals("2")){  //已驳回
					webCommonDao.updateTaskStatus(odd_number,"2",project_id);
				}
				if(status.equals("3")) {   //已通过
					webCommonDao.updateTaskStatus(odd_number,"4",project_id);
				}
				break;
			case "4" :    //设备报废
				result=webCommonDao.updateScrapStatus(odd_number,status,project_id); //修改报废状态
				break;
		}

		return result;
	}

	/**
	 * 删除审批
	 * @param
	 * @return
	 */
	@Override
	public int deleteApproval(String odd_number,String type_id) {

		return webCommonDao.deleteApproval(odd_number,type_id);
	}


	/**
	 * 修改设备状态
	 * @param
	 * @return
	 */
	@Override
	public int updateDeviceStatus(String device_id,String status) {

		return webCommonDao.updateDeviceStatus(device_id,status);
	}


	/**
	 * 检测设备状态
	 * @param
	 * @return
	 */
	@Override
	public int checkDeviceStatus(String device_id) {
		if(webCommonDao.getMalfunctionCount(device_id)>0){
			return 2;   //2代表有故障  维修中
		}

		int type=webCommonDao.getTaskCount(device_id);
		if(type==2){
			return 3;   //任务为点检任务  把状态改为点检
		}
		if(type==1){
			return 4;   //任务为保养任务  把状态改为保养
		}

		return 1;  //1代表正常运行
	}

	/**
	 * 获取设备状态
	 * @param
	 * @return
	 */
	@Override
	public int getDeviceStatus(String device_id) {
		return webCommonDao.getDeviceStatus(device_id);
	}

	/**
	 * 添加维保记录
	 * @param
	 * @return
	 */
	@Override
	public int addElcmMaintenanceRecord(Map<String,Object> map) {
		return webCommonDao.insertMaintenanceRecord(map);
	}


}
