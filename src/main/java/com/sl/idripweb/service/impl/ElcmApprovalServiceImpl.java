package com.sl.idripweb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.common.entity.ElcmTaskRecord;
import com.sl.common.utils.*;
import com.sl.idripweb.dao.ElcmApprovalDao;
import com.sl.idripweb.dao.WebCommonDao;
import com.sl.idripweb.service.ElcmApprovalService;
import com.sl.idripweb.service.WebCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("elcmApprovalServiceImpl")
public class ElcmApprovalServiceImpl implements ElcmApprovalService {

	@Autowired
	private ElcmApprovalDao elcmApprovalDao;
	@Autowired
	WebCommonService webCommonService;
	@Autowired
	WebCommonDao webCommonDao;
	@Autowired
	DateUtil dateUtil;

	/**
	 * 审批分类
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getApprovalType(Map<String, Object> map) {
		ArrayList<Map<String, Object>> approvalType = elcmApprovalDao.getApprovalType(map);
		return WebResult.success(approvalType);
	}


	/**
	 * 待审批
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getPendingApprovalList(Map<String, Object> map) {
		PageUtil.setPageInfo(map);
		map.put("status","1"); //待审批
		ArrayList<HashMap<String, Object>> approvalList = elcmApprovalDao.getApprovalList(map);
		ResultHandleUtil.addOrderNumber(approvalList, map);  //为结果集添加序号
		int total = elcmApprovalDao.getApprovalListCount(map);
		Map<String, Object> result = new HashMap<>();
		result.put("approvalList", approvalList);
		result.put("total", total);
		return WebResult.success(result);
	}
	/**
	 * 已审批
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getApprovedList(Map<String, Object> map) {
		PageUtil.setPageInfo(map);
		map.put("status","2"); //已审批
		ArrayList<HashMap<String, Object>> approvalList = elcmApprovalDao.getApprovalList(map);
		ResultHandleUtil.addOrderNumber(approvalList, map);  //为结果集添加序号
		int total = elcmApprovalDao.getApprovalListCount(map);
		Map<String, Object> result = new HashMap<>();
		result.put("approvalList", approvalList);
		result.put("total", total);
		return WebResult.success(result);
	}



	/**
	 * 查看审批
	 * @param
	 * @param map
	 * @return
	 */
	@Override
	public WebResult getApproveDetail(Map<String, Object> map) {
		if (map.get("odd_number")==null||String.valueOf(map.get("odd_number")).equals("")||map.get("type_id")==null||String.valueOf(map.get("type_id")).equals("")) {
			return WebResult.error(301);
		}
		Map<String, Object> result = new HashMap<>();
		int type_id = Integer.parseInt(String.valueOf(map.get("type_id")));
		switch (type_id) {
			case 1:    //维修工单
				Map<String, Object> wxDetail = elcmApprovalDao.getRepairDetail(map); //维修详情
				if (String.valueOf(wxDetail.get("repair_url")).equals("") || wxDetail.get("repair_url") == null) {
					wxDetail.put("repair_url", new ArrayList<>());
				} else {
					List<String> repair_url = Arrays.asList(String.valueOf(wxDetail.get("repair_url")).split(","));
					wxDetail.put("repair_url", repair_url);
				}

				if (String.valueOf(wxDetail.get("mal_url")).equals("") || wxDetail.get("mal_url") == null) {
					wxDetail.put("mal_url", new ArrayList<>());
				} else {
					List<String> mal_url = Arrays.asList(String.valueOf(wxDetail.get("mal_url")).split(","));
					wxDetail.put("mal_url", mal_url);
				}

				//备件记录
				List<Map<String, Object>> sparepartsRecord = webCommonService.getSparepartsByNumber(String.valueOf(wxDetail.get("repair_number")), "1", String.valueOf(map.get("project_id")));
				wxDetail.put("sparepartsRecord", sparepartsRecord);
				result.put("repairDetail", wxDetail);
				break;
			case 2:  //维保工单
				Map<String, Object> record = elcmApprovalDao.getRecordDetail(map);
				//把图片转换成数组
				if (String.valueOf(record.get("complete_url")).equals("") || record.get("complete_url") == null) {
					record.put("complete_url", new ArrayList<>());
				} else {
					List<String> repair_url = Arrays.asList(String.valueOf(record.get("complete_url")).split(","));
					record.put("complete_url", repair_url);
				}
				//备件记录
				List<Map<String, Object>> sparepartsRecord2 = webCommonService.getSparepartsByNumber(String.valueOf(record.get("record_number")), "2", String.valueOf(map.get("project_id")));
				record.put("sparepartsRecord", sparepartsRecord2);
				//审批记录
				result.put("recordDetail", record);
				break;
			case 3:    //维保计划
				Map<String, Object> jhDetail = elcmApprovalDao.getTaskDetail(map); //维保计划详情

				List<Map<String, Object>> relationList = elcmApprovalDao.getTaskRelation(jhDetail);
				List<Map<String, Object>> relation = new ArrayList<>();
				List<String> deviceList = new ArrayList<>();
				for (int i = 0; i < relationList.size(); i++) {
					String device_id = String.valueOf(relationList.get(i).get("device_id"));
					if (deviceList.contains(device_id)) {  //如果存在
						continue;
					} else {
						deviceList.add(device_id);
						Map<String, Object> relationMap = new HashMap<>();
						relationMap.put("device_id", device_id);
						relationMap.put("device_name", String.valueOf(relationList.get(i).get("device_name")));
						relationMap.put("device_number", String.valueOf(relationList.get(i).get("device_number")));
						relationMap.put("install_position", String.valueOf(relationList.get(i).get("install_position")));
						relationMap.put("device_model", String.valueOf(relationList.get(i).get("device_model")));
						relation.add(relationMap);
					}
				}

				for (int i = 0; i < relation.size(); i++) {
					List<Map<String, Object>> list = new ArrayList<>();
					String device_id = String.valueOf(relation.get(i).get("device_id"));
					for (int j = 0; j < relationList.size(); j++) {
						if (device_id.equals(String.valueOf(relationList.get(j).get("device_id")))) {
							Map<String, Object> maintain = new HashMap<>();
							maintain.put("maintain_id", String.valueOf(relationList.get(j).get("maintain_id")));
							maintain.put("maintain_parts", String.valueOf(relationList.get(j).get("maintain_parts")));
							maintain.put("operation_content", String.valueOf(relationList.get(j).get("operation_content")));
							maintain.put("operation_steps", String.valueOf(relationList.get(j).get("operation_steps")));
							maintain.put("maintain_intervals", String.valueOf(relationList.get(j).get("maintain_intervals")) + String.valueOf(relationList.get(i).get("maintain_intervals_unit")));
							maintain.put("task_work", String.valueOf(relationList.get(j).get("task_work")) + "h");
							maintain.put("last_maintenance_time", String.valueOf(relationList.get(j).get("last_maintenance_time")));
							list.add(maintain);
						}
					}
					relation.get(i).put("maintainList", list);
				}

				jhDetail.put("relation", relation);
				result.put("taskDetail", jhDetail);
				break;
			case 4:    //设备报废
				Map<String, Object> bfDetail = elcmApprovalDao.getScrapDetail(map); //报废详情
				result.put("scrapDetail", bfDetail);
				break;
			default:
				break;
		}

		List<Map<String, Object>> approvalRecord = webCommonService.getElcmApprovalRecord(String.valueOf(map.get("odd_number")), String.valueOf(map.get("project_id")));
		result.put("approvalRecord", approvalRecord);
		return WebResult.success(result);
	}




