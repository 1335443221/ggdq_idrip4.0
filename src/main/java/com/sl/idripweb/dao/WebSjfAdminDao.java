package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebSjfAdminDao {

    ArrayList<Map<String, Object>> getFactoryList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getBuildingList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getElecMeterList(Map<String, Object> map);
    int getElecMeterListCount(Map<String, Object> map);
    int getElecMeterListCount2(Map<String, Object> map);
    int addMeter(List<Map<String,Object>> list);
    Map<String, Object> getMeter(Map<String, Object> Map);
    int updateMeter(Map<String, Object> map);
    List<String>  getAllMeterNumber(Map<String, Object> map);
    List<Map<String, Object>>  getAllBuildingHouse(Map<String, Object> map);


    ArrayList<Map<String, Object>> houseType(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHouseList(Map<String, Object> map);
    int getHouseListCount(Map<String, Object> map);
    int updateHouse(Map<String, Object> map);
    Map<String, Object> getHouseByNumber(Map<String, Object> map);
    int updateHouseByMeter(Map<String, Object> map);
    int updateElecEpToday(Map<String, Object> map);
    int deleteSjfData(Map<String, Object> map);
    ArrayList<Map<String, Object>> getEpListByList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getPaymentListByList(Map<String, Object> map);
    Map<String, Object> getHouse(Map<String, Object> map);
    int deleteMeter(Map<String, Object> map);
    Map<String, Object> getLastPaymentRecord(Map<String, Object> map);
    Map<String, Object> getHouseEpFees(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHouseTemplate(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHouseNumber(Map<String, Object> map);
    ArrayList<Map<String, Object>> getPaymentRecord(Map<String, Object> map);
    ArrayList<Map<String, Object>> getAllPaymentRecord(Map<String, Object> map);
    ArrayList<Map<String, Object>> downloadPaymentData(Map<String, Object> map);
    int getPaymentRecordCount(Map<String, Object> map);
    int deleteHouse(Map<String, Object> map);




    ArrayList<Map<String, Object>> getAllTypeList(Map<String, Object> map);
    int getAllTypeListCount(Map<String, Object> map);
    Map<String, Object> getChargeUnit(Map<String, Object> map);
    Map<String, Object> getHouseTypeByType(Map<String, Object> map);
    Map<String, Object> getElecMeterNumber(Map<String, Object> map);
    int addHouseType(Map<String, Object> map);
    int updateHouseType(Map<String, Object> map);
    int addHouseTypeFuture(Map<String, Object> map);

    Map<String, Object> getFpg(Map<String, Object> map);
    Map<String, Object> getFpgFuture(Map<String, Object> map);
    Map<String, Object> getLadder(Map<String, Object> map);
    Map<String, Object> getLadderFuture(Map<String, Object> map);
    int addFpgFutrue(Map<String, Object> map);
    int addLadderFutrue(Map<String, Object> map);
    int deleteAllLadderFuture(Map<String,Object> map);
    int deleteAllFpgFuture(Map<String,Object> map);
    int deleteAllHouseTypeFuture(Map<String, Object> map);
    int deleteHouseType(Map<String, Object> map);
    int deleteHouseTypeFuture(Map<String, Object> map);

}
