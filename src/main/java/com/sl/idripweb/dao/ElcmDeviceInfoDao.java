package com.sl.idripweb.dao;

import com.sl.idripapp.entity.ElcmDeviceTypeTree;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface ElcmDeviceInfoDao {

    //#########################设备字典表部分################################################
    //获取所有的设备类型
    ArrayList<ElcmDeviceTypeTree> getDeviceTypeTree(Map<String, Object> map);
    ArrayList<Map<String,Object>> getDeviceCountByType(Map<String, Object> map);

    //获取所有的设备类型
    ArrayList<HashMap<String, Object>> getDeviceTypes();

    //获取所有的设备状态
    ArrayList<HashMap<String, Object>> getDeviceStatus();

    //获取所有的资产类别
    ArrayList<HashMap<String, Object>> getDeviceMaterial();




    //##############################设备列表部分###################################################
    //设备台账页面根据筛选条件获取设备列表
    ArrayList<HashMap<String, Object>> getDeviceList(Map<String, Object> map);

    //设备台账页面根据筛选条件获取设备列表--count
    int getDeviceListCount(Map<String, Object> map);

    //设备台账页面导出数据查询
    ArrayList<HashMap<String, Object>> getExportDataList(Map<String, Object> map);

    //查询所有设备编号
    ArrayList<String> getDeviceNumberList(Map<String, Object> map);

    //批量添加设备
    int batchAddDevice(Map<String, Object> map);

    //选择设备页面根据条件获取设备列表
    ArrayList<HashMap<String, Object>> getDeviceListForSelect(Map<String, Object> map);

    //获取设备台账页批量打印接口数据
    ArrayList<HashMap<String, Object>> getPrintDataList(Map<String, Object> map);




    //##############################设备基础信息部分###################################################
    //设备台账页面基础信息查询
    HashMap<String, Object> getDeviceById(Map<String, Object> map);

    //添加单个设备
    int addSingleDevice(Map<String, Object> map);

    //修改设备信息
    int updateDevice(Map<String, Object> map);






    //##############################设备组成元件部分###################################################
    //组成元件列表查询
    ArrayList<HashMap<String, Object>> getComponentList(Map<String, Object> map);

    //组成元件列表查询--count
    int getComponentListCount(Map<String, Object> map);

    //根据元器件id查询单个设备元器件
    HashMap<String, Object> getComponentById(Map<String, Object> map);

    //新增设备元器件
    int addComponent(Map<String, Object> map);

    //修改设备元器件
    int updateComponent(Map<String, Object> map);

    //删除设备元器件
    int deleteComponent(Map<String, Object> map);





    //##############################设备技术参数部分###################################################
    //技术参数列表查询
    ArrayList<HashMap<String, Object>> getTechnicalParamList(Map<String, Object> map);

    //技术参数列表查询-count
    int getTechnicalParamListCount(Map<String, Object> map);

    //根据技术参数id查询单个技术参数
    HashMap<String, Object> getTechnicalParamById(Map<String, Object> map);

    //新增设备技术参数
    int addTechnicalParam(Map<String, Object> map);

    //修改设备技术参数
    int updateTechnicalParam(Map<String, Object> map);

    //删除设备技术参数
    int deleteTechnicalParam(Map<String, Object> map);





    //##############################设备维保记录部分###################################################
    //维保记录列表查询
    ArrayList<HashMap<String, Object>> getMaintenanceRecordList(Map<String, Object> map);

    //维保记录列表查询-count
    int getMaintenanceRecordListCount(Map<String, Object> map);

    //维保记录统计查询
    ArrayList<HashMap<String, Object>> getMaintenanceRecordCount(Map<String, Object> map);





    //##############################设备保养清单部分###################################################
    //保养清单列表查询
    ArrayList<HashMap<String, Object>> getMaintainList(Map<String, Object> map);

    //保养清单列表查询-count
    int getMaintainListCount(Map<String, Object> map);

    //根据保养清单id查询单个保养清单
    HashMap<String, Object> getMaintainById(Map<String, Object> map);

    //新增设备保养清单
    int addMaintain(Map<String, Object> map);

    //修改设备保养清单
    int updateMaintain(Map<String, Object> map);

    //删除设备保养清单
    int deleteMaintain(Map<String, Object> map);





    //##############################设备资料部分###################################################
    //设备资料列表查询
    ArrayList<HashMap<String, Object>> getMaterialList(Map<String, Object> map);

    //新增设备资料
    int addMaterial(Map<String, Object> map);

    //删除设备资料
    int deleteMaterial(Map<String, Object> map);

}
