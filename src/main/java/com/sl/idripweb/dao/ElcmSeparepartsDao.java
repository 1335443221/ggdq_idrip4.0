package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface ElcmSeparepartsDao {

    //#########################备件字典表部分################################################
    //获取所有的设备类型
    ArrayList<HashMap<String, Object>> getSeparepartsTypes();

    //获取所有的备件来源
    ArrayList<HashMap<String, Object>> getSeparepartsSources();



    //##############################备件台账列表部分###################################################
    //备件台账页面根据筛选条件获取备件台账列表
    ArrayList<HashMap<String, Object>> getSeparepartsList(Map<String, Object> map);

    // 备件台账页面根据筛选条件获取备件台账列表->count
    int getSeparepartsListCount(Map<String, Object> map);

    //备件台账页面导出数据查询
    ArrayList<HashMap<String, Object>> getExportDataList(Map<String, Object> map);

    //查询所有备件编号
    ArrayList<String> getNumberList(Map<String, Object> map);

    //批量添加备件
    int batchAddSepareparts(Map<String, Object> map);

    //选择备件页面根据条件获取备件列表
    ArrayList<HashMap<String, Object>> getSeparepartsListForSelect(Map<String, Object> map);





    //##############################单个备件台账部分###################################################
    //单个备件台账信息查询
    HashMap<String, Object> getSeparepartsById(Map<String, Object> map);

    //添加单个备件
    int addSingleSepareparts(Map<String, Object> map);

    //修改备件信息
    int updateSepareparts(Map<String, Object> map);

    //删除备件信息
    int deleteSepareparts(Map<String, Object> map);

    //更新备件库存信息
    int updateSeparepartsInventory(Map<String, Object> map);

    //查询备件关联设备信息
    ArrayList<Integer> getDevicesRelation(Map<String, Object> map);

    //添加备件关联设备信息
    int addDevicesRelation(Map<String, Object> map);

    //删除备件关联设备信息
    int deleteDevicesRelation(Map<String, Object> map);





    //##############################备件入库###################################################
    //入库记录查询
    ArrayList<HashMap<String, Object>> getSeparepartsInRecord(Map<String, Object> map);

    //入库记录查询-》count
    int getSeparepartsInRecordCount(Map<String, Object> map);

    //单条入库记录查询
    HashMap<String, Object> getSeparepartsInById(Map<String, Object> map);

    //获取单条入库记录下的备件采购列表
    ArrayList<HashMap<String, Object>> getSeparepartsInList(Map<String, Object> map);

    //入库单新增
    int addSeparepartsInRecord(Map<String, Object> map);

    //入库单修改
    int updateSeparepartsInRecord(Map<String, Object> map);

    //入库单删除
    int deleteSeparepartsInRecord(Map<String, Object> map);

    //入库单采购列表删除
    int deleteSeparepartsInList(Map<String, Object> map);

    //入库单采购列表批量添加
    int addSeparepartsInList(Map<String, Object> map);







    //##############################备件出库管理###################################################
    //备件申请列表查询
    ArrayList<HashMap<String, Object>> getSeparepartsApply(Map<String, Object> map);

    //备件申请列表查询--count
    int getSeparepartsApplyCount(Map<String, Object> map);

    //根据备件申请id查询单个备件申请
    HashMap<String, Object> getSeparepartsApplyById(Map<String, Object> map);

    //查询备件申请下的备件清单
    ArrayList<HashMap<String, Object>> getSeparepartsApplyOut(Map<String, Object> map);

    //出库单新增
    int addSeparepartsOutRecord(Map<String, Object> map);

    //出库单出库列表批量添加
    int addSeparepartsOutList(Map<String, Object> map);

    //出库明细列表查询
    ArrayList<HashMap<String, Object>> getSeparepartsOutRecord(Map<String, Object> map);

    //出库明细列表查询--count
    int getSeparepartsOutRecordCount(Map<String, Object> map);

    //出库单修改
    int updateSeparepartsOutRecord(Map<String, Object> map);

    //出库单删除
    int deleteSeparepartsOutRecord(Map<String, Object> map);

    //出库单采购列表删除
    int deleteSeparepartsOutList(Map<String, Object> map);

    //备件申请记录新增
    int addSeparepartsApply(Map<String, Object> map);

    //备件申请备件列表批量添加
    int addSeparepartsApplyList(Map<String, Object> map);

    //根据申请备件工单类型和工单号查询所有工单申请记录
    ArrayList<HashMap<String, Object>> getSeparepartsApplyList(Map<String, Object> map);

    //根据申请id获取备件申请列表
    ArrayList<HashMap<String, Object>> getSeparepartsApplyOutList(Map<String, Object> map);

    //更新备件申请已出库数量信息
    int updateSeparepartsApplyOut(Map<String, Object> map);

    //单条出库记录查询
    HashMap<String, Object> getSeparepartsOutById(Map<String, Object> map);

    //获取单条出库记录下的备件出库列表
    ArrayList<HashMap<String, Object>> getSeparepartsOutList(Map<String, Object> map);

}
