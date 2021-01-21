package com.sl.common.dao;

import com.sl.common.entity.OperationLogs;
import com.sl.common.entity.SjfEpFees;
import com.sl.common.entity.SjfYesterdayData;
import com.sl.common.entity.params.SetValParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface CommonDao {
    //根据项目id  获取所有厂区列表
    ArrayList<Map<String, Object>> getFactoryByProjectid(Map<String, Object> map);

    //下至操作！
    ArrayList<Map<String, Object>> getTGInfoByName(SetValParams setValParams);
    int setOperationLogs(OperationLogs operationLogsVo);
    //收缴费!!!
    ArrayList<Map<String, Object>> getSjfAllProject(Map<String,Object> map);
    ArrayList<SjfYesterdayData> getSjfAllMeterData(Map<String,Object> map);
    ArrayList<Map<String, Object>> getSjfMeterDataByProject(Map<String,Object> map);
    ArrayList<Map<String, Object>> getSjfYearDataByList(Map<String,Object> map);
    //收缴费!!! 获取分户区间用电总量和总钱
    Map<String, Object> getSjfHouseSumEpFees(Map<String, Object> map);
    int deleteSjfAllSjfData(Map<String, Object> map);
    int insertSjfAllSjfData(List<SjfEpFees> list);
    //收缴费!!!  阶梯
    Map<String, Object> getSjfLadder(Map<String, Object> map);
    List<Map<String, Object>> getSjfTodayPower(Map<String, Object> map);

    //获取电表标签
    ArrayList<Map<String, Object>> getElecTag();

}
