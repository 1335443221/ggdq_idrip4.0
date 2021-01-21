package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.common.utils.*;
import com.sl.idripweb.dao.ElcmTaskDao;
import com.sl.idripweb.dao.WebCommonDao;
import com.sl.idripweb.service.ElcmTaskService;
import com.sl.idripweb.service.WebCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.collection.immutable.Page;

import java.util.*;


@Service("elcmTaskServiceImpl")
public class ElcmTaskServiceImpl implements ElcmTaskService {

	@Autowired
	private ElcmTaskDao elcmTaskDao;
	@Autowired
	private WebCommonService webCommonService;
	@Autowired
	private WebCommonDao webCommonDao;
	@Autowired
	DateUtil dateUtil;

	/**
	 * 任务状态
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getTaskStatus(Map<String, Object> map) {
		ArrayList<Map<String, Object>> taskStatus = elcmTaskDao.getTaskStatus(map);
		return WebResult.success(taskStatus);
	}
	/**
	 * 任务类别
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getTaskType(Map<String, Object> map) {
		ArrayList<Map<String, Object>> taskType = elcmTaskDao.getTaskType(map);
		return WebResult.success(taskType);
	}


	/**
	 * 新增任务
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult addTask(Map<String, Object> map) {
		if (map.get("maintenance_type_id")==null||String.valueOf(map.get("maintenance_type_id")).equals("")) {
			return WebResult.error(201,"请选择维保类型");
		}
		if (map.get("task_name")==null||String.valueOf(map.get("task_name")).equals("")) {
			return WebResult.error(201,"请填写维保计划名称");
		}
		if (map.get("begin_time")==null||String.valueOf(map.get("begin_time")).equals("")||map.get("end_time")==null||String.valueOf(map.get("end_time")).equals("")) {
			return WebResult.error(201,"请选择计划时间");
		}
		String begin_time=String.valueOf(map.get("begin_time"));
		String today=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
		Date beginDate=DateUtil.parseStrToDate(begin_time,DateUtil.DATE_FORMAT_YYYY_MM_DD);
		Date todayDate=DateUtil.parseStrToDate(today,DateUtil.DATE_FORMAT_YYYY_MM_DD);

		if (todayDate.compareTo(beginDate)>0){
			return WebResult.error(201,"计划开始时间不能早于当前时间!");
		}

		if (map.get("is_repeat")==null||String.valueOf(map.get("is_repeat")).equals("")) {
			return WebResult.error(201,"请选择是否按周期执行");
		}
		if (map.get("relation")==null||String.valueOf(map.get("relation")).equals("")||String.valueOf(map.get("relation")).equals("[]")) {
			return WebResult.error(201,"请至少选择一个设备");
		}
		String end_time=  String.valueOf(map.get("end_time"));
		Date endDate=DateUtil.parseStrToDate(end_time,DateUtil.DATE_FORMAT_YYYY_MM_DD);
		//验证 是否重复
		List<Map<String,Object>>  taskRelationList=elcmTaskDao.getElcmTaskRelation(map);
		Map<String, Map<String,Object>> relation=new HashMap<>();
		for(int i=0;i<taskRelationList.size();i++){
			Map<String, Object> taskRelation=new HashMap<>();
			taskRelation.put("begin_time",taskRelationList.get(i).get("begin_time"));
			taskRelation.put("end_time",taskRelationList.get(i).get("end_time"));
			taskRelation.put("device_name",taskRelationList.get(i).get("device_name"));
			taskRelation.put("maintain_parts",taskRelationList.get(i).get("maintain_parts"));
			relation.put(String.valueOf(taskRelationList.get(i).get("maintain_id")),taskRelation);
		}

		String relationStr=String.valueOf(map.get("relation"));
		List<Map> relationList= JSONArray.parseArray(relationStr,Map.class);
		//判断是否重复
       /* for(int i=0;i<relationList.size();i++){
            String maintain_id=String.valueOf(relationList.get(i).get("maintain_id"));
            List<String> maintain_ids= JSONArray.parseArray(maintain_id,String.class);
            for(int j=0;j<maintain_ids.size();j++){
              if (relation.get(maintain_ids.get(j))!=null){  //如果已有的关联关系 包含 新增的关联关系
               String begin_time2= String.valueOf(relation.get(maintain_ids.get(j)).get("begin_time"));
               String end_time2= String.valueOf(relation.get(maintain_ids.get(j)).get("end_time"));
                  Date beginDate2=DateUtil.parseStrToDate(begin_time2,DateUtil.DATE_FORMAT_YYYY_MM_DD);
                  Date endDate2=DateUtil.parseStrToDate(end_time2,DateUtil.DATE_FORMAT_YYYY_MM_DD);
                //不重叠~~
               if (beginDate.compareTo(endDate2)>0||beginDate2.compareTo(endDate)>0){
                continue;
               }else{  //重叠
                   String maintain_parts= String.valueOf(relation.get(maintain_ids.get(j)).get("maintain_parts"));
                   String device_name= String.valueOf(relation.get(maintain_ids.get(j)).get("device_name"));
                    return "所选的`"+device_name+"`的`"+maintain_parts+"`部位在计划时间内与其他计划重复!";
               }
              }
            }
        }*/


