package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface ElcmDeviceTaskDao {

    //获取即将开始任务数量
    int getAboutToStartCount(Map<String, Object> map);

    //根据任务状态获取任务列表数据
    ArrayList<HashMap<String, Object>> getTaskList(Map<String, Object> map);
    //根据任务状态获取任务列表数据->count
    int getTaskListCount(Map<String, Object> map);
    //我的任务
    ArrayList<HashMap<String, Object>> getMyTaskRecordList(Map<String, Object> map);
    int getMyTaskRecordListCount(Map<String, Object> map);
    int getMyTaskRecordListTotal(Map<String, Object> map);

    //任务状态--类型
    ArrayList<Map<String, Object>> getTaskRecordStatus(Map<String, Object> map);
    ArrayList<Map<String, Object>> getTaskRecordType(Map<String, Object> map);


    //根据任务id获取单个任务
    Map<String, Object> getTaskById(@Param("id") String id);

    Map<String, Object> getRepair(@Param("id") String id);
    Map<String, Object> getRepairByNumber(@Param("number") String number, @Param("project_id") String project_id);
    Map<String, Object> getTaskByNumber(@Param("number") String number, @Param("project_id") String project_id);

     int  updateRepair(Map<String, Object> map);;
     int  updateTaskRecord(Map<String, Object> map);
    int  chargebackRepair(Map<String, Object> map);
    int  chargebackTaskRecord(Map<String, Object> map);

    //任务完成
    int completeTask(Map<String, Object> map);

    //添加维保记录
    int insertMaintenanceRecord(Map<String, Object> map);

    //获取逾期任务数量
    int getOverdueTaskCount(Map<String, Object> map);

    //获取全部未完成任务数量
    int getUnFinishedTaskCount(Map<String, Object> map);


    //备件列表
    int getSparepartListCount(Map<String, Object> map);

    ArrayList<Map<String, Object>> getSparepartList(Map<String, Object> map);

    int getSparepartsApplyCount(Map<String, Object> map);

    ArrayList<Map<String, Object>> getSparepartApplyList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getSparepartApplyDetail(Map<String, Object> map);
    //备件申请添加
    int addSeparepartsApply(Map<String, Object> map);
    //备件申请备件列表批量添加
    int addSeparepartsApplyList(Map<String, Object> map);

    //新增备件使用
    int addSparepartsUseList(Map<String, Object> map);
    //备件使用详情
    ArrayList<Map<String, Object>> getSparepartUseDetail(@Param("oddNumber") String oddNumber,
                                                         @Param("type_id") String type_id,
                                                         @Param("project_id") String project_id);
    //备件使用详情
    ArrayList<Map<String, Object>> getSparepartsUseChoice(@Param("oddNumber") String oddNumber,
                                                          @Param("type_id") String type_id,
                                                          @Param("project_id") String project_id);


    Map<String, Object> getApproval(Map<String, Object> map);

}
