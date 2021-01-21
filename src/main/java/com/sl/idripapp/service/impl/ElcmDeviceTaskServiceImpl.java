package com.sl.idripapp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sl.common.utils.AppResult;
import com.sl.common.utils.DateUtil;
import com.sl.idripapp.dao.ElcmDeviceTaskDao;
import com.sl.idripapp.dao.ElcmMalfunctionDao;
import com.sl.idripapp.service.ElcmDeviceTaskService;
import com.sl.common.utils.JwtToken;
import com.sl.idripweb.dao.WebCommonDao;
import com.sl.idripweb.service.ElcmApprovalService;
import com.sl.idripweb.service.WebCommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("elcmDeviceTaskServiceImpl")
public class ElcmDeviceTaskServiceImpl implements ElcmDeviceTaskService {

    @Autowired
    private ElcmDeviceTaskDao deviceTaskDao;
    @Autowired
    private ElcmMalfunctionDao elcmMalfunctionDao;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private WebCommonService webCommonService;
    @Autowired
    private WebCommonDao webCommonDao;
    @Autowired
    private ElcmApprovalService elcmApprovalService;

    //获取即将开始任务数量
    @Override
    public AppResult getAboutToStartCount(Map<String, Object> map) {
        map.put("device_role", JwtToken.getDeviceRole(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getUserId(String.valueOf(map.get("token"))));
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        Map<String, Object> result = new HashMap<>();
        //获取即将开始任务数量
        // int aboutToStartCount = deviceTaskDao.getAboutToStartCount(map);
        result.put("aboutToStartCount", 0);
        //获取未处理故障数量
        int malfunctionCount= elcmMalfunctionDao.getNoDealMalfunctionCount(map);
        result.put("malfunctionCount", malfunctionCount);
        //获取逾期未完成任务数量
        // int overdueTaskCount = deviceTaskDao.getOverdueTaskCount(map);
        result.put("overdueTaskCount", 0);
        //获取全部未完成任务数量
        int unFinishedTaskCount = deviceTaskDao.getUnFinishedTaskCount(map);
        result.put("unFinishedTaskCount", unFinishedTaskCount);
        return AppResult.success(result);
    }

    //根据任务状态获取任务列表数据
    @Override
    public AppResult getTaskList(Map<String, Object> map) {
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        Integer fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);
        map.put("device_role",JwtToken.getDeviceRole(String.valueOf(map.get("token"))));
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        Map<String, Object> result = new HashMap<>();
        Integer pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
        int total = deviceTaskDao.getTaskListCount(map);
        if((fromNum+pageSize) >= total){
            result.put("is_LastPage",true);
        }else{
            result.put("is_LastPage",false);
        }
        ArrayList<HashMap<String, Object>> taskList = deviceTaskDao.getTaskList(map);
        for(int i=0;i<taskList.size();i++){
            //处理时间
            transferDate(taskList.get(i));
        }
        result.put("devices_data", taskList);
        return AppResult.success(result);
    }

    //我的任务
    @Override
    public AppResult getMyTaskRecordList(Map<String, Object> map) {
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        Integer fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);
        map.put("device_role",JwtToken.getDeviceRole(String.valueOf(map.get("token"))));
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        Map<String, Object> result = new HashMap<>();
        Integer pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
        int total = deviceTaskDao.getMyTaskRecordListCount(map);

