package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ElcmDeviceInfoService {

    //##################设备字典表部分#####################
    WebResult getDeviceTypeTree(Map<String, Object> map);

    //获取所有的设备类型
    public WebResult getDeviceTypes(Map<String, Object> map);

    //获取所有的设备状态
    public WebResult getDeviceStatus(Map<String, Object> map);

    //获取所有的资产类别
    WebResult getDeviceMaterial(Map<String, Object> map);




    //#################设备列表部分################################
    //设备台账页面根据筛选条件获取设备列表
    public WebResult getDeviceList(Map<String, Object> map);

    //设备台账页面导出数据
    public void exportDeviceList(Map<String, Object> map, HttpServletResponse response);

    //设备台账页面下载模板
    public void downloadTemplate(Map<String, Object> map, HttpServletResponse response);

    //设备台账页面模板导入
    public WebResult importDevices(Map<String, Object> map, MultipartFile file);

    //选择设备页面根据条件获取设备列表
    public WebResult getDeviceListForSelect(Map<String, Object> map);

    //获取设备台账页批量打印接口数据
    public WebResult getPrintDataList(Map<String, Object> map);




    //######################设备基础信息部分#######################################
    //设备台账页面基础信息查询
    public WebResult getDeviceById(Map<String, Object> map);

    //添加单个设备
    public WebResult addSingleDevice(Map<String, Object> map);

    //修改设备信息
    public WebResult updateDevice(Map<String, Object> map);




    //#######################设备元件部分######################################
    //组成元件列表查询
    public WebResult getComponentList(Map<String, Object> map);

    //根据元器件id查询单个设备元器件
    public WebResult getComponentById(Map<String, Object> map);

    //新增设备元器件
    public WebResult addComponent(Map<String, Object> map);

    //修改设备元器件
    public WebResult updateComponent(Map<String, Object> map);

    //删除设备元器件
    public WebResult deleteComponent(Map<String, Object> map);





    //#########################设备技术参数部分#############################################
    //技术参数列表查询
    public WebResult getTechnicalParamList(Map<String, Object> map);

    //根据技术参数id查询单个技术参数
    public WebResult getTechnicalParamById(Map<String, Object> map);

    //新增设备技术参数
    public WebResult addTechnicalParam(Map<String, Object> map);

    //修改设备技术参数
    public WebResult updateTechnicalParam(Map<String, Object> map);

    //删除设备技术参数
    public WebResult deleteTechnicalParam(Map<String, Object> map);




    //############################设备维保记录部分#########################################
    //维保记录列表查询,包含count
    public WebResult getMaintenanceRecordList(Map<String, Object> map);




    //###########################设备保养清单部分############################################
    //保养清单列表查询
    public WebResult getMaintainList(Map<String, Object> map);

    //根据保养清单id查询单个保养清单
    public WebResult getMaintainById(Map<String, Object> map);

    //新增设备保养清单
    public WebResult addMaintain(Map<String, Object> map);

    //修改设备保养清单
    public WebResult updateMaintain(Map<String, Object> map);

    //删除设备保养清单
    public WebResult deleteMaintain(Map<String, Object> map);




    //##############################设备资料部分################################################
    //设备资料列表查询
    public WebResult getMaterialList(Map<String, Object> map);

    //新增设备资料
    public WebResult addMaterial(Map<String, Object> map);

    //删除设备资料
    public WebResult deleteMaterial(Map<String, Object> map);

}
