package com.sl.idripweb.dao;

import com.sl.common.entity.ElcmTaskRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ElcmApprovalDao {

    ArrayList<Map<String, Object>> getApprovalType(Map<String, Object> map);
    ArrayList<HashMap<String, Object>> getApprovalList(Map<String, Object> map);
    int getApprovalListCount(Map<String, Object> map);
    Map<String, Object> getScrapDetail(Map<String, Object> map);
    Map<String, Object> getTaskDetail(Map<String, Object> map);
    Map<String, Object> getRecordDetail(Map<String, Object> map);
    List<ElcmTaskRecord> getElcmTaskRelationById(@Param("task_id") String task_id);
    int getAllTaskRecordCountByTaskId(@Param("task_id") String task_id);


    List<Map<String, Object>> getTaskRelation(Map<String, Object> map);
    Map<String, Object> getRepairDetail(Map<String, Object> map);

    public int addTaskActualEndTime(@Param("task_id") String task_id);


    Map<String, Object> getApproval(@Param("approval_id") String approval_id);
    int updateApproveStatus(Map<String, Object> map);



    List<ElcmTaskRecord> getElcmTaskRelation(Map<String, Object> map);
    public int insertAllElcmTaskRecord(List<Map<String, Object>> list);
}