        if((fromNum+pageSize) >= total){
            result.put("is_LastPage",true);
        }else{
            result.put("is_LastPage",false);
        }
        ArrayList<HashMap<String, Object>> taskList = deviceTaskDao.getMyTaskRecordList(map);
        for(int i=0;i<taskList.size();i++){
            //处理时间
            transferDate(taskList.get(i));
        }
        int count = deviceTaskDao.getMyTaskRecordListTotal(map);
        result.put("total", count);
        result.put("devices_data", taskList);
        return AppResult.success(result);
    }

    //任务状态
    @Override
    public AppResult getTaskRecordStatus(Map<String, Object> map) {
        ArrayList<Map<String, Object>> taskRecordStatus = deviceTaskDao.getTaskRecordStatus(map);
        return AppResult.success(taskRecordStatus);
    }

    //任务类型
    @Override
    public AppResult getTaskRecordType(Map<String, Object> map) {
        ArrayList<Map<String, Object>> taskRecordType = deviceTaskDao.getTaskRecordType(map);
        return AppResult.success(taskRecordType);
    }


    /**
     * 根据任务id获取单个任务
     */
    @Override
    public AppResult getTaskById(Map<String, Object> map) {
        if (map.get("id")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id=Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        String id=String.valueOf(map.get("id"));
        //返回结果
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        //取消原因
        Map<String, Object> maintenance_base_info = new HashMap<>();
        //维修 信息
        Map<String, Object> maintenance_repair_content = new HashMap<>();
        //维保 信息
        Map<String, Object> maintenance_task_content = new HashMap<>();
        //维护/维保 完成信息
        Map<String, Object> maintenance_completed = new HashMap<>();
        //审批记录
        List<Map<String, Object>> approvalRecord=new ArrayList<>();

        if (maintenance_type_id==3){
            //维修信息
            Map<String, Object> repair = deviceTaskDao.getRepair(id);
            //维保信息为null
            maintenance_task_content=null;


            //基础信息
            result.put("id",repair.get("id"));
            result.put("status",repair.get("status"));
            result.put("status_name",repair.get("status_name"));
            result.put("number",repair.get("repair_number"));
            result.put("maintenance_type_id",repair.get("maintenance_type_id"));
            result.put("maintenance_type_name",repair.get("maintenance_type_name"));
            result.put("user_id",repair.get("repair_user_id"));
            //设备信息
            result.put("device_model",repair.get("device_model"));
            result.put("device_name",repair.get("device_name"));
            result.put("device_number",repair.get("device_number"));
            result.put("install_position",repair.get("install_position"));


            if(repair.get("cancel_user")==null||String.valueOf(repair.get("cancel_user")).equals("")){
                maintenance_base_info=null;
            }else{
                //取消信息
                maintenance_base_info.put("cancel_user",repair.get("cancel_user"));
                maintenance_base_info.put("cancel_reason",repair.get("cancel_reason"));
                maintenance_base_info.put("cancel_time",repair.get("cancel_time"));
            }


            //维修信息
            maintenance_repair_content.put("mal_describe",repair.get("mal_describe"));
            maintenance_repair_content.put("mal_url",repair.get("mal_url"));
            maintenance_repair_content.put("urgency",repair.get("urgency"));
            maintenance_repair_content.put("report_user",repair.get("report_user"));
            maintenance_repair_content.put("report_time",repair.get("report_time"));
            maintenance_repair_content.put("is_repair",repair.get("is_repair"));
            maintenance_repair_content.put("malfunction_number",repair.get("malfunction_number"));
            if(maintenance_repair_content.get("mal_url") == null || StringUtils.isEmpty(String.valueOf(maintenance_repair_content.get("mal_url")))){
                maintenance_repair_content.put("mal_url", new ArrayList<>());
            }else{
                maintenance_repair_content.put("mal_url", Arrays.asList(String.valueOf(maintenance_repair_content.get("mal_url")).split(",")));
            }

            //完成信息
            if(repair.get("completed_describe")==null||String.valueOf(repair.get("completed_describe")).equals("")){
                maintenance_completed=null;
            }else{
                maintenance_completed.put("completed_describe",repair.get("repair_describe"));
                maintenance_completed.put("completed_user",repair.get("repair_user"));
                maintenance_completed.put("completed_time",repair.get("repair_time"));
                maintenance_completed.put("completed_url",repair.get("repair_url"));
                if(maintenance_completed.get("completed_url") == null || StringUtils.isEmpty(String.valueOf(maintenance_completed.get("completed_url")))){
                    maintenance_completed.put("completed_url", new ArrayList<>());
                }else{
                    maintenance_completed.put("completed_url", Arrays.asList(String.valueOf(maintenance_completed.get("completed_url")).split(",")));
                }
            }


            //审批记录
            approvalRecord=webCommonService.getElcmApprovalRecord(String.valueOf(repair.get("repair_number")),String.valueOf(map.get("project_id")));
        }else{

            //维保信息
            Map<String, Object> record =  deviceTaskDao.getTaskById(id);
            //取消信息
            maintenance_base_info=null;
            //维修信息为null
            maintenance_repair_content=null;
            //基础信息
            result.put("id",record.get("id"));
            result.put("status",record.get("status"));
            result.put("status_name",record.get("status_name"));
            result.put("number",record.get("record_number"));
            result.put("maintenance_type_id",record.get("maintenance_type_id"));
            result.put("maintenance_type_name",record.get("maintenance_type_name"));
            result.put("user_id",record.get("user_id"));
            //设备信息
            result.put("device_model",record.get("device_model"));
            result.put("device_name",record.get("device_name"));
            result.put("device_number",record.get("device_number"));
            result.put("install_position",record.get("install_position"));
            //维保信息
            maintenance_task_content.put("maintain_parts",record.get("maintain_parts"));
            maintenance_task_content.put("operation_content",record.get("operation_content"));
            maintenance_task_content.put("operation_steps",record.get("operation_steps"));
            maintenance_task_content.put("maintain_intervals",record.get("maintain_intervals"));
            maintenance_task_content.put("task_work",record.get("task_work"));
            maintenance_task_content.put("last_maintenance_time",record.get("last_maintenance_time"));
            maintenance_task_content.put("plan_start_time",record.get("plan_start_time"));
            maintenance_task_content.put("plan_end_time",record.get("plan_end_time"));
            maintenance_task_content.put("execute_user",record.get("user_name"));
            //处理时间
            transferDate(maintenance_task_content);

            //完成信息
            if(record.get("complete_time")==null||String.valueOf(record.get("complete_time")).equals("")){
                maintenance_completed=null;
            }else{
                //完成信息
                maintenance_completed.put("completed_describe",record.get("complete_describe"));
                maintenance_completed.put("completed_user",record.get("user_name"));
                maintenance_completed.put("completed_time",record.get("complete_time"));
                maintenance_completed.put("completed_url",record.get("complete_url"));
                if(maintenance_completed.get("completed_url") == null || StringUtils.isEmpty(String.valueOf(maintenance_completed.get("completed_url")))){
                    maintenance_completed.put("completed_url", new ArrayList<>());
                }else{
                    maintenance_completed.put("completed_url", Arrays.asList(String.valueOf(maintenance_completed.get("completed_url")).split(",")));
                }
            }
            //审批记录
            approvalRecord=webCommonService.getElcmApprovalRecord(String.valueOf(record.get("record_number")),String.valueOf(map.get("project_id")));
        }

        result.put("maintenance_base_info",maintenance_base_info);
        result.put("maintenance_repair_content",maintenance_repair_content);
        result.put("maintenance_task_content",maintenance_task_content);
        result.put("maintenance_completed",maintenance_completed);
        result.put("approvalRecord",approvalRecord);
        return AppResult.success(result);
    }


    /**
     * 调整保养时间
     */
    @Override
    public AppResult updateStartTime(Map<String, Object> map) {
        if (map.get("id")==null||map.get("start_time")==null){
            return AppResult.error("1003");
        }
        map.remove("token");
        int status=getTaskStatus(String.valueOf(map.get("id")),2);

        if (status==4||status==6){  //待审核/已完成 不能调整时间
            getStatusCodeMsg(status);
        }
        int result = 0;
        String startTime=String.valueOf(map.get("start_time"));
        Date plan_end_date=DateUtil.addDate(DateUtil.parseStrToDate(startTime,DateUtil.DATE_FORMAT_YYYY_MM_DD),0,0,1,0,0,0,0);
        String plan_end_time = DateUtil.parseDateToStr(plan_end_date, DateUtil.DATE_FORMAT_YYYY_MM_DD);
        map.put("start_time",startTime+" 08:00:00");
        map.put("plan_end_time",plan_end_time+" 08:00:00");
        result=deviceTaskDao.updateTaskRecord(map);
        if (result>0){
            result = -1;
        }
        return getStatusCodeMsg(result);
    }


    /**
     * 分配人员
     */
    @Transactional
    @Override
    public AppResult assignUser(Map<String, Object> map) {
        if (map.get("id")==null||map.get("execute_user_id")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        int status=getTaskStatus(String.valueOf(map.get("id")),maintenance_type_id);
        int result = 0;
        if (maintenance_type_id == 3) {
            if (status!=1) {  //待派单才能派单
                if(status==7){
                    return AppResult.error("1047");
                }
                if(status>0){
                    return AppResult.error("1040");
                }
            }
            map.put("repair_user", map.get("execute_user_id"));
            map.put("repair_original_user", map.get("execute_user_id"));
            map.put("repair_status", 2); //待接单
            String assign_time = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
            map.put("assign_time", assign_time); //分配时间
            deviceTaskDao.updateRepair(map);
            result = -1;
        } else {
            if (status!=1) {  //待派单才能派单
                if(status==7){
                    return AppResult.error("1047");
                }
                if(status>0){
                    return AppResult.error("1040");
                }
            }
            map.put("user_id", map.get("execute_user_id"));
            map.put("original_user_id", map.get("execute_user_id"));
            map.put("status", 2); //待接单
            deviceTaskDao.updateTaskRecord(map);
            result = -1;
        }
        if(result==-1){
            return AppResult.success();
        }else{
            return AppResult.error("1010");
        }
    }


    /**
     * 接单
     * @param map
     * @return
     */
    @Transactional
    @Override
    public AppResult receiveRecord(Map<String, Object> map) {
        if (map.get("id")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        int status=getTaskStatus(String.valueOf(map.get("id")),maintenance_type_id);
        if (status!=2) {  //待接单 才能接单
            return getStatusCodeMsg(status);
        }
        int result = 0;
        Map<String, Object> data=new HashMap<>();
        if (maintenance_type_id == 3) {
            data=deviceTaskDao.getRepair(String.valueOf(map.get("id")));

            map.put("repair_status",3); //维修状态改为处理中
            map.put("repair_original_user",map.get("user_id")); //原本维修人改为当前人
            map.put("repair_user",map.get("user_id")); //维修人改为当前人
            result=deviceTaskDao.updateRepair(map);
        } else {
            data=deviceTaskDao.getTaskById(String.valueOf(map.get("id")));

            map.put("status",3); //状态改为处理中
            map.put("original_user_id",map.get("user_id")); //原本负责人改为当前人
            result=deviceTaskDao.updateTaskRecord(map);
        }
        //如果操作成功  改变设备状态
        if(result>0){
            String device_id=String.valueOf(data.get("device_id"));
            String device_status=String.valueOf(webCommonService.checkDeviceStatus(device_id));
            webCommonService.updateDeviceStatus(device_id,device_status); //修改状态
            result = -1;
        }else{
            result = 0;
        }
        return getStatusCodeMsg(result);
    }

    /**
     * 退单
     * @param map
     * @return
     */
    @Transactional
    @Override
    public AppResult chargeback(Map<String, Object> map) {
        if (map.get("id")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        int status=getTaskStatus(String.valueOf(map.get("id")),maintenance_type_id);
        int result = 0;

        if (maintenance_type_id == 3) {
            if (status!=2&&status!=5&&status!=3) {  //待接单 已驳回  处理中 才能退单
                return getStatusCodeMsg(status);
            }
            Map<String, Object> repair=deviceTaskDao.getRepair(String.valueOf(map.get("id")));  //维修详情
            int repair_user=Integer.parseInt(String.valueOf(repair.get("repair_user_id"))); //维修人
            int repair_original_user=Integer.parseInt(String.valueOf(repair.get("repair_original_user"))); //原本维修人
            if(repair_user==repair_original_user){  //不是派单
                map.put("repair_status",1); //维修状态改为待派单
                map.put("assign_time",null);
                map.put("repair_user",null);
                map.put("repair_original_user",null);
            }else{  //派单  退单  会返回原本工单
                map.put("repair_status",3); //维修状态改为处理中
                map.put("assign_time",repair.get("assign_time"));
                map.put("repair_user",repair_original_user);
                map.put("repair_original_user",repair_original_user);
            }
            deviceTaskDao.chargebackRepair(map);
            result = -1;
        } else {
            if (status!=2&&status!=5&&status!=3) {  //待接单 已驳回  处理中 才能退单
                result = status;
            }
            Map<String, Object> record=deviceTaskDao.getTaskById(String.valueOf(map.get("id")));
            int user_id=Integer.parseInt(String.valueOf(record.get("user_id"))); //负责人
            int original_user_id=Integer.parseInt(String.valueOf(record.get("original_user_id"))); //原本负责人
            if(user_id==original_user_id){  //不是转派的...
                map.put("status",1); //状态改为待派单
                map.put("user_id",null);
                map.put("original_user_id",null);
            }else{  //派单  退单  会返回原本工单
                map.put("status",3); //维修状态改为处理中
                map.put("user_id",original_user_id);
                map.put("original_user_id",original_user_id);
            }
            deviceTaskDao.chargebackTaskRecord(map);
            result = -1;

        }
        return getStatusCodeMsg(result);
    }

    /**
     * 转派
     * @param map
     * @return
     */
    @Transactional
    @Override
    public AppResult transfer(Map<String, Object> map) {
        if (map.get("id")==null||map.get("record_user_id")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        int status=getTaskStatus(String.valueOf(map.get("id")),maintenance_type_id);

        if (status!=3&&status!=5) {  //处理中 已驳回 才能转派
            return getStatusCodeMsg(status);
        }
        int result =0;
        Map<String, Object> data =new HashMap<>();
        if (maintenance_type_id == 3) {
            map.put("repair_status",2); //维修状态改为待接单
            map.put("repair_original_user",map.get("user_id")); //原本维修人
            map.put("repair_user",map.get("record_user_id")); //转派人
            result= deviceTaskDao.updateRepair(map);
            data=deviceTaskDao.getRepair(String.valueOf(map.get("id")));
        } else {
            map.put("status", 2); //状态改为待接单
            map.put("original_user_id", map.get("user_id")); //原本负责人
            map.put("user_id", map.get("record_user_id")); //负责人
            result = deviceTaskDao.updateTaskRecord(map);
            data=deviceTaskDao.getTaskById(String.valueOf(map.get("id")));
        }
        if(result>0){
            String device_id = String.valueOf(data.get("device_id"));
            String device_status = String.valueOf(webCommonService.checkDeviceStatus(device_id));
            webCommonService.updateDeviceStatus(device_id, device_status); //修改状态
            return getStatusCodeMsg(-1);
        }else{
            return getStatusCodeMsg(result);
        }

    }


    /**
     * 任务完成
     * @param map
     * @return
     */
    @Transactional
    @Override
    public AppResult completeTask(Map<String, Object> map) {
        if (map.get("id")==null||map.get("complete_describe")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        if (String.valueOf(map.get("id")).equals("")||String.valueOf(map.get("complete_describe")).equals("")||String.valueOf(map.get("maintenance_type_id")).equals("")){
            return AppResult.error("1003");
        }
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        int status=getTaskStatus(String.valueOf(map.get("id")),maintenance_type_id);

        if (status!=3&&status!=5) {  //处理中 已驳回 才能完成
            return getStatusCodeMsg(status);
        }
        int result=0;
        if (maintenance_type_id == 3) {
            map.put("repair_status",4); //状态改为待审核
            // map.put("status",2); //故障状态改为已完成
            map.put("repair_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));  //处理时间
            result= deviceTaskDao.updateRepair(map);
            if(result>0){
                Map<String, Object> repair=deviceTaskDao.getRepair(String.valueOf(map.get("id")));
                //新增使用备件
                if(map.get("spareparts")!=null){
                    Map<String,Object> sparepartsMap=new HashMap<>();
                    sparepartsMap.put("apply_number",repair.get("repair_number"));
                    sparepartsMap.put("project_id",map.get("project_id"));
                    sparepartsMap.put("type_id",1);
                    String spareparts = String.valueOf(map.get("spareparts"));
                    List<Map> sparepartsList = JSONArray.parseArray(spareparts, Map.class);
                    sparepartsMap.put("sparepartsList", sparepartsList);
                    //添加备件使用信息
                    if (sparepartsList.size()>0){
                        deviceTaskDao.addSparepartsUseList(sparepartsMap);
                    }
                }
                //新增审批
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
            result = -1;
        } else {
            map.put("status",4); //状态改为待审核
            map.put("complete_time", DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS));  //处理时间
            result= deviceTaskDao.updateTaskRecord(map);
            if(result>0){
                Map<String, Object> repair=deviceTaskDao.getTaskById(String.valueOf(map.get("id")));
                //新增使用备件
                if(map.get("spareparts")!=null){
                    Map<String,Object> sparepartsMap=new HashMap<>();
                    sparepartsMap.put("apply_number",repair.get("record_number"));
                    sparepartsMap.put("project_id",map.get("project_id"));
                    sparepartsMap.put("type_id",2);
                    String spareparts = String.valueOf(map.get("spareparts"));
                    List<Map> sparepartsList = JSONArray.parseArray(spareparts, Map.class);
                    sparepartsMap.put("sparepartsList", sparepartsList);
                    //添加备件使用信息
                    if (sparepartsList.size()>0){
                        deviceTaskDao.addSparepartsUseList(sparepartsMap);
                    }
                }

                //新增审批
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
            result = -1;
        }
        return getStatusCodeMsg(result);
    }



    /**
     * 备件列表
     * @param map
     * @return
     */
    @Override
    public AppResult getSparepartList(Map<String, Object> map) {
        if (map.get("apply_number")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        if(map.get("pageSize")==null||map.get("pageSize")==""||map.get("pageNum")==""||map.get("pageNum")==null){
            map.put("pageNum",1);
            map.put("pageSize",20);
        }
        Integer fromNum=(Integer.parseInt(String.valueOf(map.get("pageNum")))-1)*Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum",fromNum);
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        Map<String, Object> result = new HashMap<>();
        Integer pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        map.put("fromNum", fromNum);
        map.put("pageSize", pageSize);
        int total = deviceTaskDao.getSparepartListCount(map);
        if((fromNum+pageSize) >= total){
            result.put("is_LastPage",true);
        }else{
            result.put("is_LastPage",false);
        }

        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        map.put("type_id",maintenance_type_id==3?1:2); //1 维修工单  2维保

        ArrayList<Map<String, Object>> sparepartList = deviceTaskDao.getSparepartList(map);
        for(int i=0;i<sparepartList.size();i++){
            if(sparepartList.get(i).get("file_url") == null || StringUtils.isEmpty(String.valueOf(sparepartList.get(i).get("file_url")))){
                sparepartList.get(i).put("file_url", new ArrayList<>());
            }else{
                sparepartList.get(i).put("file_url", Arrays.asList(String.valueOf(sparepartList.get(i).get("file_url")).split(",")));
            }
        }
        int applyCount = deviceTaskDao.getSparepartsApplyCount(map);
        result.put("sparepartsList", sparepartList);
        result.put("applyCount", applyCount);

        return AppResult.success(result);
    }

    /**
     * 备件申请列表
     * @param map
     * @return
     */
    @Override
    public AppResult getSparepartApplyList(Map<String, Object> map) {
        if (map.get("apply_number")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        if(maintenance_type_id==3){
            map.put("type_id",1); //维修工单
        }else{
            map.put("type_id",2); //维保工单
        }
        ArrayList<Map<String, Object>> sparepartApplyList = deviceTaskDao.getSparepartApplyList(map);
        return AppResult.success(sparepartApplyList);
    }
    /**
     * 备件列表
     * @param map
     * @return
     */
    @Override
    public AppResult getSparepartApplyDetail(Map<String, Object> map) {
        if (map.get("apply_id")==null){
            return AppResult.error("1003");
        }
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        ArrayList<Map<String, Object>> sparepartList = deviceTaskDao.getSparepartApplyDetail(map);
        for(int i=0;i<sparepartList.size();i++){
            if(sparepartList.get(i).get("file_url") == null || StringUtils.isEmpty(String.valueOf(sparepartList.get(i).get("file_url")))){
                sparepartList.get(i).put("file_url", new ArrayList<>());
            }else{
                sparepartList.get(i).put("file_url", Arrays.asList(String.valueOf(sparepartList.get(i).get("file_url")).split(",")));
            }
        }
        return AppResult.success(sparepartList);
    }
    /**
     * 备件使用情况-选择(新)
     * @param map
     * @return
     */
    @Override
    public AppResult getSparepartsUseChoice(Map<String, Object> map) {
        if (map.get("apply_number")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        int maintenance_type_id=Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        String type_id=maintenance_type_id==3?"1":"2";
        //备件记录
        List<Map<String, Object>> sparepartList=deviceTaskDao.getSparepartsUseChoice(String.valueOf(map.get("apply_number")),type_id,String.valueOf(map.get("project_id")));
        for(int i=0;i<sparepartList.size();i++){
            if(sparepartList.get(i).get("file_url") == null || StringUtils.isEmpty(String.valueOf(sparepartList.get(i).get("file_url")))){
                sparepartList.get(i).put("file_url", new ArrayList<>());
            }else{
                sparepartList.get(i).put("file_url", Arrays.asList(String.valueOf(sparepartList.get(i).get("file_url")).split(",")));
            }
        }
        return AppResult.success(sparepartList);
    }

    /**
     * 备件使用情况-查看/选择
     * @param map
     * @return
     */
    @Override
    public AppResult getSparepartsUseDetail(Map<String, Object> map) {
        if (map.get("apply_number")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.remove("token");
        int maintenance_type_id=Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        String type_id=maintenance_type_id==3?"1":"2";
        //备件记录
        List<Map<String, Object>> sparepartList=deviceTaskDao.getSparepartUseDetail(String.valueOf(map.get("apply_number")),type_id,String.valueOf(map.get("project_id")));
        for(int i=0;i<sparepartList.size();i++){
            if(sparepartList.get(i).get("file_url") == null || StringUtils.isEmpty(String.valueOf(sparepartList.get(i).get("file_url")))){
                sparepartList.get(i).put("file_url", new ArrayList<>());
            }else{
                sparepartList.get(i).put("file_url", Arrays.asList(String.valueOf(sparepartList.get(i).get("file_url")).split(",")));
            }
        }
        return AppResult.success(sparepartList);
    }



    /**
     * 备件申请
     * @param map
     * @return
     */
    @Transactional
    @Override
    public AppResult addSeparepartsApply(Map<String, Object> map) {
        if (map.get("apply_number")==null||map.get("spareparts")==null||map.get("maintenance_type_id")==null){
            return AppResult.error("1003");
        }
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        //类型  3是报修  1/2是维保
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        String apply_number =String.valueOf(map.get("apply_number"));
        String project_id =String.valueOf(map.get("project_id"));
        int status=0;
        if (maintenance_type_id == 3) {
            Map<String, Object> data=deviceTaskDao.getRepairByNumber(apply_number,project_id);
            status=Integer.parseInt(String.valueOf(data.get("status")));  //状态
        }else{
            Map<String, Object> data=deviceTaskDao.getTaskByNumber(apply_number,project_id);
            status=Integer.parseInt(String.valueOf(data.get("status")));  //状态
        }

        if (status!=3&&status!=5) {  //处理中 已驳回 才能申请备件
            return getStatusCodeMsg(status);
        }
        if(maintenance_type_id==3){
            map.put("type_id",1); //维修工单
        }else{
            map.put("type_id",2); //维保工单
        }
        //插入备件申请信息
        int result = deviceTaskDao.addSeparepartsApply(map);
        //备件申请信息插入成功以后的该条记录对应的apply_id
        int apply_id = Integer.parseInt(String.valueOf(map.get("apply_id")));
        //解析该入库单下的备件采购信息
        String separeparts = String.valueOf(map.get("spareparts"));
        List<Map> sparepartsList = JSONArray.parseArray(separeparts, Map.class);
        map.put("sparepartsList", sparepartsList);
        map.put("apply_id", apply_id);
        if(sparepartsList.size()>0){
            //添加备件申请信息
            deviceTaskDao.addSeparepartsApplyList(map);
        }
        return getStatusCodeMsg(-1);
    }


    /**
     * 取消任务
     * @param map
     * @return
     */
    @Transactional
    @Override
    public AppResult revoke(Map<String, Object> map) {
        if (map.get("id")==null||map.get("maintenance_type_id")==null||map.get("cancel_reason")==null){
            return AppResult.error("1003");
        }
        int maintenance_type_id=Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));
        if(maintenance_type_id!=3){  //不是维修任务  不允许撤销
            return AppResult.error("1049");
        }
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        int status=getTaskStatus(String.valueOf(map.get("id")),maintenance_type_id);
        if (status==4||status==6||status==7){
            return getStatusCodeMsg(status);
        }
        map.put("status",3); //故障状态改为已取消
        map.put("repair_status",7); //维修状态改为已取消
        map.put("cancel_user",map.get("user_id"));
        map.put("cancel_time",new Date());
        int result=deviceTaskDao.updateRepair(map);
        if(result>0){
            //修改故障状态
            Map<String, Object> mal=deviceTaskDao.getRepair(String.valueOf(map.get("id")));
            String device_id=String.valueOf(mal.get("device_id"));
            String device_status=String.valueOf(webCommonService.checkDeviceStatus(device_id));
            webCommonService.updateDeviceStatus(device_id,device_status);
            //修改审批状态
            int approval_id=webCommonDao.getElcmApprovalId(String.valueOf(mal.get("repair_number")),"1");
            if(approval_id!=0){
                String apply_by=String.valueOf(map.get("user_id"));
                webCommonService.addElcmApprovalRecord(String.valueOf(approval_id),apply_by,"撤销申请","撤销申请");
                //审批改为已撤销
                webCommonService.updateApproveStatus(String.valueOf(approval_id),"4");
            }
            result = -1;
        }else {
            result = 0;
        }
        return getStatusCodeMsg(result);
    }


    /**
     * 审批
     * @param map
     * @return
     */
    @Override
    public AppResult approve(Map<String, Object> map) {
        if (map.get("odd_number")==null||map.get("maintenance_type_id")==null||map.get("status")==null){
            return AppResult.error("1003");
        }
        if(String.valueOf(map.get("status")).equals("2")){  //驳回时 必填意见
            if (map.get("content")==null||String.valueOf(map.get("content")).equals("")) {
                AppResult.error("1003");
            }
        }
        map.put("user_id",JwtToken.getProjectUserId(String.valueOf(map.get("token"))));
        map.put("project_id",JwtToken.getProjectId(String.valueOf(map.get("token"))));
        map.remove("token");
        int maintenance_type_id = Integer.parseInt(String.valueOf(map.get("maintenance_type_id")));   //类型  3报修  1/2 维保
        int type_id=maintenance_type_id==3?1:2;
        map.put("type_id",type_id);
        Map<String, Object> approval =  deviceTaskDao.getApproval(map);
        int status=Integer.parseInt(String.valueOf(approval.get("status")));
        if (status!=1){
            return AppResult.error("1048");
        }
        map.put("approval_id",String.valueOf(approval.get("approval_id")));
        int result = elcmApprovalService.getApprovalResult(map);
        if(result>0){
            return AppResult.success();
        }else{
            return AppResult.error("1010");
        }
    }



    /**
     * 获取错误码
     * @param
     * @return
     */
    public AppResult getStatusCodeMsg(int status) {
        switch (status){
            case -1:   //正确
                return AppResult.success();
            case 0:   //失败
                return AppResult.error("1010");
            case 1:   //失败
                return AppResult.error("1041");
            case 2:   //待接单
                return AppResult.error("1042");
            case 3:   //处理中
                return AppResult.error("1043");
            case 4:   //待审核
                return AppResult.error("1044");
            case 5:   //已驳回
                return AppResult.error("1045");
            case 6:   //已完成
                return AppResult.error("1046");
            case 7:   //已撤销
                return AppResult.error("1047");
            default:
                return AppResult.success();
        }
    }


    public int getTaskStatus(String id,int maintenance_type_id){
        if (maintenance_type_id == 3) {
            Map<String, Object> data=deviceTaskDao.getRepair(String.valueOf(id));
            return Integer.parseInt(String.valueOf(data.get("status")));  //状态
        }else{
            Map<String, Object> data=deviceTaskDao.getTaskById(String.valueOf(id));
            return Integer.parseInt(String.valueOf(data.get("status")));  //状态
        }
    }







    //处理时间
    public void transferDate(Map<String, Object> taskById){
        //获取当前时间的年份
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        //年份判断，如果是当年就去除字段中的年份展示
        int plan_start_time_year = taskById.get("plan_start_time") != null ? Integer.parseInt(String.valueOf(taskById.get("plan_start_time")).substring(0,4)) : 0;//plan_start_time
        int plan_end_time_year = taskById.get("plan_end_time") != null ? Integer.parseInt(String.valueOf(taskById.get("plan_end_time")).substring(0,4)) : 0;//plan_start_time
        int complete_time_year = taskById.get("complete_time") != null ? Integer.parseInt(String.valueOf(taskById.get("complete_time")).substring(0,4)) : 0;//plan_start_time
        if(nowYear == plan_start_time_year)
            taskById.put("plan_start_time", String.valueOf(taskById.get("plan_start_time")).substring(5));
        if(nowYear == plan_end_time_year)
            taskById.put("plan_end_time", String.valueOf(taskById.get("plan_end_time")).substring(5));
        if(nowYear == complete_time_year)
            taskById.put("complete_time", String.valueOf(taskById.get("complete_time")).substring(5));
    }
}
