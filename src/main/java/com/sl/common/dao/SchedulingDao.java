package com.sl.common.dao;

import com.sl.common.entity.ElcmTaskRecord;
import com.sl.common.entity.SjfFpgProjectSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface SchedulingDao {

    //维保计划 获取最后一个单号的末尾三位数
    String elcmTaskRecordNumber(@Param("number") String number,
                                @Param("project_id") String project_id);
    public List<ElcmTaskRecord> getElcmTaskRelation(@Param("project_id") String project_id);
    public int insertAllElcmTaskRecord(List<Map<String, Object>> list);

    //APP报警推送
    ArrayList<Map<String, Object>> getLogsByIsPush(@Param("project_id") String project_id);
    //APP报警推送
    int setLogsPushed(@Param("id")String id);

    //收缴费!!!!
    ArrayList<Map<String, Object>> getElecSettingFutureByProjectId(Map<String,Object> map);
    int updateAllElecSetting(Map<String,Object> map);
    ArrayList<Map<String, Object>> getLadderFutureByProjectId(Map<String,Object> map);
    int updateAllLadder(Map<String,Object> map);
    ArrayList<SjfFpgProjectSet> getFpgFutureByProjectId(Map<String,Object> map);
    int updateAllFpg(SjfFpgProjectSet map);
    ArrayList<Map<String, Object>> getFpgProjectSet(Map<String, Object> map);
    int addFpgProjectSet(Map<String, Object> map);
    int updateFpgProjectSet(Map<String, Object> map);
    ArrayList<Map<String, Object>> getMeterDataByProject(Map<String,Object> map);

    //火灾
    List<Map<String, Object>> getAllFirePatrolList(Map<String, Object> map);
    int addAllFirePatrolLog(List<Map<String, Object>> list);

}
