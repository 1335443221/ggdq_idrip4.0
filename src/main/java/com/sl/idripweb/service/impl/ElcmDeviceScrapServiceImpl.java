package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.common.utils.*;
import com.sl.idripweb.dao.ElcmDeviceScrapDao;
import com.sl.idripweb.dao.WebCommonDao;
import com.sl.idripweb.service.ElcmDeviceScrapService;
import com.sl.idripweb.service.WebCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("elcmDeviceScrapServiceImpl")
public class ElcmDeviceScrapServiceImpl implements ElcmDeviceScrapService {

	@Autowired
	private ElcmDeviceScrapDao elcmDeviceScrapDao;
	@Autowired
	private WebCommonService webCommonService;
	@Autowired
	private WebCommonDao webCommonDao;
	@Autowired
	DateUtil dateUtil;

	/**
	 * 报废状态查询
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getScrapStatus(Map<String, Object> map) {
		ArrayList<Map<String, Object>> scrapStatus = elcmDeviceScrapDao.getScrapStatus(map);
		return  WebResult.success(scrapStatus);
	}
	/**
	 * 报废列表查询
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getScrapList(Map<String, Object> map) {
		PageUtil.setPageInfo(map);
		ArrayList<HashMap<String, Object>> scrapList=new ArrayList<>();
		ArrayList<HashMap<String, Object>> scrap=elcmDeviceScrapDao.getScrapList(map);
		for (int i=0;i<scrap.size();i++){
			HashMap<String,Object> scrapMap=new HashMap<>();
			scrapMap.put("scrap_id",scrap.get(i).get("scrap_id"));
			scrapMap.put("status",scrap.get(i).get("status"));
			scrapMap.put("status_name",scrap.get(i).get("status_name"));
			scrapMap.put("scrap_number",scrap.get(i).get("scrap_number"));
			scrapMap.put("apply_user",scrap.get(i).get("apply_user"));
			scrapMap.put("apply_time",scrap.get(i).get("apply_time"));
			scrapMap.put("apply_reason",scrap.get(i).get("apply_reason"));
			scrapMap.put("disposal_mode",scrap.get(i).get("disposal_mode"));
			scrapMap.put("disposal_amount",scrap.get(i).get("disposal_amount"));

			Map<String,Object> device=new HashMap<>();
			device.put("device_id",scrap.get(i).get("device_id"));
			device.put("device_type_name",scrap.get(i).get("device_type_name"));
			device.put("device_model",scrap.get(i).get("device_model"));
			device.put("device_name",scrap.get(i).get("device_name"));
			device.put("assets_name",scrap.get(i).get("assets_name"));
			device.put("device_number",scrap.get(i).get("device_number"));
			device.put("install_position",scrap.get(i).get("install_position"));

			scrapMap.put("device",device);
			scrapList.add(scrapMap);
		}

		ResultHandleUtil.addOrderNumber(scrapList, map);  //为结果集添加序号
		int total=elcmDeviceScrapDao.getScrapListCount(map);
		Map<String, Object> result=new HashMap<>();
		result.put("scrapList",scrapList);
		result.put("total",total);
		return WebResult.success(result);
	}

	/**
	 * 撤销
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public WebResult revokeApply(Map<String, Object> map) {
		if (map.get("scrap_id")==null||String.valueOf(map.get("scrap_id")).equals("")) {
				return WebResult.error(301);
		}
		map.put("status",4); //状态改为已撤销
		//报废状态改为已撤销
		int result= elcmDeviceScrapDao.updateScrapStatus(map);

		//新增审批记录 已撤销
		Map<String, Object> scrap=elcmDeviceScrapDao.getScrapDetail(map);
		int approval_id=webCommonDao.getElcmApprovalId(String.valueOf(scrap.get("scrap_number")),"4");
		if(approval_id!=0){
			String apply_by=String.valueOf(map.get("user_id"));
			webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"撤销申请","撤销申请");
			//审批改为已撤销
			webCommonService.updateApproveStatus(String.valueOf(approval_id),"4");
		}
		if(result>0){
			return WebResult.success(200,"撤销成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 再申请
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult reapply(Map<String, Object> map) {
		if (map.get("scrap_id")==null||String.valueOf(map.get("scrap_id")).equals("")) {
				return WebResult.error(301);
		}
		map.put("status",1); //状态改为待审批
		//报废状态改为待审批
		int result= elcmDeviceScrapDao.updateScrapStatus(map);

		//新增审批记录
		Map<String, Object> scrap=elcmDeviceScrapDao.getScrapDetail(map);
		int approval_id=webCommonDao.getElcmApprovalId(String.valueOf(scrap.get("scrap_number")),"4");
		String apply_by=String.valueOf(map.get("user_id"));
		if(approval_id!=0){
			webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"重新发起申请","");
			//审批记录改为待审批
			webCommonService.updateApproveStatus(String.valueOf(approval_id),"1");
			//修改审批时间/审批人
			String apply_at=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
			webCommonService.updateApprove(String.valueOf(approval_id),null,apply_at,apply_by);
		}else{
			String approval_name=String.valueOf(scrap.get("device_name"));
			String project_id=String.valueOf(map.get("project_id"));
			int new_approval_id=webCommonService.addElcmApproval(String.valueOf(scrap.get("scrap_number")),"4",approval_name,apply_by,project_id);
			//新增审批记录
			webCommonService.addElcmApprovalRecord(String.valueOf(new_approval_id),apply_by,"发起申请","");
		}
		if(result>0){
			return WebResult.success(200,"申请成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 删除
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult deleteScrap(Map<String, Object> map) {
		if (map.get("scrap_id")==null||String.valueOf(map.get("scrap_id")).equals("")) {
			return WebResult.error(301);
		}
		//报废详情
		Map<String,Object> scrapDetail=elcmDeviceScrapDao.getScrapDetail(map);
		//删除相关审批
		webCommonService.deleteApproval(String.valueOf(scrapDetail.get("scrap_number")),"4");

		int result=elcmDeviceScrapDao.deleteScrap(map);
		if(result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}
	/**
	 * 新增
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult addScrap(Map<String, Object> map) {
		if (map.get("device_id")==null||String.valueOf(map.get("device_id")).equals("")) {
			return WebResult.error(201,"请选择报废的设备!");
		}
		if (map.get("apply_reason")==null||String.valueOf(map.get("apply_reason")).equals("")) {
			return WebResult.error(201,"请填写申请原因!");
		}
		if (map.get("disposal_mode")==null||String.valueOf(map.get("disposal_mode")).equals("")) {
			return WebResult.error(201,"请填写处置方式!");
		}
		map.put("status",1); //待审批
		map.put("apply_user",map.get("user_id")); //申请人
		int result = 0;
		int status=webCommonService.getDeviceStatus(String.valueOf(map.get("device_id")));
		if (status==5){
			return WebResult.error(201,"该设备已报废,请查看报废单!");
		}
		int count=elcmDeviceScrapDao.getScrapDeciceCount(map);
		if (count>0){
			return WebResult.error(201,"该设备已申请报废,请查看报废单!");
		}

		map.put("scrap_number",webCommonService.getElcmOddNumber("BF",String.valueOf(map.get("project_id")))); //报废编号

		//新增报废
		result=elcmDeviceScrapDao.addScrap(map);
		Map<String, Object> scrap=elcmDeviceScrapDao.getScrapDetail(map);
		//新增审批
		String odd_number=String.valueOf(map.get("scrap_number"));
		String approval_name=String.valueOf(scrap.get("device_name"));
		String apply_by=String.valueOf(map.get("apply_user"));
		String project_id=String.valueOf(map.get("project_id"));
		int approval_id=webCommonService.addElcmApproval(odd_number,"4",approval_name,apply_by,project_id);

		//新增审批记录
		webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"发起申请","");

		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 修改
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult updateScrap(Map<String, Object> map) {
		if (map.get("device_id")==null||String.valueOf(map.get("device_id")).equals("")) {
			return WebResult.error(201,"请选择报废的设备!");
		}
		if (map.get("scrap_id")==null||String.valueOf(map.get("scrap_id")).equals("")) {
			return WebResult.error(201,"请选择报废单!");
		}
		if (map.get("apply_reason")==null||String.valueOf(map.get("apply_reason")).equals("")) {
			return WebResult.error(201,"请填写申请原因!");
		}
		if (map.get("disposal_mode")==null||String.valueOf(map.get("disposal_mode")).equals("")) {
			return WebResult.error(201,"请填写处置方式!");
		}
		map.put("status",1); //待审批
		map.put("apply_user",map.get("user_id")); //申请人
		int status=webCommonService.getDeviceStatus(String.valueOf(map.get("device_id")));
		int result = 0;
		if (status==5){
			return WebResult.error(201,"该设备已报废,请查看报废单!");
		}
		Map<String, Object> old_scrap=elcmDeviceScrapDao.getScrapDetail(map);
		if (!String.valueOf(old_scrap.get("device_id")).equals(String.valueOf(map.get("device_id")))){
			int count=elcmDeviceScrapDao.getScrapDeciceCount(map);
			if (count>0){
				return WebResult.error(201,"该设备已申请报废,请查看报废单!");
			}
		}


		//修改报废
		map.put("apply_time",new Date());
		result=elcmDeviceScrapDao.updateScrap(map);

		//新增审批记录
		Map<String, Object> scrap=elcmDeviceScrapDao.getScrapDetail(map);
		int approval_id=webCommonDao.getElcmApprovalId(String.valueOf(scrap.get("scrap_number")),"4");
		String apply_by=String.valueOf(map.get("user_id"));
		String approval_name=String.valueOf(scrap.get("device_name"));
		if(approval_id!=0){
			webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"重新发起申请","");
			//审批记录改为待审批
			webCommonService.updateApproveStatus(String.valueOf(approval_id),"1");
			//修改审批时间/审批人
			String apply_at=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
			webCommonService.updateApprove(String.valueOf(approval_id),approval_name,apply_at,apply_by);
		}else{
			String project_id=String.valueOf(map.get("project_id"));
			int new_approval_id=webCommonService.addElcmApproval(String.valueOf(scrap.get("scrap_number")),"4",approval_name,apply_by,project_id);
			//新增审批记录
			webCommonService.addElcmApprovalRecord(String.valueOf(new_approval_id),apply_by,"发起申请","");
		}

		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 查看
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getScrapDetail(Map<String, Object> map) {
		if (map.get("scrap_id")==null||String.valueOf(map.get("scrap_id")).equals("")) {
			return WebResult.error(301);
		}
		Map<String,Object> scrapDetail=elcmDeviceScrapDao.getScrapDetail(map); //报废详情
		List<Map<String, Object>> approvalRecord=webCommonService.getElcmApprovalRecord(String.valueOf(scrapDetail.get("scrap_number")),String.valueOf(map.get("project_id"))); //报废详情
		Map<String,Object> result=new HashMap<>();
		result.put("scrapDetail",scrapDetail);
		result.put("approvalRecord",approvalRecord);

		return WebResult.success(result);
	}



}