		String project_id=String.valueOf(map.get("project_id"));
		String number=webCommonService.getElcmOddNumber("JH",project_id);
		map.put("task_number",number); //工单
		map.put("status",1); //默认 待审核状态
		map.put("is_start",1); //默认开启
		int result=elcmTaskDao.addTask(map);
		if(result>0){ //新增成功

			//添加一条维保计划的审批
			String task_name=String.valueOf(map.get("task_name"));
			String user_id=String.valueOf(map.get("user_id"));
			int approval_id= webCommonService.addElcmApproval(number,"3",task_name,user_id,project_id);
			String apply_by=String.valueOf(map.get("user_id"));
			//添加一条审批记录
			webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"发起申请","");

			//新增对应关系  task device  maintain
			for(int i=0;i<relationList.size();i++){
				Map<String,Object> relationMap=new HashMap<>();
				relationMap.put("task_id",map.get("task_id"));
				relationMap.put("device_id",relationList.get(i).get("device_id"));
				String maintain_id=String.valueOf(relationList.get(i).get("maintain_id"));
				List<String> maintain_ids= JSONArray.parseArray(maintain_id,String.class);
				for(int j=0;j<maintain_ids.size();j++){
					relationMap.put("maintain_id",maintain_ids.get(j));
					elcmTaskDao.addTaskRelation(relationMap);
				}
			}

		}

		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}

	}

	/**
	 * 任务列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getTaskList(Map<String, Object> map) {
		if (map.get("is_agree")==null||String.valueOf(map.get("is_agree")).equals("")){
			return WebResult.error(301);
		}
		PageUtil.setPageInfo(map);
		ArrayList<HashMap<String, Object>> taskList=elcmTaskDao.getTaskList(map);
		ResultHandleUtil.addOrderNumber(taskList, map);  //为结果集添加序号
		int total=elcmTaskDao.getTaskListCount(map);
		Map<String, Object> result=new HashMap<>();
		result.put("taskList",taskList);
		result.put("total",total);
		return WebResult.success(result);
	}


	/**
	 * 撤销申请
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult revokeTask(Map<String, Object> map) {
		if (map.get("task_id")==null||String.valueOf(map.get("task_id")).equals("")){
			return WebResult.error(301);
		}
		//修改任务状态
		map.put("status",3);
		map.put("is_start",0); //不开启
		int result= elcmTaskDao.updateTask(map);
		if(result>0){
			//新增审批记录 已撤销
			Map<String, Object> task=elcmTaskDao.getTask(map);
			int approval_id=webCommonDao.getElcmApprovalId(String.valueOf(task.get("task_number")),"3");
			if(approval_id!=0){
				String apply_by=String.valueOf(map.get("user_id"));
				webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"撤销申请","撤销申请");
				//审批改为已撤销
				webCommonService.updateApproveStatus(String.valueOf(approval_id),"4");
			}
		}
		if(result>0){
			return WebResult.success(200,"撤销成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 修改申请
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult updateTask(Map<String, Object> map) {
		if (map.get("task_id")==null||String.valueOf(map.get("task_id")).equals("")){
			return WebResult.error(301);
		}
		if (map.get("maintenance_type_id")==null||String.valueOf(map.get("maintenance_type_id")).equals("")) {
			return WebResult.error(201,"请选择维保类型");
		}
		if (map.get("task_name")==null||String.valueOf(map.get("task_name")).equals("")) {
			return WebResult.error(201,"请填写维保计划名称");
		}
		if (map.get("begin_time")==null||String.valueOf(map.get("begin_time")).equals("")||map.get("end_time")==null||String.valueOf(map.get("end_time")).equals("")) {
			return WebResult.error(201,"请选择计划时间");
		}
		String begin_time=String.valueOf(map.get("begin_time"));
		String today=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
		Date beginDate=DateUtil.parseStrToDate(begin_time,DateUtil.DATE_FORMAT_YYYY_MM_DD);
		Date todayDate=DateUtil.parseStrToDate(today,DateUtil.DATE_FORMAT_YYYY_MM_DD);

		if (todayDate.compareTo(beginDate)>0){
			return WebResult.error(201,"计划开始时间不能早于当前时间!");
		}

		if (map.get("is_repeat")==null||String.valueOf(map.get("is_repeat")).equals("")) {
			return WebResult.error(201,"请选择是否按周期执行");
		}
		if (map.get("relation")==null||String.valueOf(map.get("relation")).equals("")||String.valueOf(map.get("relation")).equals("[]")) {
			return WebResult.error(201,"请至少选择一个设备");
		}
		map.put("status",1); //默认 待审核状态
		map.put("is_start",1); //默认开启
		int result=elcmTaskDao.updateTask(map);
		if(result>0){ //新增成功  添加一条维保计划的审批
			//新增审批记录 待审批
			Map<String, Object> task=elcmTaskDao.getTask(map);
			int approval_id=webCommonDao.getElcmApprovalId(String.valueOf(task.get("task_number")),"3");
			String apply_by=String.valueOf(map.get("user_id"));
			if(approval_id==0){
				String approval_name=String.valueOf(map.get("task_name"));
				String project_id=String.valueOf(map.get("project_id"));
				int new_approval_id=webCommonService.addElcmApproval(String.valueOf(task.get("task_number")),"3",approval_name,apply_by,project_id);
				//新增审批记录
				webCommonService.addElcmApprovalRecord(String.valueOf(new_approval_id),apply_by,"发起申请","");
			}else{
				webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"重新发起申请","");
				//审批改为待审批
				webCommonService.updateApproveStatus(String.valueOf(approval_id),"1");
				//修改审批名字
				String task_name=String.valueOf(map.get("task_name"));
				String apply_at=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
				webCommonService.updateApprove(String.valueOf(approval_id),task_name,apply_at,apply_by);
			}

			//新增对应关系  task device  maintain
			elcmTaskDao.deleteTaskRelation(map);  //删除旧的关系  新增新的关系
			String relation=String.valueOf(map.get("relation"));
			List<Map> relationList= JSONArray.parseArray(relation,Map.class);
			for(int i=0;i<relationList.size();i++){
				Map<String,Object> relationMap=new HashMap<>();
				relationMap.put("task_id",map.get("task_id"));
				relationMap.put("device_id",relationList.get(i).get("device_id"));
				String maintain_id=String.valueOf(relationList.get(i).get("maintain_id"));
				List<String> maintain_ids= JSONArray.parseArray(maintain_id,String.class);
				for(int j=0;j<maintain_ids.size();j++){
					relationMap.put("maintain_id",maintain_ids.get(j));
					elcmTaskDao.addTaskRelation(relationMap);
				}
			}
		}
		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 删除申请
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult deleteTask(Map<String, Object> map) {
		if (map.get("task_id")==null||String.valueOf(map.get("task_id")).equals("")){
			return WebResult.error(301);
		}
		Map<String, Object> task=elcmTaskDao.getTask(map);
		//删除任务
		int result= elcmTaskDao.deleteTask(map);
		if(result>0){
			//删除关系
			elcmTaskDao.deleteTaskRelation(map);
			//删除审批
			webCommonService.deleteApproval(String.valueOf(task.get("task_number")),"3");
		}
		if(result>0){
			return WebResult.success(200,"删除成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 任务详情
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getTaskDetail(Map<String, Object> map) {
		if (map.get("task_id")==null||String.valueOf(map.get("task_id")).equals("")){
			return WebResult.error(301);
		}
		Map<String, Object> task=elcmTaskDao.getTask(map);

		List<Map<String, Object>> relationList=elcmTaskDao.getTaskRelation(map);
		List<Map<String, Object>> relation=new ArrayList<>();
		List<String> deviceList=new ArrayList<>();
		for(int i=0;i<relationList.size();i++){
			String device_id=String.valueOf(relationList.get(i).get("device_id"));
			if(deviceList.contains(device_id)){  //如果存在
				continue;
			}else{
				deviceList.add(device_id);
				Map<String, Object> relationMap=new HashMap<>();
				relationMap.put("device_id",Integer.parseInt(device_id));
				relationMap.put("device_name",relationList.get(i).get("device_name"));
				relationMap.put("device_number",relationList.get(i).get("device_number"));
				relationMap.put("install_position",relationList.get(i).get("install_position"));
				relationMap.put("device_model",relationList.get(i).get("device_model"));
				relation.add(relationMap);
			}
		}

		for(int i=0;i<relation.size();i++){
			List<Map<String, Object>> list=new ArrayList<>();
			String device_id=String.valueOf(relation.get(i).get("device_id"));
			for(int j=0;j<relationList.size();j++) {
				if(device_id.equals(String.valueOf(relationList.get(j).get("device_id")))){
					Map<String, Object> maintain=new HashMap<>();
					maintain.put("maintain_id",relationList.get(j).get("maintain_id"));
					maintain.put("maintain_parts",relationList.get(j).get("maintain_parts"));
					maintain.put("operation_content",relationList.get(j).get("operation_content"));
					maintain.put("operation_steps",relationList.get(j).get("operation_steps"));
					maintain.put("maintain_intervals",String.valueOf(relationList.get(j).get("maintain_intervals"))+String.valueOf(relationList.get(i).get("maintain_intervals_unit")));
					maintain.put("task_work",relationList.get(j).get("task_work")+"h");
					maintain.put("last_maintenance_time",relationList.get(j).get("last_maintenance_time"));
					list.add(maintain);
				}
			}
			relation.get(i).put("maintainList",list);
		}

		task.put("relation",relation);
		List<Map<String, Object>> approvalRecord=webCommonService.getElcmApprovalRecord(String.valueOf(task.get("task_number")),String.valueOf(task.get("project_id")));
		Map<String,Object> result=new HashMap<>();
		result.put("taskDetail",task);
		result.put("approvalRecord",approvalRecord);
		return WebResult.success(result);
	}


	/**
	 * 开始/停止
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult startOrStop(Map<String, Object> map) {
		if (map.get("task_id")==null||String.valueOf(map.get("task_id")).equals("")||String.valueOf(map.get("is_start")).equals("")||map.get("is_start")==null){
			return WebResult.error(301);
		}
		//修改任务状态
		int is_start= Integer.parseInt(String.valueOf(map.get("is_start")));
		if(is_start==0){  //关闭
			map.put("status",5);
		}else{    //开启
			map.put("status",4);
		}
		//修改任务状态
		int result= elcmTaskDao.updateTask(map);
		if(result>0){
			return WebResult.success();
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 设备/部件
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getDeviceMaintainRelation(Map<String, Object> map) {
		if (map.get("device_ids")==null||String.valueOf(map.get("device_ids")).equals("")){
			return WebResult.success(new ArrayList<>());
		}

		List<String> list = Arrays.asList(String.valueOf(map.get("device_ids")).split(","));
		if (list.size() == 0) {
			return WebResult.error(301,"请选择设备!");
		}
		map.put("list", list);
		List<Map<String, Object>> relationList=elcmTaskDao.getDeviceMaintainRelation(map);
		List<Map<String, Object>> deviceList=new ArrayList<>();
		List<String> deviceIdList=new ArrayList<>();
		//第一次循环  拿出所有设备信息
		for(int i=0;i<relationList.size();i++){
			String device_id=String.valueOf(relationList.get(i).get("device_id"));
			if(deviceIdList.contains(device_id)){  //如果存在
				continue;
			}else{
				deviceIdList.add(device_id);
				Map<String, Object> deviceMap=new HashMap<>();
				deviceMap.put("device_id",Integer.parseInt(device_id));
				deviceMap.put("device_name",relationList.get(i).get("device_name"));
				deviceMap.put("device_number",relationList.get(i).get("device_number"));
				deviceMap.put("install_position",relationList.get(i).get("install_position"));
				deviceMap.put("device_model",relationList.get(i).get("device_model"));
				deviceList.add(deviceMap);
			}
		}

		for(int i=0;i<deviceList.size();i++){
			List<Map<String, Object>> maintainList=new ArrayList<>();
			String device_id=String.valueOf(deviceList.get(i).get("device_id"));
			String device_name=String.valueOf(deviceList.get(i).get("device_name"));
			for(int j=0;j<relationList.size();j++) {
				if(device_id.equals(String.valueOf(relationList.get(j).get("device_id")))){
					/*if(relationList.get(j).get("maintain_id")==null) {
						return null;
					}*/
					Map<String, Object> maintain=new HashMap<>();
					if(relationList.get(j).get("maintain_id")!=null){
						maintain.put("maintain_id",relationList.get(j).get("maintain_id"));
						maintain.put("maintain_parts",relationList.get(j).get("maintain_parts"));
						maintain.put("operation_content",relationList.get(j).get("operation_content"));
						maintain.put("operation_steps",relationList.get(j).get("operation_steps"));
						maintain.put("maintain_intervals",String.valueOf(relationList.get(j).get("maintain_intervals"))+String.valueOf(relationList.get(j).get("maintain_intervals_unit")));
						maintain.put("task_work",relationList.get(j).get("task_work")+"h");
						maintain.put("last_maintenance_time",relationList.get(j).get("last_maintenance_time"));
						maintainList.add(maintain);
					}
				}
			}

			if(maintainList.size()==0){
				return WebResult.error(201,"您选择的`"+device_name+"`设备目前还没有添加需保养/点检的部位,请先添加!");
			}
			deviceList.get(i).put("maintainList",maintainList);
		}

		return WebResult.success(deviceList);
	}



	//========================维保工单=====================//

	/**
	 * 工单状态
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRecordStatus(Map<String, Object> map) {
		List<Map<String, Object>> recordStatus = elcmTaskDao.getRecordStatus(map);
		return WebResult.success(recordStatus);
	}


	/**
	 * 工单列表
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRecordList(Map<String, Object> map) {
		PageUtil.setPageInfo(map);
		ArrayList<HashMap<String, Object>>  taskList=elcmTaskDao.getRecordList(map);
		ResultHandleUtil.addOrderNumber(taskList, map);  //为结果集添加序号

		int total=elcmTaskDao.getRecordListCount(map);
		Map<String, Object> result=new HashMap<>();
		result.put("recordList",taskList);
		result.put("total",total);
		return WebResult.success(result);
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
			idList.add(String.valueOf(list.get(i).get("record_id")));
		}
		map.put("list",idList);
		List<String> statusList=elcmTaskDao.getStatusByIds(map);
		int result=0;
		if (statusList.contains("3")||statusList.contains("4")||statusList.contains("5")||statusList.contains("6")||statusList.contains("7")){
			return WebResult.error(201,"只有待派单和待接单状态可以分配人员!");
		}


		for(int i=0;i<list.size();i++){
			Map<String, Object> assignMap=new HashMap<>();
			assignMap.put("user_id",list.get(i).get("user_id"));
			assignMap.put("record_id",list.get(i).get("record_id"));
			assignMap.put("original_user_id",list.get(i).get("user_id"));
			assignMap.put("status",2);  //待接单
			result=elcmTaskDao.updateRecord(assignMap);
		}

		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 分配提醒时间
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult assignRemindTime(Map<String, Object> map) {
		if (map.get("remind_time")==null||String.valueOf(map.get("remind_time")).equals("")){
			return WebResult.error(201,"请填写提醒时间!");
		}
		String remind_time=String.valueOf(map.get("remind_time"));
		Date remindDate=DateUtil.parseStrToDate(remind_time,DateUtil.DATE_FORMAT_YYYY_MM_DD);
		if (new Date().compareTo(remindDate)>0){
			return WebResult.error(201,"提醒时间不能早于当前时间!");
		}

		List<String> list = Arrays.asList(String.valueOf(map.get("record_ids")).split(","));
		map.put("list",list);
		List<String> statusList=elcmTaskDao.getStatusByIds(map);
		int result=0;
		if (statusList.contains("4")||statusList.contains("6")){
			return WebResult.error(201,"待审批和已完成的工单无法设置提醒时间!");
		}
		result=elcmTaskDao.assignTime(map);

		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}


	/**
	 * 调整包养时间
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult updateStartTime(Map<String, Object> map) {
		if (map.get("start_time")==null||String.valueOf(map.get("start_time")).equals("")){
			return WebResult.error(201,"请填写保养时间!");
		}
		String start_time=String.valueOf(map.get("start_time"));
		Date startDate=DateUtil.parseStrToDate(start_time,DateUtil.DATE_FORMAT_YYYY_MM_DD);
		if (new Date().compareTo(startDate)>0){
			return WebResult.error(201,"保养时间不能早于当前时间!");
		}

		List<String> list = Arrays.asList(String.valueOf(map.get("record_ids")).split(","));
		map.put("list",list);
		List<String> statusList=elcmTaskDao.getStatusByIds(map);
		int result=0;
		if (statusList.contains("4")||statusList.contains("6")){
			return WebResult.error(201,"待审批和已完成的工单无法调整保养时间!");
		}


		String startTime=String.valueOf(map.get("start_time"));
		Date plan_end_date=DateUtil.addDate(DateUtil.parseStrToDate(startTime,DateUtil.DATE_FORMAT_YYYY_MM_DD),0,0,1,0,0,0,0);
		String plan_end_time = DateUtil.parseDateToStr(plan_end_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);
		map.put("start_time",startTime+" 08:00:00");
		map.put("plan_end_time",plan_end_time+" 08:00:00");
		result=elcmTaskDao.assignTime(map);
		if(result>0){
			return WebResult.success(200,"保存成功");
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 工单详情
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRecordDetail(Map<String, Object> map) {
		if (map.get("record_id")==null||String.valueOf(map.get("record_id")).equals("")){
			return WebResult.error(301);
		}
		LinkedHashMap<String, Object> recordDetailData = getRecordDetailData(map);
		return WebResult.success(recordDetailData);
	}
	/**
	 * 工单详情
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getRecordByIds(Map<String, Object> map) {
		if (map.get("record_id")==null||String.valueOf(map.get("record_id")).equals("")){
			return WebResult.error(301);
		}
		String repair_id=String.valueOf(map.get("record_id"));
		List<String> idList=Arrays.asList(repair_id.split(","));  //id集合
		List<Map<String, Object>> resultList=new ArrayList<>();
		for (int i=0;i<idList.size();i++){
			Map<String, Object> idMap=new HashMap<>();
			idMap.put("record_id",idList.get(i));
			idMap.put("project_id",map.get("project_id"));
			LinkedHashMap<String, Object> result =getRecordDetailData(idMap);
			resultList.add(result);
		}
		return WebResult.success(resultList);
	}


	/**
	 * 我的工单
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getMyRecordList(Map<String, Object> map) {
		if (map.get("is_complete")==null||String.valueOf(map.get("is_complete")).equals("")){
			return WebResult.error(301);
		}
		PageUtil.setPageInfo(map);
		map.put("repair_user_id",map.get("user_id"));
		ArrayList<HashMap<String, Object>> recordList=elcmTaskDao.getMyRecordList(map);
		ResultHandleUtil.addOrderNumber(recordList, map);  //为结果集添加序号

		int user_id=Integer.parseInt(String.valueOf(map.get("user_id")));
		for(int i=0;i<recordList.size();i++){
			int record_user_id=Integer.parseInt(String.valueOf(recordList.get(i).get("user_id")));
			if(user_id==record_user_id){  //负责人是本人
				recordList.get(i).put("is_self",1);
			}else{
				recordList.get(i).put("is_self",0);
			}
		}

		int total=elcmTaskDao.getMyRecordListCount(map);
		Map<String, Object> result=new HashMap<>();
		result.put("recordList",recordList);
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
	public WebResult receiveRecord(Map<String, Object> map) {
		if (map.get("record_id")==null||String.valueOf(map.get("record_id")).equals("")){
			return WebResult.error(301);
		}
		Map<String, Object> mal=elcmTaskDao.getRecordDetail(map);
		int oldStatus=Integer.parseInt(String.valueOf(mal.get("status"))); //当期状态
		int result=0;
		if (oldStatus!=2) {  //待接单 才能接单
			return WebResult.error(201,"当前状态无法接单,请刷新!");
		}

		map.put("status",3); //状态改为处理中
		map.put("original_user_id",map.get("user_id")); //原本负责人改为当前人
		// map.remove("user_id");
		result=elcmTaskDao.updateRecord(map);
		if(result>0){  //设备状态改为
			String device_id=String.valueOf(mal.get("device_id"));
			String status=String.valueOf(webCommonService.checkDeviceStatus(device_id));
			webCommonService.updateDeviceStatus(device_id,status); //修改状态
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
	@Transactional
	@Override
	public WebResult chargeback(Map<String, Object> map) {
		if (map.get("record_id")==null||String.valueOf(map.get("record_id")).equals("")){
			return WebResult.error(301);
		}
		Map<String, Object> record=elcmTaskDao.getRecordDetail(map);
		int oldStatus=Integer.parseInt(String.valueOf(record.get("status"))); //当期状态
		int result=0;
		if (oldStatus!=2&&oldStatus!=5&&oldStatus!=3) { ///待接单 已驳回  处理中 才能退单
			return WebResult.error(201,"当前状态无法退单,请刷新!");
		}

		int user_id=Integer.parseInt(String.valueOf(record.get("user_id"))); //负责人
		int original_user_id=Integer.parseInt(String.valueOf(record.get("original_user_id"))); //原本负责人
		if(user_id==original_user_id){  //不是转派的...
			map.put("status",1); //状态改为待派单
			map.put("user_id",null);
			map.put("original_user_id",null);
			result=elcmTaskDao.chargeback(map);
		}else{  //派单  退单  会返回原本工单
			map.put("status",3); //维修状态改为处理中
			map.put("user_id",original_user_id);
			map.put("original_user_id",original_user_id);
			result=elcmTaskDao.chargeback(map);
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
		if (map.get("record_id")==null||String.valueOf(map.get("record_id")).equals("")){
			return WebResult.error(301);
		}
		if (map.get("record_user")==null||String.valueOf(map.get("record_user")).equals("")){
			return WebResult.error(201,"请选择负责人!");
		}
		if (String.valueOf(map.get("record_user")).equals(String.valueOf(map.get("user_id")))){
			return WebResult.error(201,"不能转派给自己!");
		}
		Map<String, Object> mal=elcmTaskDao.getRecordDetail(map);
		int oldStatus=Integer.parseInt(String.valueOf(mal.get("status"))); //当期状态
		int result=0;
		if (oldStatus!=3&&oldStatus!=5) {  //处理中 已驳回 才能转派
			return WebResult.error(201,"当前状态无法转派,请刷新!");
		}

		map.put("status",2); //状态改为待接单
		map.put("original_user_id",map.get("user_id")); //原本负责人
		map.put("user_id",map.get("record_user")); //负责人
		result=elcmTaskDao.updateRecord(map);
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
	 * 解决
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult complete(Map<String, Object> map) {
		if (map.get("record_id")==null||String.valueOf(map.get("record_id")).equals("")){
			return WebResult.error(301);
		}
		if (map.get("complete_describe")==null||String.valueOf(map.get("complete_describe")).equals("")){
			return WebResult.error(201,"请填写任务完成记录!");
		}
		Map<String, Object> repair=elcmTaskDao.getRecordDetail(map);  //详情
		int status=Integer.parseInt(String.valueOf(repair.get("status")));  //之前状态
		int result=0;
		if (status!=3&&status!=5) {  //处理中 已驳回 才能完成
			return WebResult.error(201,"当前状态无法完成任务,请刷新!");
		}

		map.put("status",4); //状态改为待审核
		map.put("complete_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));  //处理时间
		result=elcmTaskDao.updateRecord(map); //处理完成

		if(result>0){ //新增审批
			String apply_by=String.valueOf(map.get("user_id"));
			String odd_number=String.valueOf(repair.get("record_number"));

			int approval_id=webCommonDao.getElcmApprovalId(odd_number,"2");
			if(approval_id==0){
				String approval_name=String.valueOf(repair.get("task_name"));
				String project_id=String.valueOf(map.get("project_id"));
				int new_approval_id=webCommonService.addElcmApproval(odd_number,"2",approval_name,apply_by,project_id);
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


	public LinkedHashMap<String, Object> getRecordDetailData(Map<String, Object> map){
		//返回结果
		LinkedHashMap<String, Object> result = new LinkedHashMap<>();
		//维保内容信息
		Map<String, Object> maintenance_content = new HashMap<>();
		//维保完成信息
		Map<String, Object> maintenance_completed = new HashMap<>();

		Map<String, Object> record=elcmTaskDao.getRecordDetail(map);
		//把图片转换成数组
		if(record.get("complete_url")==null||String.valueOf(record.get("complete_url")).equals("")){
			record.put("complete_url",new ArrayList<>());
		}else{
			List<String> repair_url = Arrays.asList(String.valueOf(record.get("complete_url")).split(","));
			record.put("complete_url",repair_url);
		}

		//基础信息
		result.put("record_id",record.get("record_id"));
		result.put("record_number",record.get("record_number"));
		result.put("task_name",record.get("task_name"));
		result.put("status",record.get("status"));
		result.put("status_name",record.get("status_name"));
		result.put("user_id",record.get("user_id"));
		result.put("user_name",record.get("user_name"));
		result.put("plan_start_time",record.get("plan_start_time"));
		result.put("complete_time",record.get("complete_time"));
		//
		maintenance_content.put("device_id",record.get("device_id"));
		maintenance_content.put("device_number",record.get("device_number"));
		maintenance_content.put("device_name",record.get("device_name"));
		maintenance_content.put("device_model",record.get("device_model"));
		maintenance_content.put("install_position",record.get("install_position"));
		maintenance_content.put("operation_content",record.get("operation_content"));
		maintenance_content.put("maintain_intervals",record.get("maintain_intervals"));
		maintenance_content.put("task_work",record.get("task_work"));


		if(record.get("complete_describe")==null||String.valueOf(record.get("complete_describe")).equals("")){
			maintenance_completed=null;
		}else{
			//
			maintenance_completed.put("complete_describe",record.get("complete_describe"));
			maintenance_completed.put("complete_url",record.get("complete_url"));
			maintenance_completed.put("complete_time",record.get("complete_time"));
			//备件记录
			List<Map<String, Object>> sparepartsRecord=webCommonService.getSparepartsByNumber(String.valueOf(record.get("record_number")),"2",String.valueOf(map.get("project_id")));
			maintenance_completed.put("sparepartsRecord",sparepartsRecord);
		}
		//审批记录
		List<Map<String, Object>> approvalRecord=webCommonService.getElcmApprovalRecord(String.valueOf(record.get("record_number")),String.valueOf(map.get("project_id")));

		result.put("maintenance_content",maintenance_content);
		result.put("maintenance_completed",maintenance_completed);
		result.put("approvalRecord",approvalRecord);
		return result;
	}

}
