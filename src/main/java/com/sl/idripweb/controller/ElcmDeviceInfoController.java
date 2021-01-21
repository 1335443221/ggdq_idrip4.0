package com.sl.idripweb.controller;

import com.sl.idripweb.service.ElcmDeviceInfoService;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/elcm")
public class ElcmDeviceInfoController {

    @Autowired
    private ElcmDeviceInfoService deviceInfoService;

    //##################设备字典表部分########################

    /**
     * 获取所有的设备类型
     * @param map
     * @return
     */
    @PostMapping("getDeviceTypeTree")
    public WebResult getDeviceTypeTree(@RequestAttribute Map<String, Object> map){
        return deviceInfoService.getDeviceTypeTree(map);
    }
    /**
     * 获取所有的设备类型
     * @return
     */
    @RequestMapping("getDeviceTypes")
    public WebResult getDeviceTypes(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getDeviceTypes(map);
    }

    /**
     * 获取所有的设备状态
     * @return
     */
    @RequestMapping("getDeviceStatus")
    public WebResult getDeviceStatus(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getDeviceStatus(map);
    }

    /**
     * 获取所有的资产类别
     * @return
     */
    @RequestMapping("getDeviceMaterial")
    public WebResult getDeviceMaterial(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getDeviceMaterial(map);
    }



    //########################设备列表部分#####################################
    /**
     * 设备台账页面根据筛选条件获取设备列表
     * @return
     */
    @RequestMapping("getDeviceList")
    public WebResult getDeviceList(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getDeviceList(map);
    }

    /**
     * 设备台账页面导出数据
     * @return
     */
    @RequestMapping("exportDeviceList")
    public void exportDeviceList(@RequestAttribute Map<String,Object> map, HttpServletResponse response){
        deviceInfoService.exportDeviceList(map, response);
    }

    /**
     * 设备台账页面下载模板
     * @return
     */
    @RequestMapping("downloadTemplate")
    public void downloadTemplate(@RequestAttribute Map<String,Object> map, HttpServletResponse response){
        deviceInfoService.downloadTemplate(map, response);
    }

    /**
     * 设备台账页面下载模板
     * @return
     */
    @RequestMapping("importDevices")
    public WebResult importDevices(@RequestAttribute Map<String,Object> map, MultipartFile file){
        return deviceInfoService.importDevices(map, file);
    }

    /**
     * 选择设备页面根据条件获取设备列表
     * @return
     */
    @RequestMapping("getDeviceListForSelect")
    public WebResult getDeviceListForSelect(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getDeviceListForSelect(map);
    }

    /**
     * 获取设备台账页批量打印接口数据
     * @return
     */
    @RequestMapping("getPrintDataList")
    public WebResult getPrintDataList(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getPrintDataList(map);
    }





    //######################设备基础信息部分############################
    /**
     * 设备台账页面基础信息查询
     * @return
     */
    @RequestMapping("getDeviceById")
    public WebResult getDeviceById(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getDeviceById(map);
    }

    /**
     * 添加单个设备
     * @return
     */
    @RequestMapping("addSingleDevice")
    public WebResult addSingleDevice(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.addSingleDevice(map);
    }

    /**
     * 修改设备信息
     * @return
     */
    @RequestMapping("updateDevice")
    public WebResult updateDevice(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.updateDevice(map);
    }




    //#####################设备组成元件###########################################
    /**
     * 组成元件列表查询
     * @return
     */
    @RequestMapping("getComponentList")
    public WebResult getComponentList(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getComponentList(map);
    }

    /**
     * 根据元器件id查询单个设备元器件
     * @return
     */
    @RequestMapping("getComponentById")
    public WebResult getComponentById(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getComponentById(map);
    }

    /**
     * 新增设备元器件
     * @return
     */
    @RequestMapping("addComponent")
    public WebResult addComponent(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.addComponent(map);
    }

    /**
     * 修改设备元器件
     * @return
     */
    @RequestMapping("updateComponent")
    public WebResult updateComponent(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.updateComponent(map);
    }

    /**
     * 删除设备元器件
     * @return
     */
    @RequestMapping("deleteComponent")
    public WebResult deleteComponent(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.deleteComponent(map);
    }




    //###########################设备技术参数########################################
    /**
     * 技术参数列表查询
     * @return
     */
    @RequestMapping("getTechnicalParamList")
    public WebResult getTechnicalParamList(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getTechnicalParamList(map);
    }

    /**
     * 根据技术参数id查询单个技术参数
     * @return
     */
    @RequestMapping("getTechnicalParamById")
    public WebResult getTechnicalParamById(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getTechnicalParamById(map);
    }

    /**
     * 新增设备技术参数
     * @return
     */
    @RequestMapping("addTechnicalParam")
    public WebResult addTechnicalParam(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.addTechnicalParam(map);
    }

    /**
     * 修改设备技术参数
     * @return
     */
    @RequestMapping("updateTechnicalParam")
    public WebResult updateTechnicalParam(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.updateTechnicalParam(map);
    }

    /**
     * 删除设备技术参数
     * @return
     */
    @RequestMapping("deleteTechnicalParam")
    public WebResult deleteTechnicalParam(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.deleteTechnicalParam(map);
    }




    //##########################设备维保记录#########################################
    /**
     * 维保记录列表查询,包含count
     * @return
     */
    @RequestMapping("getMaintenanceRecordList")
    public WebResult getMaintenanceRecordList(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getMaintenanceRecordList(map);
    }




    //#############################设备保养清单#################################
    /**
     * 保养清单列表查询
     * @return
     */
    @RequestMapping("getMaintainList")
    public WebResult getMaintainList(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getMaintainList(map);
    }

    /**
     * 根据保养清单id查询单个保养清单
     * @return
     */
    @RequestMapping("getMaintainById")
    public WebResult getMaintainById(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getMaintainById(map);
    }

    /**
     * 新增设备保养清单
     * @return
     */
    @RequestMapping("addMaintain")
    public WebResult addMaintain(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.addMaintain(map);
    }

    /**
     * 修改设备保养清单
     * @return
     */
    @RequestMapping("updateMaintain")
    public WebResult updateMaintain(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.updateMaintain(map);
    }

    /**
     * 删除设备保养清单
     * @return
     */
    @RequestMapping("deleteMaintain")
    public WebResult deleteMaintain(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.deleteMaintain(map);
    }





    //############################设备资料#####################################
    /**
     * 设备资料列表查询
     * @return
     */
    @RequestMapping("getMaterialList")
    public WebResult getMaterialList(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.getMaterialList(map);
    }

    /**
     * 新增设备资料
     * @return
     */
    @RequestMapping("addMaterial")
    public WebResult addMaterial(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.addMaterial(map);
    }

    /**
     * 删除设备资料
     * @return
     */
    @RequestMapping("deleteMaterial")
    public WebResult deleteMaterial(@RequestAttribute Map<String,Object> map){
        return deviceInfoService.deleteMaterial(map);
    }

}
