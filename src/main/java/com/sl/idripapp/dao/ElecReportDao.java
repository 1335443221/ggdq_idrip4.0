package com.sl.idripapp.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ElecReportDao {
    //获取区域用能 分类的 配电室列表
    List<Map<String,Object>> getCategoryTransformerroom(Map<String,Object> map);
    //Category详情
    Map<String,Object> getCategoryByCategoryId(Map<String,Object> map);
    //进线列表 category_type=3
    List<Map<String,Object>> getBranchList(Map<String,Object> map);
    int getBranchListCount(Map<String,Object> map);
    //获取能耗数据  单日
    List<Map<String,Object>> getSingleDayData(Map<String,Object> map);
    //获取能耗数据
    List<Map<String,Object>> getSectionData(Map<String,Object> map);
    List<Map<String,Object>> getSectionDataDetail(Map<String,Object> map);
    //获取该进线的出线列表
    List<Map<String,Object>> getCategoryByParentId(Map<String,Object> map);
    int getCategoryByParentIdCount(Map<String,Object> map);
    //获取曲线数据 从UI表
    ArrayList<Map<String, Object>> getUIData(Map<String, Object> map);
}
