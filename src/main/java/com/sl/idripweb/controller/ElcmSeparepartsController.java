package com.sl.idripweb.controller;

import com.sl.idripweb.service.ElcmSeparepartsService;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/elcmSepareparts")
public class ElcmSeparepartsController {

    @Autowired
    private ElcmSeparepartsService separepartsService;

    //##################备件字典表部分########################
    /**
     * 获取所有的备件类型
     * @return
     */
    @RequestMapping("getSeparepartsTypes")
    public WebResult getSeparepartsTypes(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsTypes(map);
    }

    /**
     * 获取所有的备件来源
     * @return
     */
    @RequestMapping("getSeparepartsSources")
    public WebResult getSeparepartsSources(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsSources(map);
    }




    //########################备件台账列表部分#####################################
    /**
     * 备件台账页面根据筛选条件获取备件台账列表
     * @return
     */
    @RequestMapping("getSeparepartsList")
    public WebResult getSeparepartsList(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsList(map);
    }

    /**
     * 备件台账页面导出数据
     * @return
     */
    @RequestMapping("exportSeparepartsList")
    public void exportSeparepartsList(@RequestAttribute Map<String,Object> map, HttpServletResponse response){
        separepartsService.exportSeparepartsList(map, response);
    }

    /**
     * 备件台账页面下载模板
     * @return
     */
    @RequestMapping("downloadTemplate")
    public void downloadTemplate(@RequestAttribute Map<String,Object> map, HttpServletResponse response){
        separepartsService.downloadTemplate(map, response);
    }

    /**
     * 备件台账页面模板导入
     * @return
     */
    @RequestMapping("importSepareparts")
    public WebResult importSepareparts(@RequestAttribute Map<String,Object> map, MultipartFile file){
        return separepartsService.importSepareparts(map, file);
    }

    /**
     * 选择备件页面根据条件获取备件列表
     * @return
     */
    @RequestMapping("getSeparepartsListForSelect")
    public WebResult getSeparepartsListForSelect(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsListForSelect(map);
    }




    //######################单个备件台账部分############################
    /**
     * 单个备件台账信息查询
     * @return
     */
    @RequestMapping("getSeparepartsById")
    public WebResult getSeparepartsById(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsById(map);
    }

    /**
     * 添加单个备件
     * @return
     */
    @RequestMapping("addSingleSepareparts")
    public WebResult addSingleSepareparts(@RequestAttribute Map<String,Object> map){
        return separepartsService.addSingleSepareparts(map);
    }

    /**
     * 修改备件信息
     * @return
     */
    @RequestMapping("updateSepareparts")
    public WebResult updateSepareparts(@RequestAttribute Map<String,Object> map){
        return separepartsService.updateSepareparts(map);
    }

    /**
     * 删除备件信息
     * @return
     */
    @RequestMapping("deleteSepareparts")
    public WebResult deleteSepareparts(@RequestAttribute Map<String,Object> map){
        return separepartsService.deleteSepareparts(map);
    }







    //########################备件入库#####################################
    /**
     * 入库记录查询
     * @return
     */
    @RequestMapping("getSeparepartsInRecord")
    public WebResult getSeparepartsInRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsInRecord(map);
    }

    /**
     * 单条入库记录查询
     * @return
     */
    @RequestMapping("getSeparepartsInById")
    public WebResult getSeparepartsInById(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsInById(map);
    }

    /**
     * 根据入库记录id（多个）查询多条入库记录
     * @return
     */
    @RequestMapping("getSeparepartsInByIds")
    public WebResult getSeparepartsInByIds(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsInByIds(map);
    }

    /**
     * 入库单新增
     * @return
     */
    @RequestMapping("addSeparepartsInRecord")
    public WebResult addSeparepartsInRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.addSeparepartsInRecord(map);
    }

    /**
     * 入库单修改
     * @return
     */
    @RequestMapping("updateSeparepartsInRecord")
    public WebResult updateSeparepartsInRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.updateSeparepartsInRecord(map);
    }

    /**
     * 入库单删除
     * @return
     */
    @RequestMapping("deleteSeparepartsInRecord")
    public WebResult deleteSeparepartsInRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.deleteSeparepartsInRecord(map);
    }






    //########################备件出库管理#####################################
    /**
     * 备件申请列表查询
     * @return
     */
    @RequestMapping("getSeparepartsApply")
    public WebResult getSeparepartsApply(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsApply(map);
    }

    /**
     * 查询备件申请下的备件清单
     * @return
     */
    @RequestMapping("getSeparepartsApplyOut")
    public WebResult getSeparepartsApplyOut(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsApplyOut(map);
    }

    /**
     * 根据申请记录id（多个）查询多条申请记录
     * @return
     */
    @RequestMapping("getSeparepartsApplyOutByIds")
    public WebResult getSeparepartsApplyOutByIds(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsApplyOutByIds(map);
    }

    /**
     * 出库单新增
     * @return
     */
    @RequestMapping("addSeparepartsOutRecord")
    public WebResult addSeparepartsOutRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.addSeparepartsOutRecord(map);
    }

    /**
     * 备件申请列表查询
     * @return
     */
    @RequestMapping("getSeparepartsOutRecord")
    public WebResult getSeparepartsOutRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsOutRecord(map);
    }

    /**
     * 出库单修改
     * @return
     */
    @RequestMapping("updateSeparepartsOutRecord")
    public WebResult updateSeparepartsOutRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.updateSeparepartsOutRecord(map);
    }

    /**
     * 出库单删除
     * @return
     */
    @RequestMapping("deleteSeparepartsOutRecord")
    public WebResult deleteSeparepartsOutRecord(@RequestAttribute Map<String,Object> map){
        return separepartsService.deleteSeparepartsOutRecord(map);
    }

    /**
     * 备件申请记录新增
     * @return0000
     */
    @RequestMapping("addSeparepartsApply")
    public WebResult addSeparepartsApply(@RequestAttribute Map<String,Object> map){
        return separepartsService.addSeparepartsApply(map);
    }

    /**
     * 根据申请备件工单类型和工单号查询所有工单申请记录
     * @return
     */
    @RequestMapping("getSeparepartsApplyList")
    public WebResult getSeparepartsApplyList(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsApplyList(map);
    }

    /**
     * 单条出库记录查询
     * @return
     */
    @RequestMapping("getSeparepartsOutById")
    public WebResult getSeparepartsOutById(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsOutById(map);
    }

    /**
     * 出库记录批量查询
     * @return
     */
    @RequestMapping("getSeparepartsOutByIds")
    public WebResult getSeparepartsOutByIds(@RequestAttribute Map<String,Object> map){
        return separepartsService.getSeparepartsOutByIds(map);
    }

}