	/**
	 * 审批
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public WebResult approve(Map<String, Object> map) {
		if (map.get("status")==null||String.valueOf(map.get("status")).equals("")) {
			return WebResult.error(201,"请填写审批结果！");
		}
		if (map.get("approval_id")==null||String.valueOf(map.get("approval_id")).equals("")) {
			return WebResult.error(301);
		}
		if(String.valueOf(map.get("status")).equals("2")){  //驳回时 必填意见
			if (map.get("content")==null||String.valueOf(map.get("content")).equals("")) {
				return WebResult.error(201,"驳回时请填写审批意见！");
			}
		}
		int result = getApprovalResult(map);

		if(result==4){
			return WebResult.error(201,"该审批已经撤销！");
		}
		if(result==-1){
			return WebResult.error(201,"已经审批,无法再次审批！");
		}
		if(result==5){
			return WebResult.error(201,"当前时间已超过计划开始时间,无法审批通过!");
		}
		if(result>0){
			return WebResult.success();
		}else{
			return WebResult.error(201);
		}
	}

	/**
	 * 获取审批结果审批
	 * @param
	 * @param map
	 * @return
	 */
	@Transactional
	@Override
	public int getApprovalResult(Map<String, Object> map) {
		String approval_id = String.valueOf(map.get("approval_id"));  //审批id
		String project_id = String.valueOf(map.get("project_id"));   //项目id
		String status = String.valueOf(map.get("status"));      //操作  2已驳回 3已通过
		Map<String, Object> approval = elcmApprovalDao.getApproval(approval_id);  //审批信息
		int approval_status = Integer.parseInt(String.valueOf(approval.get("status")));  //审批目前状态 需要判断 防止重复审批
		String type_id = String.valueOf(approval.get("type_id"));   //审批类型 1维修 2维保 3计划  4报废
		String device_id="";  //设备id  需要改设备状态
		String task_id="";   //计划id
		int result=0;	//返回值结果
		if (approval_status == 4) {   //如果已取消  返回已取消
			return 4;
		}
		if (approval_status != 1) {    //如果已审批  返回已审批
			return -1;
		}


		//如果审批通过  并且是维保计划  需要判断是否过了开始时间
		if (status.equals("3") && type_id.equals("3")) {
			Map<String, Object> taskMap = new HashMap<>();
			taskMap.put("odd_number", approval.get("odd_number"));
			taskMap.put("project_id", map.get("project_id"));
			Map<String, Object> task = elcmApprovalDao.getTaskDetail(taskMap);
			Date beginTime = DateUtil.parseStrToDate(String.valueOf(task.get("begin_time")), DateUtil.DATE_FORMAT_YYYY_MM_DD);
			String today=DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD);
			Date todayDate=DateUtil.parseStrToDate(today,DateUtil.DATE_FORMAT_YYYY_MM_DD);

			if (todayDate.compareTo(beginTime)>0){  //如果当前时间>计划开始时间 已经过了开始时间
				return 5;
			}
		}

