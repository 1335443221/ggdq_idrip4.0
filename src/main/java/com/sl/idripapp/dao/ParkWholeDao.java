package com.sl.idripapp.dao;

import com.sl.idripapp.entity.TaskPark;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: ParkWholeDao
 * Description: 园区整体
 */

@Mapper
public interface ParkWholeDao {
    int getDeviceInfoCount(Map<String, Object> map);

    List<Map<String, Object>> getParkYearEnergy(Map<String, Object> map);


    List<Map<String, Object>> getAllDeviceTaskRecord(Map<String, Object> map);
    List<Map<String, Object>> getAllDeviceRepair(Map<String, Object> map);
    List<Map<String, Object>> getAllFirePatrolLog(Map<String, Object> map);

    //平均维修时间
    double getMalRepairAverageTime(Map<String, Object> map);

    //意见反馈
    int addOpinion(Map<String, Object> map);

    List<Map<String, Object>> getTaskType(Map<String, Object> map);
    List<Map<String, Object>> getTaskStatus(Map<String, Object> map);

    List<TaskPark> getMyDeviceTask(Map<String, Object> map);
    List<TaskPark> getMyFireTask(Map<String, Object> map);


}
