package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WebAccountTrainDao {

    //##########################培训管理############################
    //获取培训台账列表数据
    List<Map<String, Object>> getTrainList(Map<String, Object> map);

    //获取培训台账列表数据->count
    int getTrainListCount(Map<String, Object> map);

    //根据培训台账id获取该培训资料
    List<Map<String, Object>> getTrainMaterial(Map<String, Object> map);

    //通过id获取培训台账
    Map<String, Object> getTrainById(Map<String, Object> map);

    //新增培训台账基础
    int addTrain(Map<String, Object> map);

    //新增培训资料
    int addTrainMaterial(Map<String, Object> map);

    //修改培训台账基础
    int updateTrain(Map<String, Object> map);

    //删除某培训台账id下所有的培训材料
    int deleteTrainMaterial(Map<String, Object> map);

    //批量删除培训台账
    int deleteTrains(Map<String, Object> map);

    //批量删除培训台账id下所有的培训材料
    int deleteTrainMaterials(Map<String, Object> map);

    //获取所有培训内容
    List<Map<String, Object>> getTrainContents(Map<String, Object> map);


    //##########################培训单############################

    //获取培训单列表数据
    List<Map<String, Object>> getTrainSheetList(Map<String, Object> map);

    //获取培训单列表数据->count
    int getTrainSheetListCount(Map<String, Object> map);

    //根据培训单id获取该培训单文件
    List<Map<String, Object>> getTrainSheetFile(Map<String, Object> map);

    //根据培训单id获取该培训单
    Map<String, Object> getTrainSheetById(Map<String, Object> map);

    //新增培训单
    int addTrainSheet(Map<String, Object> map);

    //新增培单文件
    int addTrainSheetFile(Map<String, Object> map);

    //修改培训单
    int updateTrainSheet(Map<String, Object> map);

    //删除培训单文件
    int deleteTrainSheetFile(Map<String, Object> map);

    //删除培训单
    int deleteTrainSheet(Map<String, Object> map);





    //##########################数据统计############################

    //参与培训男女比例
    List<Map<String, Object>> getSexRatioChart(Map<String, Object> map);

    //参与培训部门比例
    List<Map<String, Object>> getDepartmentRatioChart(Map<String, Object> map);

    //培训成绩
    List<Map<String, Object>> getTrainsResultChart(Map<String, Object> map);

    //各部门参与人数
    List<Map<String, Object>> getDepartmentMonthChart(Map<String, Object> map);
}
