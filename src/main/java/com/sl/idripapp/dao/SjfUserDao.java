package com.sl.idripapp.dao;

import com.sl.common.entity.SjfEpFees;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface SjfUserDao {

    //============================APP=============================//
    //获取园区-楼列表
    ArrayList<Map<String, Object>> getFactoryList(Map<String, Object> map);
    ArrayList<Map<String, Object>> getBuildingList(Map<String, Object> map);

    //缴费-获取收费单位
    Map<String, Object> getChargeUnit(Map<String, Object> map);

    //获取用户的分组列表
    ArrayList<Map<String, Object>> getGroupList(Map<String, Object> map);

    //获取所有分户
    ArrayList<Map<String, Object>> getAllHouse(Map<String, Object> map);
    //用户的分户
    ArrayList<Map<String, Object>> getUserHouseList(Map<String, Object> map);

    ArrayList<Map<String, Object>> getEpListByList(Map<String, Object> map);


    //用户用电详情
    Map<String, Object> getHouseDetail(Map<String, Object> map);
    //获取分户区间用电量和钱
    ArrayList<SjfEpFees> getHouseEpFees(Map<String, Object> map);
    //获取分户区间用电总量和总钱
     Map<String, Object> getHouseSumEpFees(Map<String, Object> map);

    List<Map<String, Object>> getTodayPower(Map<String, Object> map);
    //获取余额用户 关于余额的一些信息
    ArrayList<Map<String, Object>> getBalanceUserHouseList(Map<String, Object> map);
    //阶梯
    Map<String, Object> getLadder(Map<String, Object> map);


    //用户缴费详情
    Map<String, Object> getChargeHouseDetail(Map<String, Object> map);

    List<Map<String, Object>> checkUserHouse(Map<String, Object> map);

    //分组
    int addGroup(Map<String, Object> map);
    int updateGroup(Map<String, Object> map);
    int deleteGroup(Map<String, Object> map);
    int deleteUserHouseByGroup(Map<String, Object> map);
    //根据分组名称和当前用户获取分组数量
    int getCountForAdd(Map<String, Object> map);
    //根据分组名称和当前用户和组id获取分组数量
    int getCountForUpdate(Map<String, Object> map);

    //分户
    int deleteHouse(Map<String, Object> map);
    int addUserHouse(Map<String, Object> map);
    int updateUserHouse(Map<String, Object> map);

    int get(Map<String, Object> map);
//缴费记录
    ArrayList<Map<String, Object>> getHousePaymentRecord(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHousePaymentRecord2(Map<String, Object> map);
    int getHousePaymentRecordCount(Map<String, Object> map);
    Map<String, Object> getHouseRecordDetail(Map<String, Object> map);
    Map<String, Object> getAliWxConfig(Map<String, Object> map);
    int insertHousePaymentRecord(Map<String, Object> map);
    int insertAdminPaymentRecord(Map<String, Object> map);
    int updateHousePaymentRecord(Map<String, Object> map);
    int updateHouse(Map<String, Object> map);
    Map<String, Object> getHousePaymentRecordByOddNumber(Map<String, Object> map);
    int updateAdminPaymentRecord(Map<String, Object> map);
    ArrayList<Map<String, Object>> getHouseByOddNumber(Map<String, Object> map);



}
