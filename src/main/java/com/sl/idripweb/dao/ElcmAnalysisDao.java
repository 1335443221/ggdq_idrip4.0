package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface ElcmAnalysisDao {
    //===故障分析===//

    //故障次数
    int getMalCount(Map<String, Object> map);
    //平均维修时间
    double getMalRepairAverageTime(Map<String, Object> map);

    List<Map<String, Object>> getMalRepairStatusCount(Map<String, Object> map);
    //高中低 故障程度集合
    ArrayList<Map<String, Object>> getMalUrgencyCount(Map<String, Object> map);

    //设备/类型名称 x轴
    List<Map<String, Object>> getDeviceMalCount(Map<String, Object> map);
    List<Map<String, Object>> getDeviceTypeMalCount(Map<String, Object> map);



    List<Map<String, Object>> getMalMonthCount(Map<String, Object> map);
    List<Map<String, Object>> getMalYearCount(Map<String, Object> map);
    List<Map<String, Object>> getMalWeekCount(Map<String, Object> map);


}