		//如果审批通过  并且是维修工单  添加维保记录
		if (status.equals("3") && type_id.equals("1")) {

			Map<String, Object> wxMap =new HashMap<>();
			wxMap.put("project_id",map.get("project_id"));
			wxMap.put("odd_number",approval.get("odd_number"));

			//新增维修 维保记录
			Map<String, Object> repair = elcmApprovalDao.getRepairDetail(wxMap); //维修详情
			Map<String, Object> record = new HashMap<>();
			record.put("maintenance_type_id",3);  //维修
			record.put("device_id", repair.get("device_id"));
			record.put("user_id", repair.get("repair_user_id"));
			record.put("desc", repair.get("repair_describe"));
			record.put("complete_url", repair.get("repair_url"));
			record.put("odd_number", repair.get("repair_number"));
			webCommonDao.insertMaintenanceRecord(record);

			//修改设备状态
			device_id=String.valueOf(repair.get("device_id"));
		}

		if (status.equals("3") && type_id.equals("2")) {   //如果审批通过  并且是维保工单
			//新增维修 维保记录
			Map<String, Object> wbMap =new HashMap<>();
			wbMap.put("project_id",map.get("project_id"));
			wbMap.put("odd_number",approval.get("odd_number"));
			Map<String, Object> recordDetail = elcmApprovalDao.getRecordDetail(wbMap); //维保详情
			Map<String, Object> record = new HashMap<>();
			record.put("maintenance_type_id",recordDetail.get("maintenance_type_id"));
			record.put("device_id", recordDetail.get("device_id"));
			record.put("user_id", recordDetail.get("user_id"));
			record.put("desc", recordDetail.get("complete_describe"));
			record.put("complete_url", recordDetail.get("complete_url"));
			record.put("odd_number", recordDetail.get("record_number"));
			webCommonDao.insertMaintenanceRecord(record);
			//修改设备状态
			device_id=String.valueOf(recordDetail.get("device_id"));
			//
			task_id=String.valueOf(recordDetail.get("task_id"));
		}

		if (status.equals("3") && type_id.equals("4")) {   //如果审批通过  并且是报废单
			Map<String, Object> scrapMap =new HashMap<>();
			scrapMap.put("project_id",map.get("project_id"));
			scrapMap.put("odd_number",approval.get("odd_number"));
			Map<String, Object> scrap= elcmApprovalDao.getScrapDetail(scrapMap);
			//修改设备状态
			device_id=String.valueOf(scrap.get("device_id"));
			webCommonService.updateDeviceStatus(device_id,"5"); //修改状态为报废
		}

