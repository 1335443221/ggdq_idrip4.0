package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface WebSjfReportDao {

    //获取厂区楼宇树形列表
    ArrayList<Map<String, Object>> getCategoryTree(Map<String, Object> map);

    //通过厂区楼宇树形列表获取所有分户信息
    ArrayList<Map<String, Object>> getHousesByTree(Map<String, Object> map);

    //电费报表-》按日统计
    ArrayList<Map<String, Object>> chargeReportOfDay(Map<String, Object> map);

    //电费报表-》按月统计
    ArrayList<Map<String, Object>> chargeReportOfMonth(Map<String, Object> map);

    //电费报表-》按年统计
    ArrayList<Map<String, Object>> chargeReportOfYear(Map<String, Object> map);

    //电量报表-》按日统计
    ArrayList<Map<String, Object>> powerReportOfDay(Map<String, Object> map);

    //电量报表-》按月统计
    ArrayList<Map<String, Object>> powerReportOfMonth(Map<String, Object> map);

    //电量报表-》按年统计
    ArrayList<Map<String, Object>> powerReportOfYear(Map<String, Object> map);

    //电量报表-》分时
    ArrayList<Map<String, Object>> powerReportOfHour(Map<String, Object> map);

    //财务报表
    ArrayList<Map<String, Object>> financeReport(Map<String, Object> map);

    //财务报表--count
    int financeReportCount(Map<String, Object> map);

    //根据分户获取电表和通讯机信息
    ArrayList<Map<String, Object>> getMeterByHouse(@Param("houseIds") List<Integer> houseIds);

    //首页根据选中园区获取缴费用户数量
    int getHouseCount(Map<String, Object> map);

    //首页根据选中园区获取缴费用户数量
    ArrayList<Map<String, Object>> getTotalFees(Map<String, Object> map);

    //首页根据选中园区获取用户电表信息
    ArrayList<Map<String, Object>> getHouses(Map<String, Object> map);

    //获取园区下所有分户
    ArrayList<Map<String, Object>> getHousesByFactoryId(Map<String, Object> map);

    //获取项目的峰平谷和阶梯设置
    Map<String, Object> getChargeFpgAndLadder(Map<String, Object> map);

    //获取每个阶梯用户数量
    ArrayList<Map<String, Object>> getHousesOfLadder(Map<String, Object> map);

    //首页根据选中分时用户获取峰平谷电费电度统计
    Map<String, Object> getFpgData(Map<String, Object> map);

    //获取某个阶梯下的用户、用电量和缴费金额
    ArrayList<Map<String, Object>> getHouseByLadder(Map<String, Object> map);

    //获取分户分组下的电费和用电量
    ArrayList<Map<String, Object>> getPowerAndFeesOfHouse(Map<String, Object> map);

    //获取分户分组下的电费和用电量-All
    List<String> getPowerAndFeesOfHouseAll(Map<String, Object> map);

    //获取所有分户分组数据
    ArrayList<Map<String, Object>> getAllHouses(Map<String, Object> map);

    //财务报表获取所有分户分组数据
    ArrayList<Map<String, Object>> getHousesOfFinance(Map<String, Object> map);

    //统计某年某园区下总用电量、电费总计,不加入is_check_in字段，因为按电表统计
    Map<String, Object> getStatisticsTotal(Map<String, Object> map);

    //统计某年某园区下缴费金额总计,不加入is_check_in字段，因为按电表统计
    Map<String, Object> getStatisticsAmount(Map<String, Object> map);
}
