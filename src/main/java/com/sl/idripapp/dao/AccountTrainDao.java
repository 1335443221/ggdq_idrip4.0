package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountTrainDao {

    //获取培训信息
    List<Map<String, Object>> getTrainList(Map<String, Object> map);

    //获取培训信息->count
    int getTrainListCount(Map<String, Object> map);

    //获取培训信息
    List<Map<String, Object>> getTrainSheetFiles(Map<String, Object> map);

    //获取参与人员
    List<Map<String, Object>> getAttendUser(Map<String, Object> map);

    //获取参与人员-count
    int getAttendUserCount(Map<String, Object> map);

    //获取参与人员详情
    List<Map<String, Object>> getAttendUserDetail(Map<String, Object> map);

}
