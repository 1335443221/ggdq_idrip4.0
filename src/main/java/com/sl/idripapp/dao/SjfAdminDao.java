package com.sl.idripapp.dao;

import com.sl.common.entity.SjfEpFees;
import com.sl.common.entity.SjfFpgProjectSet;
import com.sl.common.entity.SjfYesterdayData;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface SjfAdminDao {

    //============================APP=============================//
    //用电查询
    int getHouseEpListCount(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHouseEpList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getEpListByList(Map<String, Object> map);
    //缴费记录
    int getHousePaymentRecordCount(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHousePaymentRecord(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHousePaymentRecord2(Map<String, Object> map);
    Map<String, Object> getHouseRecordDetail(Map<String, Object> map);
    //分户管理
    ArrayList<Map<String, Object>> getHouseList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHouseType(Map<String, Object> map);
    Map<String, Object> getHouseElecMeterNumber(Map<String, Object> map);
    Map<String, Object> getHouseElecMeter(Map<String, Object> map);
    int updateHouse(Map<String, Object> map);
    int deleteHouse(Map<String, Object> map);
    int updateElecEpToday(Map<String, Object> map);
    int updateHouseByIds(Map<String, Object> map);
    Map<String, Object> getHouseDetail(Map<String, Object> map);
    //电费设置
    Map<String, Object> getElecSettingByType(Map<String, Object> map);
    Map<String, Object> getFpg(Map<String, Object> map);
    Map<String, Object> getLadder(Map<String, Object> map);
    Map<String, Object> checkElecSettingFuture(Map<String, Object> map);
    Map<String, Object> getElecSettingByFuture(Map<String, Object> map);
    int addElecSetting(Map<String, Object> map);
    int deleteAllElecSettingFuture(Map<String, Object> map);
    int deleteElecSetting(Map<String, Object> map);
    int addElecSettingFuture(Map<String, Object> map);
    Map<String, Object> getFpgFuture(Map<String, Object> map);
    int addFpgFutrue(Map<String, Object> map);
    Map<String, Object> getLadderFuture(Map<String, Object> map);
    int addLadderFutrue(Map<String, Object> map);
    int deleteElecSettingFuture(Map<String, Object> map);
    int deleteFpgFuture(Map<String, Object> map);
    int deleteLadderFuture(Map<String, Object> map);


    ArrayList<SjfYesterdayData> getAllMeterData(Map<String,Object> map);
    ArrayList<Map<String, Object>> getAllProject(Map<String,Object> map);
    int insertAllSjfData(List<SjfEpFees> list);
    int deleteAllSjfData(Map<String, Object> map);
    int deleteSjfData(Map<String, Object> map);

    ArrayList<Map<String, Object>> getAllElecSettingFuture(Map<String,Object> map);
    ArrayList<Map<String, Object>> getAllLadderFuture(Map<String,Object> map);
    ArrayList<SjfFpgProjectSet> getAllFpgFuture(Map<String,Object> map);
    int deleteAllLadderFuture(Map<String,Object> map);
    int deleteAllFpgFuture(Map<String,Object> map);
    int updateElecSetting(Map<String,Object> map);
    int updateAllElecSetting(Map<String,Object> map);
    int updateAllLadder(Map<String,Object> map);
    int updateAllFpg(SjfFpgProjectSet map);

    int addFpgProjectSet(Map<String, Object> map);
    int updateFpgProjectSet(Map<String, Object> map);
    ArrayList<Map<String, Object>> getFpgProjectSet(Map<String, Object> map);
    ArrayList<Map<String, Object>> getTgListByProject(Map<String,Object> map);
    ArrayList<Map<String, Object>> getMeterDataByProject(Map<String,Object> map);

    ArrayList<Map<String, Object>> getYearDataByList(Map<String,Object> map);
}

