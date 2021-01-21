package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportDao {
    void insertTemperatureBatch(List<Map<String, Object>> list);
    void insertPressureBatch(List<Map<String, Object>> list);
    void insertWaterBatch(List<Map<String, Object>> list);
    void insertWaterPumpRunningBatch(List<Map<String, Object>> list);
    void insertRunningDataBatch(Map<String, Object> list);
    //void insertRunningDataBatch(List<Map<String,Object>> list);
    List<Map<String,Object>> selectTemperatureByDate(@Param("dateList") List<String> list);
    List<Map<String,Object>> selectPressureByDate(@Param("dateList") List<String> list);
    List<Map<String,Object>> selectWaterByDate(@Param("dateList") List<String> list);
    List<Map<String,Object>> selectWaterPumpRunningByDate(//@Param("water_number") String water_number,
                                                          @Param("dateList") List<String> dateList);
    List<Map<String,Object>> selectRunningDataByDate(@Param("dateList") List<String> list);

}
