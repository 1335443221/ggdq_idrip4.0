package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ElcmSeparepartsService {

    //##################备件字典表部分#####################
    //获取所有的备件类型
    public WebResult getSeparepartsTypes(Map<String, Object> map);

    //获取所有的备件来源
    public WebResult getSeparepartsSources(Map<String, Object> map);




    //#################备件台账列表部分################################
    //备件台账页面根据筛选条件获取备件台账列表
    public WebResult getSeparepartsList(Map<String, Object> map);

    //备件台账页面导出数据
    public void exportSeparepartsList(Map<String, Object> map, HttpServletResponse response);

    //备件台账页面下载模板
    public void downloadTemplate(Map<String, Object> map, HttpServletResponse response);

    //备件台账页面模板导入
    public WebResult importSepareparts(Map<String, Object> map, MultipartFile file);

    //选择备件页面根据条件获取备件列表
    public WebResult getSeparepartsListForSelect(Map<String, Object> map);




    //######################单个备件台账部分#######################################
    //单个备件台账信息查询
    public WebResult getSeparepartsById(Map<String, Object> map);

    //添加单个备件
    public WebResult addSingleSepareparts(Map<String, Object> map);

    //修改备件信息
    public WebResult updateSepareparts(Map<String, Object> map);

    //删除备件信息
    public WebResult deleteSepareparts(Map<String, Object> map);






    //#################备件入库################################
    //备件台账页面根据筛选条件获取备件台账列表
    public WebResult getSeparepartsInRecord(Map<String, Object> map);

    //单条入库记录查询
    public WebResult getSeparepartsInById(Map<String, Object> map);

    //根据入库记录id（多个）查询多条入库记录
    public WebResult getSeparepartsInByIds(Map<String, Object> map);

    //入库单新增
    public WebResult addSeparepartsInRecord(Map<String, Object> map);

    //入库单修改
    public WebResult updateSeparepartsInRecord(Map<String, Object> map);

    //入库单删除
    public WebResult deleteSeparepartsInRecord(Map<String, Object> map);






    //#################备件出库管理################################
    //备件申请列表查询
    public WebResult getSeparepartsApply(Map<String, Object> map);

    //查询备件申请下的备件清单
    public WebResult getSeparepartsApplyOut(Map<String, Object> map);

    //根据申请记录id（多个）查询多条申请记录
    public WebResult getSeparepartsApplyOutByIds(Map<String, Object> map);

    //出库单新增
    public WebResult addSeparepartsOutRecord(Map<String, Object> map);

    //出库明细列表查询
    public WebResult getSeparepartsOutRecord(Map<String, Object> map);

    //出库单修改
    public WebResult updateSeparepartsOutRecord(Map<String, Object> map);

    //出库单删除
    public WebResult deleteSeparepartsOutRecord(Map<String, Object> map);

    //备件申请记录新增
    public WebResult addSeparepartsApply(Map<String, Object> map);

    //根据申请备件工单类型和工单号查询所有工单申请记录
    public WebResult getSeparepartsApplyList(Map<String, Object> map);

    //单条出库记录查询
    public WebResult getSeparepartsOutById(Map<String, Object> map);

    //出库记录批量查询
    public WebResult getSeparepartsOutByIds(Map<String, Object> map);

}