		//修改审批状态
		result = webCommonService.updateApproveStatus(approval_id, status);
		if (result > 0) {
			//修改相关的 报修/报废等状态
			webCommonService.updateElcmStatus(type_id, String.valueOf(approval.get("odd_number")), status, project_id);
			if (type_id.equals("1")||type_id.equals("2")){
				//两种工单的情况   修改设备状态
				String device_status=String.valueOf(webCommonService.checkDeviceStatus(device_id));
				webCommonService.updateDeviceStatus(device_id,device_status);
			}
			//新增审批记录
			String user_id = String.valueOf(map.get("user_id"));
			String content = map.get("content") == null ? "" : String.valueOf(map.get("content"));
			String status_name = Integer.parseInt(status) == 2 ? "已驳回" : "已通过";
			webCommonService.addElcmApprovalRecord(approval_id, user_id, content, status_name);

			//如果审批通过  并且是维保计划  //插入当前时间 到 一个月之后的所有计划工单  task-taskRecord
			if (status.equals("3") && type_id.equals("3")) {
				insertAllElcmTaskRecord(String.valueOf(approval.get("odd_number")),String.valueOf(map.get("project_id")));
			}
			//如果审批通过  并且是维保工单  //判断是否最后一个工单  添加结束时间  并且把状态改为已完成
			if (status.equals("3") && type_id.equals("2")) {
				addTaskActualEndTime(task_id);
			}
		}
		return result;
	}


	/**
	 * 插入所有工单
	 * @param odd_number
	 * @param project_id
	 */
	public void insertAllElcmTaskRecord(String odd_number,String project_id){
		//一个月之后
		Date month_end_date=DateUtil.addDate(new Date(),0,1,0,0,0,0,0);
		String month_end_time = DateUtil.parseDateToStr(month_end_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);
		Map<String,Object> taskMap=new HashMap<>();
		taskMap.put("odd_number",odd_number);
		taskMap.put("project_id",project_id);
		Map<String,Object>  task=elcmApprovalDao.getTaskDetail(taskMap); //任务
		List<ElcmTaskRecord> relationList= elcmApprovalDao.getElcmTaskRelation(taskMap);  //关联关系
		//插入数据
		List<Map<String, Object>> insertList = new ArrayList<>();
		String begin_time = String.valueOf(task.get("begin_time"));
		String end_time = String.valueOf(task.get("end_time"));
		Date beginDate = DateUtil.parseStrToDate(begin_time, DateUtil.DATE_FORMAT_YYYY_MM_DD); //开始时间
		Date endDate = DateUtil.parseStrToDate(end_time, DateUtil.DATE_FORMAT_YYYY_MM_DD); //结束时间
		//计划结束时间
		Date begin_next_date=DateUtil.addDate(beginDate,0,0,1,0,0,0,0);
		String begin_next_time = DateUtil.parseDateToStr(begin_next_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);  //明天

		//年年月月日日  工单前缀
		String oddNumber = "WB" + DateUtil.parseDateToStr(new Date(), dateUtil.DATE_FORMAT_YYMMDD);
		//工单后缀
		String oddNumberSuffix = webCommonDao.elcmTaskRecordNumber(oddNumber,project_id);
		int number = 1;
		if (oddNumberSuffix == null || oddNumberSuffix.equals("")) {  //证明没有
			number = 1;   //没有后缀的时候 为 001第一号
		} else {
			number = Integer.parseInt(oddNumberSuffix)+1; //有后缀  下一位
		}

		for (int i=0;i<relationList.size();i++){
			ElcmTaskRecord relation = relationList.get(i);
			//如果不重复并且没有记录  只插入一条记录
			if(relation.getIsRepeat()==0){
				Map<String, Object> insertMap = new HashMap<>();
				insertMap.put("plan_start_time", begin_time + " 08:00:00");  //开始时间为计划开始时间
				insertMap.put("plan_end_time", begin_next_time + " 08:00:00");
				insertMap.put("original_plan_start_time", begin_time + " 08:00:00");
				insertMap.put("status", 1);
				insertMap.put("project_id", project_id);
				insertMap.put("relation_id", relation.getRelationId());
				insertMap.put("record_number", oddNumber + String.format("%03d", number));
				insertList.add(insertMap);
				number++; //单号增加一位
			}
			//如果重复  插入今天--一个月之间的所有记录
			if(relation.getIsRepeat()==1){
				//开始时间 在  今天--一个月之间
				if(dateUtil.isEffectiveDate(beginDate,new Date(),month_end_date,DateUtil.DATE_FORMAT_YYYY_MM_DD)){


					String unit=relation.getMaintainIntervalsUnit(); //周期单位
					int cycle=relation.getMaintainIntervals(); //周期
					List<String> timeList=new ArrayList<>();  //所有工单日期
					//结束时间 在  今天--一个月之间
					if (dateUtil.isEffectiveDate(endDate,new Date(),month_end_date,DateUtil.DATE_FORMAT_YYYY_MM_DD)){
						//生成开始时间--结束时间内 所有的工单
						switch (unit) {
							case "日":
								timeList=dateUtil.getDayListOfDate(begin_time,end_time,0,0,cycle,0,0,0,0);
								break;
							case "周":
								timeList=dateUtil.getDayListOfDate(begin_time,end_time,0,0,cycle*7,0,0,0,0);
								break;
							case "月":
								timeList=dateUtil.getDayListOfDate(begin_time,end_time,0,cycle,0,0,0,0,0);
								break;
							case "年":
								timeList=dateUtil.getDayListOfDate(begin_time,end_time,cycle,0,0,0,0,0,0);
								break;
						}
					}else{  //结束时间在  一个月之外
						//生成开始时间--一个月时间内 所有的工单
						switch (unit) {
							case "日":
								timeList=dateUtil.getDayListOfDate(begin_time,month_end_time,0,0,cycle,0,0,0,0);
								break;
							case "周":
								timeList=dateUtil.getDayListOfDate(begin_time,month_end_time,0,0,cycle*7,0,0,0,0);
								break;
							case "月":
								timeList=dateUtil.getDayListOfDate(begin_time,month_end_time,0,cycle,0,0,0,0,0);
								break;
							case "年":
								timeList=dateUtil.getDayListOfDate(begin_time,month_end_time,cycle,0,0,0,0,0,0);
								break;
						}
					}
					for (int t=0;t<timeList.size();t++){
						Map<String, Object> insertMap = new HashMap<>();
						String begin_time2=timeList.get(t);
						Date beginDate2 = DateUtil.parseStrToDate(begin_time, DateUtil.DATE_FORMAT_YYYY_MM_DD); //开始时间
						//计划结束时间
						Date begin_next_date2=DateUtil.addDate(beginDate2,0,0,1,0,0,0,0);
						String begin_next_time2 = DateUtil.parseDateToStr(begin_next_date2, DateUtil.DATE_FORMAT_YYYY_MM_DD);  //明天
						insertMap.put("plan_start_time", begin_time2 + " 08:00:00");  //开始时间为计划开始时间
						insertMap.put("plan_end_time", begin_next_time2 + " 08:00:00");
						insertMap.put("original_plan_start_time", begin_time2 + " 08:00:00");
						insertMap.put("status", 1);
						insertMap.put("project_id", project_id);
						insertMap.put("relation_id", relation.getRelationId());
						insertMap.put("record_number", oddNumber + String.format("%03d", number));
						insertList.add(insertMap);
						number++; //单号增加一位
					}
				}
			}
		}
		//插入工单
		if(insertList.size()>0){
			elcmApprovalDao.insertAllElcmTaskRecord(insertList); //插入工单
		}

	}


	/**
	 *  添加结束时间  并且把状态改为已完成
	 */
	public void addTaskActualEndTime(String task_id){
		//判断是否是最后一个维保工单 如果是 新增维保计划结束时间
		int count=0;
		List<ElcmTaskRecord> taskList = elcmApprovalDao.getElcmTaskRelationById(task_id);  //获取所有关系列表
		for (int j = 0; j < taskList.size(); j++) {  //遍历结果集
			ElcmTaskRecord task = taskList.get(j);
			Date endTime = DateUtil.parseStrToDate(task.getEndTime(), DateUtil.DATE_FORMAT_YYYY_MM_DD); //结束时间
			//如果重复并且没有记录 证明审批通过时 任务工单还没开始生成时
			if (task.getIsRepeat() == 1 && task.getPlanStartTime() == null) {
				count++; //个数增加一位
			}
			//如果重复并且有记录 需要判断是否需要插入下一条
			if (task.getIsRepeat() == 1 && task.getPlanStartTime() != null) {
				//上一次计划维保时间
				Date planStartTime = DateUtil.parseStrToDate(task.getPlanStartTime(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
				String unit=task.getMaintainIntervalsUnit(); //周期单位
				int cycle=task.getMaintainIntervals(); //周期
				Date nextTime=new Date();  //下一次维保日期
				switch (unit) {
					case "日":
						nextTime=DateUtil.addDate(new Date(),0,0,cycle,0,0,0,0);
						break;
					case "周":
						nextTime=DateUtil.addDate(new Date(),0,0,cycle*7,0,0,0,0);
						break;
					case "月":
						nextTime=DateUtil.addDate(new Date(),0,cycle,0,0,0,0,0);
						break;
					case "年":
						nextTime=DateUtil.addDate(new Date(),cycle,0,0,0,0,0,0);
						break;
				}
				//一次计划维保时间小于结束时间
				if(endTime.compareTo(nextTime)>=0){
					count++; //单号增加一位
				}
			}
		}
		if (count==0){      //证明没有下一次任务  并且所有任务全部都完成
			int recordCount=elcmApprovalDao.getAllTaskRecordCountByTaskId(task_id);
			if (recordCount==0){
				elcmApprovalDao.addTaskActualEndTime(task_id);
			}
		}
	}




}
