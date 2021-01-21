package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.common.utils.*;
import com.sl.idripweb.dao.ElcmMalfunctionRepairDao;
import com.sl.idripweb.dao.WebCommonDao;
import com.sl.idripweb.service.ElcmMalfunctionRepairService;
import com.sl.idripweb.service.WebCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("elcmMalfunctionRepairServiceImpl")
public class ElcmMalfunctionRepairServiceImpl implements ElcmMalfunctionRepairService {

	@Autowired
	private ElcmMalfunctionRepairDao elcmMalfunctionRepairDao;
	@Autowired
	private WebCommonService webCommonService;
	@Autowired
	private WebCommonDao webCommonDao;
	@Autowired
	DateUtil dateUtil;

	/**
	 * 故障列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getMalfunctionList(Map<String, Object> map) {
		PageUtil.setPageInfo(map);
		ArrayList<HashMap<String, Object>> malfunctionList=elcmMalfunctionRepairDao.getMalfunctionList(map);
		ResultHandleUtil.addOrderNumber(malfunctionList, map);  //为结果集添加序号
		int total=elcmMalfunctionRepairDao.getMalfunctionListCount(map);
		Map<String, Object> result=new HashMap<>();
		result.put("malfunctionList",malfunctionList);
		result.put("total",total);
		return WebResult.success(result);
	}

	/**
	 * 状态
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getMalfunctionStatus(Map<String, Object> map) {
		ArrayList<Map<String, Object>> malfunctionStatus = elcmMalfunctionRepairDao.getMalfunctionStatus(map);
		return WebResult.success(malfunctionStatus);
	}


	/**
	 * 新增故障
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult addMalfunction(Map<String, Object> map) {
		if (map.get("device_id")==null||String.valueOf(map.get("device_id")).equals("")) {
			return WebResult.error(201,"请选择设备!");
		}
		if (map.get("urgency")==null||String.valueOf(map.get("urgency")).equals("")) {
			return WebResult.error(201,"请选择紧急程度!");
		}
		if (map.get("is_repair")==null||String.valueOf(map.get("is_repair")).equals("")) {
			return WebResult.error(201,"请选择转维修还是立即处理!");
		}
		int is_repair=Integer.parseInt(String.valueOf(map.get("is_repair"))); //是否转维修
		if(is_repair==0){  //不转维修
			if(map.get("repair_describe")==null){
				return WebResult.error(201,"请选择处理描述!");
			}
		}

		if(is_repair==1){
			map.put("status",1);    //转维修状态为维修中
		}else{
			map.put("status",2);    //不转维修状态为已完成
		}
		String number=webCommonService.getElcmOddNumber("BX",String.valueOf(map.get("project_id")));
		map.put("malfunction_number",number); //单号
		int result=elcmMalfunctionRepairDao.addMalfunction(map); //上报故障
		if(result>0){  //上报成功
			if(is_repair==1){  //如果是转维修  添加维修信息
				String repair_number=webCommonService.getElcmOddNumber("WX",String.valueOf(map.get("project_id")));
				Map<String, Object> repair=new HashMap<>();
				repair.put("repair_number",repair_number);
				repair.put("malfunction_id",map.get("malfunction_id"));
				repair.put("repair_status",1); //待派单
				repair.put("is_repair_time",new Date()); //转维修时间
				elcmMalfunctionRepairDao.addReapir(repair);
			}else{  //不转维修 添加处理信息
				Map<String, Object> repair=new HashMap<>();
				repair.put("malfunction_id",map.get("malfunction_id"));
				repair.put("repair_user",map.get("user_id"));
				repair.put("repair_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));
				repair.put("repair_describe",map.get("repair_describe")==null?null:map.get("repair_describe"));
				repair.put("repair_url", map.get("repair_url")==null?null:map.get("repair_url"));
				elcmMalfunctionRepairDao.updateMalfunction(repair);

				//添加故障维保记录信息
				Map<String, Object> record = new HashMap<>();
				record.put("maintenance_type_id",4);   //故障
				record.put("device_id", map.get("device_id"));
				record.put("user_id", map.get("user_id"));
				record.put("desc", map.get("repair_describe")==null?null:map.get("repair_describe"));
				record.put("complete_url", map.get("repair_url")==null?null:map.get("repair_url"));
				record.put("odd_number", number);
				webCommonService.addElcmMaintenanceRecord(record);
			}
		}
		if(result>0){
			return WebResult.success(200,"上报成功");
		}else{
			return WebResult.error(201);
		}
	}
	/**
	 * 故障
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getMalfunction(Map<String, Object> map) {
		if (map.get("malfunction_id")==null&&map.get("malfunction_number")==null&&
				String.valueOf(map.get("malfunction_id")).equals("")&&String.valueOf(map.get("malfunction_number")).equals("")) {
			return WebResult.error(301);
		}
		Map<String, Object> result=elcmMalfunctionRepairDao.getMalfunction(map); //故障

		if(result.get("mal_url")==null||String.valueOf(result.get("mal_url")).equals("")){
			result.put("mal_url",new ArrayList<>());
		}else{
			List<String> mal_url = Arrays.asList(String.valueOf(result.get("mal_url")).split(","));
			result.put("mal_url",mal_url);
		}

		if(result.get("repair_url")==null||String.valueOf(result.get("repair_url")).equals("")){
			result.put("repair_url",new ArrayList<>());
		}else{
			List<String> repair_url = Arrays.asList(String.valueOf(result.get("repair_url")).split(","));
			result.put("repair_url",repair_url);
		}
		return WebResult.success(result);
	}

	/**
	 * 处理故障
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult dealMalfunction(Map<String, Object> map) {
		if (map.get("malfunction_id")==null||String.valueOf(map.get("malfunction_id")).equals("")) {
			return WebResult.error(301);
		}
		map.put("status",2); //状态改为已完成
		map.put("repair_user",map.get("user_id"));
		map.put("repair_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));
		int result=elcmMalfunctionRepairDao.updateMalfunction(map);
		if(result>0){
			return WebResult.success(200,"处理成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 修改故障
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult updateMalfunction(Map<String, Object> map) {
		if (map.get("malfunction_id")==null||String.valueOf(map.get("malfunction_id")).equals("")) {
			return WebResult.error(301);
		}
		if (map.get("device_id")==null||String.valueOf(map.get("device_id")).equals("")) {
			return WebResult.error(201,"请选择设备!");
		}
		if (map.get("urgency")==null||String.valueOf(map.get("urgency")).equals("")) {
			return WebResult.error(201,"请选择紧急程度!");
		}
		if (map.get("is_repair")==null||String.valueOf(map.get("is_repair")).equals("")) {
			return WebResult.error(201,"请选择是否转维修!");
		}
		int is_repair=Integer.parseInt(String.valueOf(map.get("is_repair"))); //是否转维修
		if(is_repair==1){
			map.put("status",1);    //转维修状态为维修中
		}else{
			map.put("status",2);    //不转维修状态为已完成
		}
		int result=elcmMalfunctionRepairDao.updateMalfunction(map);
		if(result>0){  //修改成功
			if(is_repair==1){  //如果是转维修  添加维修信息
				String repair_number=webCommonService.getElcmOddNumber("WX",String.valueOf(map.get("project_id")));
				Map<String, Object> repair=new HashMap<>();
				repair.put("repair_number",repair_number);
				repair.put("malfunction_id",map.get("malfunction_id"));
				repair.put("repair_status",1); //待派单
				repair.put("is_repair_time",new Date()); //转维修时间
				elcmMalfunctionRepairDao.addReapir(repair);
			}
		}
		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}
	/**
	 * 撤销故障
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult revokeMalfunction(Map<String, Object> map) {
		if (map.get("malfunction_id")==null||String.valueOf(map.get("malfunction_id")).equals("")) {
			return WebResult.error(301);
		}
		map.put("status",3); //状态改为已撤销
		int result=elcmMalfunctionRepairDao.updateMalfunction(map);
		if(result>0){
			return WebResult.success(200,"撤销成功");
		}else{
			return WebResult.error(201);
		}
	}
	/**
	 * 删除故障
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult deleteMalfunction(Map<String, Object> map) {
		if (map.get("malfunction_id")==null||String.valueOf(map.get("malfunction_id")).equals("")) {
			return WebResult.error(301);
		}
		int result=elcmMalfunctionRepairDao.deleteMalfunction(map);
		if(result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}



	//=============================维修工单==============================//


	/**
	 * 状态
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRepairStatus(Map<String, Object> map) {
		ArrayList<Map<String, Object>> repairStatus = elcmMalfunctionRepairDao.getRepairStatus(map);
		return WebResult.success(repairStatus);
	}



	/**
	 * 维修列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRepairList(Map<String, Object> map) {
		PageUtil.setPageInfo(map);
		ArrayList<HashMap<String, Object>> repairList=elcmMalfunctionRepairDao.getRepairList(map);
		ResultHandleUtil.addOrderNumber(repairList, map);  //为结果集添加序号
		int total=elcmMalfunctionRepairDao.getRepairListCount(map);
		Map<String, Object> result=new HashMap<>();
		result.put("repairList",repairList);
		result.put("total",total);
		return WebResult.success(result);
	}


	/**
	 * 新增故障
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult addRepair(Map<String, Object> map) {
		if (map.get("device_id")==null||String.valueOf(map.get("device_id")).equals("")) {
			return WebResult.error(201,"请选择设备!");
		}
		if (map.get("urgency")==null||String.valueOf(map.get("urgency")).equals("")) {
			return WebResult.error(201,"请选择紧急程度!");
		}
        if (map.get("mal_describe")==null||String.valueOf(map.get("mal_describe")).equals("")) {
            return WebResult.error(201,"请填写故障描述!");
        }

		map.put("is_repair",1);  //转维修
		map.put("status",1);    //转维修状态为维修中 xxx
		String number=webCommonService.getElcmOddNumber("BX",String.valueOf(map.get("project_id")));
		map.put("malfunction_number",number); //单号
		int result=elcmMalfunctionRepairDao.addMalfunction(map); //上报故障
		if(result>0){  //上报成功
			String repair_number=webCommonService.getElcmOddNumber("WX",String.valueOf(map.get("project_id")));
			Map<String, Object> repair=new HashMap<>();
			repair.put("repair_number",repair_number);
			repair.put("malfunction_id",map.get("malfunction_id"));
			repair.put("repair_status",1); //待派单
			repair.put("is_repair_time",new Date()); //转维修时间
			elcmMalfunctionRepairDao.addReapir(repair);
		}
		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}



	/**
	 * 分配人员
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult assignUser(Map<String, Object> map) {
        if (map.get("json")!=null&&!String.valueOf(map.get("json")).equals("")) {
            String json=String.valueOf(map.get("json"));
            List<Map> list=JSONArray.parseArray(json,Map.class);
            for(int i=0;i<list.size();i++){
                if (list.get(i).get("user_id")==null||String.valueOf(list.get(i).get("user_id")).equals("")){
                    return WebResult.error(201,"负责人不能为空,请选择负责人!");
                }
            }
        }

		String json=String.valueOf(map.get("json"));
		List<Map> list=JSONArray.parseArray(json,Map.class);
		List<String> idList = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			idList.add(String.valueOf(list.get(i).get("repair_id")));
		}
		map.put("list",idList);
		int result=0;
		List<String> statusList=elcmMalfunctionRepairDao.getStatusByIds(map);
		if (statusList.contains("3")||statusList.contains("4")||statusList.contains("5")||statusList.contains("6")||statusList.contains("7")){
			return WebResult.error(201,"只有待派单和待接单状态可以分配人员!");
		}

		String assign_time=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
		for(int i=0;i<list.size();i++){
			Map<String, Object> assignMap=new HashMap<>();
			assignMap.put("repair_user",list.get(i).get("user_id"));
			assignMap.put("repair_original_user",list.get(i).get("user_id"));
			assignMap.put("malfunction_id",list.get(i).get("repair_id"));
			assignMap.put("assign_time",assign_time);
			assignMap.put("repair_status",2);
			result=elcmMalfunctionRepairDao.updateMalfunction(assignMap);
		}

		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 维修查看
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRepair(Map<String, Object> map) {
		if (map.get("repair_id")==null||String.valueOf(map.get("repair_id")).equals("")){
			return WebResult.error(301);
		}
		LinkedHashMap<String, Object> repairData = getRepairData(map);
		return WebResult.success(repairData);
	}

	/**
	 * 维修查看
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRepairByIds(Map<String, Object> map) {
		if (map.get("repair_id")==null||String.valueOf(map.get("repair_id")).equals("")){
			return WebResult.error(301);
		}
		String repair_id=String.valueOf(map.get("repair_id"));
		List<String> idList=Arrays.asList(repair_id.split(","));  //id集合
		List<Map<String, Object>> resultList=new ArrayList<>();
		for (int i=0;i<idList.size();i++){
			Map<String, Object> idMap=new HashMap<>();
			idMap.put("repair_id",idList.get(i));
			idMap.put("project_id",map.get("project_id"));
			LinkedHashMap<String, Object> result =getRepairData(idMap);
			resultList.add(result);
		}
		return WebResult.success(resultList);
	}


	/**
	 * 撤销维修
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult revokeRepair(Map<String, Object> map) {
		if (map.get("repair_id")==null||String.valueOf(map.get("repair_id")).equals("")){
			return WebResult.error(301);
		}
		if (map.get("cancel_reason")==null||String.valueOf(map.get("cancel_reason")).equals("")){
			return WebResult.error(201,"请填写取消原因!");
		}
		map.put("status",3); //故障状态改为已取消
		map.put("repair_status",7); //维修状态改为已取消
		map.put("malfunction_id",map.get("repair_id"));
		map.put("cancel_user",map.get("user_id"));
		map.put("cancel_time",new Date());
		Map<String, Object> mal=elcmMalfunctionRepairDao.getMalfunction(map);
		int oldStatus=Integer.parseInt(String.valueOf(mal.get("status")));  //之前状态
		int result = 0;
		if (oldStatus==4||oldStatus==6||oldStatus==7){
			return WebResult.error(201,"当前状态无法取消,请刷新!");
		}

		result=elcmMalfunctionRepairDao.updateMalfunction(map);
		if(result>0){
			//修改设备状态
			String device_id=String.valueOf(mal.get("device_id"));
			String status=String.valueOf(webCommonService.checkDeviceStatus(device_id));
			webCommonService.updateDeviceStatus(device_id,status);
			//修改审批状态
			int approval_id=webCommonDao.getElcmApprovalId(String.valueOf(mal.get("repair_number")),"1");
			if(approval_id!=0){
				String apply_by=String.valueOf(map.get("user_id"));
				webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"撤销申请","撤销申请");
				//审批改为已撤销
				webCommonService.updateApproveStatus(String.valueOf(approval_id),"4");
			}

		}
		if(result>0){
			return WebResult.success(200,"取消成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 我的维修列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getMyRepairList(Map<String, Object> map) {
		if (map.get("is_complete")==null||String.valueOf(map.get("is_complete")).equals("")){
			return WebResult.error(301);
		}
		PageUtil.setPageInfo(map);
		map.put("repair_user_id",map.get("user_id"));
		ArrayList<HashMap<String, Object>> repairList=elcmMalfunctionRepairDao.getMyRepairList(map);
		ResultHandleUtil.addOrderNumber(repairList, map);  //为结果集添加序号

		int total=elcmMalfunctionRepairDao.getMyRepairListCount(map);
		int user_id=Integer.parseInt(String.valueOf(map.get("user_id")));
		for(int i=0;i<repairList.size();i++){
			int repair_user_id=Integer.parseInt(String.valueOf(repairList.get(i).get("repair_user_id")));
			if(user_id==repair_user_id){  //维修人是本人
				repairList.get(i).put("is_self",1);
			}else{
				repairList.get(i).put("is_self",0);
			}
		}
		Map<String, Object> result=new HashMap<>();
		result.put("repairList",repairList);
		result.put("total",total);
		return WebResult.success(result);
	}

	/**
	 * 接单
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult receiveRepair(Map<String, Object> map) {
		if (map.get("repair_id")==null||String.valueOf(map.get("repair_id")).equals("")){
			return WebResult.error(301);
		}
		map.put("repair_status",3); //维修状态改为处理中
		map.put("repair_original_user",map.get("user_id")); //原本维修人改为当前人
		map.put("repair_user",map.get("user_id")); //原本维修人改为当前人
		map.put("malfunction_id",map.get("repair_id"));
		Map<String, Object> mal=elcmMalfunctionRepairDao.getMalfunction(map);
		int oldStatus=Integer.parseInt(String.valueOf(mal.get("repair_status")));  //之前状态
		int result=0;
		if (oldStatus!=2){
			WebResult.error(201,"当前状态无法接单,请刷新!");
		}

		result=elcmMalfunctionRepairDao.updateMalfunction(map);
		if(result>0){  //设备状态改为维修中
			webCommonService.updateDeviceStatus(String.valueOf(mal.get("device_id")),"2");
		}
		if(result>0){
			return WebResult.success(200,"接单成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 退单
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult chargeback(Map<String, Object> map) {
		if (map.get("repair_id")==null||String.valueOf(map.get("repair_id")).equals("")){
			return WebResult.error(301);
		}
		Map<String, Object> repair=elcmMalfunctionRepairDao.getRepair(map);
		int oldStatus=Integer.parseInt(String.valueOf(repair.get("status")));  //之前状态
		int result=0;
		if (oldStatus!=2&&oldStatus!=5&&oldStatus!=3) {  //待接单 已驳回  处理中 才能退单
			return WebResult.error(201,"当前状态无法退单,请刷新!");
		}


		int repair_user=Integer.parseInt(String.valueOf(repair.get("repair_user_id"))); //维修人
		int repair_original_user=Integer.parseInt(String.valueOf(repair.get("repair_original_user"))); //原本维修人

		if(repair_user==repair_original_user){  //不是派单
			map.put("repair_status",1); //维修状态改为待派单
			map.put("assign_time",null);
			map.put("repair_user",null);
			map.put("repair_original_user",null);
			result=elcmMalfunctionRepairDao.chargeback(map);
		}else{  //派单  退单  会返回原本工单
			map.put("repair_status",3); //维修状态改为处理中
			map.put("assign_time",repair.get("assign_time"));
			map.put("repair_user",repair_original_user);
			map.put("repair_original_user",repair_original_user);
			result=elcmMalfunctionRepairDao.chargeback(map);
		}

		if(result>0){
			return WebResult.success(200,"退单成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 转派
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult transfer(Map<String, Object> map) {
		if (map.get("repair_id")==null||String.valueOf(map.get("repair_id")).equals("")){
			return WebResult.error(301);
		}
		if (map.get("repair_user")==null||String.valueOf(map.get("repair_user")).equals("")){
			return WebResult.error(201,"请选择维修人员!");
		}
		if (String.valueOf(map.get("repair_user")).equals(String.valueOf(map.get("user_id")))){
			return WebResult.error(201,"不能转派给自己!");
		}
		map.put("repair_status",2); //维修状态改为待接单
		map.put("repair_original_user",map.get("user_id")); //原本维修人
		map.put("malfunction_id",map.get("repair_id"));

		Map<String, Object> mal=elcmMalfunctionRepairDao.getMalfunction(map);
		int oldStatus=Integer.parseInt(String.valueOf(mal.get("repair_status")));  //之前状态
		int result = 0;
		if (oldStatus!=3&&oldStatus!=5) {  //处理中 已驳回 才能转派
			return WebResult.error(201,"当前状态无法转派,请刷新!");
		}

		result=elcmMalfunctionRepairDao.updateMalfunction(map);
		if(result>0){
			String device_id=String.valueOf(mal.get("device_id"));
			String status=String.valueOf(webCommonService.checkDeviceStatus(device_id));
			webCommonService.updateDeviceStatus(device_id,status); //修改状态
		}
		if(result>0){
			return WebResult.success(200,"转派成功");
		}else{
			return WebResult.error(201);
		}
	}
	/**
	 *处理
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult complete(Map<String, Object> map) {
		if (map.get("repair_id")==null||String.valueOf(map.get("repair_id")).equals("")) {
			return WebResult.error(301);
		}
        if (map.get("repair_describe")==null||String.valueOf(map.get("repair_describe")).equals("")){
            return WebResult.error(201,"请填写维修完成记录!");
        }
		Map<String, Object> repair=elcmMalfunctionRepairDao.getRepair(map);

		int oldStatus=Integer.parseInt(String.valueOf(repair.get("status")));  //之前状态
		int result = 0;
		if (oldStatus!=3&&oldStatus!=5) {  //处理中 已驳回 才能完成
			return WebResult.error(201,"当前状态无法完成维修,请刷新!");
		}


		map.put("repair_status",4); //状态改为待审核
		map.put("status",2); //故障状态改为已完成
		map.put("repair_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));  //处理时间
		map.put("malfunction_id",map.get("repair_id"));
		result=elcmMalfunctionRepairDao.updateMalfunction(map); //处理完成
		if(result>0){ //新增审批
			String apply_by=String.valueOf(map.get("user_id"));
			String odd_number=String.valueOf(repair.get("repair_number"));
			String approval_name=String.valueOf(repair.get("device_name"));
			String project_id=String.valueOf(map.get("project_id"));
			int approval_id=webCommonDao.getElcmApprovalId(odd_number,"1");

			if(approval_id==0){  //如果是处理中  并且没有转派  那么 申请审批  &&apply_by.equals(repair_original_user)
				int new_approval_id=webCommonService.addElcmApproval(odd_number,"1",approval_name,apply_by,project_id);
				//新增审批记录
				webCommonService.addElcmApprovalRecord(String.valueOf(new_approval_id),apply_by,"发起申请","");
			}else{
				webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"重新发起申请","");
				//审批改为待审批
				webCommonService.updateApproveStatus(String.valueOf(approval_id),"1");
				//修改审批时间/审批人
				String apply_at=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
				webCommonService.updateApprove(String.valueOf(approval_id),null,apply_at,apply_by);
			}

		}

		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}

	public LinkedHashMap<String, Object> getRepairData(Map<String, Object> map){
		//返回结果
		LinkedHashMap<String, Object> result = new LinkedHashMap<>();
		//故障设备
		Map<String, Object> mal_device = new HashMap<>();
		//故障描述
		Map<String, Object> mal_content = new HashMap<>();
		//故障完成情况
		Map<String, Object> mal_completed = new HashMap<>();
		//审批记录
		List<Map<String, Object>> approvalRecord=new ArrayList<>();

		Map<String, Object> repair=elcmMalfunctionRepairDao.getRepair(map);

		if(repair.get("repair_url")==null||String.valueOf(repair.get("repair_url")).equals("")){
			repair.put("repair_url",new ArrayList<>());
		}else{
			List<String> repair_url = Arrays.asList(String.valueOf(repair.get("repair_url")).split(","));
			repair.put("repair_url",repair_url);
		}

		if(repair.get("mal_url")==null||String.valueOf(repair.get("mal_url")).equals("")){
			repair.put("mal_url",new ArrayList<>());
		}else{
			List<String> mal_url = Arrays.asList(String.valueOf(repair.get("mal_url")).split(","));
			repair.put("mal_url",mal_url);
		}

		//基础信息
		result.put("repair_id",repair.get("repair_id"));
		result.put("repair_number",repair.get("repair_number"));
		result.put("repair_user_id",repair.get("repair_user_id"));
		result.put("repair_user",repair.get("repair_user"));
		result.put("status",repair.get("status"));
		result.put("status_name",repair.get("status_name"));
		result.put("assign_time",repair.get("assign_time"));
		result.put("repair_time",repair.get("repair_time"));
		//设备信息
		mal_device.put("device_model",repair.get("device_model"));
		mal_device.put("device_name",repair.get("device_name"));
		mal_device.put("device_number",repair.get("device_number"));
		mal_device.put("install_position",repair.get("install_position"));
		//故障描述
		mal_content.put("urgency",repair.get("urgency"));
		mal_content.put("report_time",repair.get("report_time"));
		mal_content.put("report_user",repair.get("report_user"));
		mal_content.put("mal_describe",repair.get("mal_describe"));
		mal_content.put("mal_url",repair.get("mal_url"));

		if(repair.get("repair_describe")==null||String.valueOf(repair.get("repair_describe")).equals("")){
			mal_completed=null;
		}else{
			//完成情况
			mal_completed.put("repair_user",repair.get("repair_user"));
			mal_completed.put("repair_time",repair.get("repair_time"));
			mal_completed.put("repair_describe",repair.get("repair_describe"));
			mal_completed.put("repair_url",repair.get("repair_url"));
			//备件记录
			List<Map<String, Object>> sparepartsRecord=webCommonService.getSparepartsByNumber(String.valueOf(result.get("repair_number")),"1",String.valueOf(map.get("project_id")));
			mal_completed.put("sparepartsRecord",sparepartsRecord);
		}
		//审批记录
		approvalRecord=webCommonService.getElcmApprovalRecord(String.valueOf(result.get("repair_number")),String.valueOf(map.get("project_id")));
		result.put("mal_device",mal_device);
		result.put("mal_content",mal_content);
		result.put("mal_completed",mal_completed);
		result.put("approvalRecord",approvalRecord);
		return result;
	}


}
