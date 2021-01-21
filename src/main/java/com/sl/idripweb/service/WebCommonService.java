package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface WebCommonService {

    public WebResult getFactory(Map<String, Object> map);
    public WebResult getBuilding(Map<String, Object> map);
    public WebResult getSjfHouse(Map<String, Object> map);
    public WebResult getUserList(Map<String, Object> map);
    public WebResult getUserInfo(Map<String, Object> map, HttpServletRequest request);

    //向houseList中存入余额 添加余额 balance字段
    public List<Map<String,Object>> addBalanceToHouseList(List<Map<String, Object>> houseList, Map<String, Object> map);


    /**
     * 获取申请记录
     * @param oddNumber 单号
     * @return
     */
    public List<Map<String, Object>> getElcmApprovalRecord(String oddNumber,String project_id);


    /**
     * 备件申请情况
     * @param oddNumber 单号
     * @return
     */
    public List<Map<String, Object>> getSparepartsByNumber(String oddNumber,String type_id,String project_id);

    /**
     * 获取单号
     * @param type  类型  BF：报废  /WB：维保   /WX：维修 /JH：计划  /BX: 保修
     * @param project_id 项目id
     * @return
     */
    public String getElcmOddNumber(String type,String project_id);


    /**
     * 新增审批
     * @param odd_number  单号
     * @param type  类型 1：维修工单  2：维保工单   3：维保计划   4：设备报废
     * @param approval_name  审批名/单号名/设备名
     * @param apply_by  审批人id
     * @param project_id  审批项目id
     * @return
     */
    public int addElcmApproval(String odd_number,String type,String approval_name,String apply_by,String project_id);


    /**
     *
     * 新增审批记录
     * @param approval_id  审批id
     * @param user_id  申请人id
     * @param content  审批原因/驳回原因等 /发起申请
     * @param status  申请状态  申请/重新申请/已通过/已驳回等
     * @return
     */
    public int addElcmApprovalRecord(String approval_id,String user_id,String content,String status);


    /**
     * 修改审批状态
     * @param approval_id  审批id
     * @param status  状态id
     * @return
     */
    public int updateApproveStatus(String approval_id,String status);


    /**
     * 修改审批
     * @param approval_id  审批id
     * @param approval_name  审批名字
     * @return
     */
    public int updateApprove(String approval_id,String approval_name,String apply_at,String apply_by);


    /**
     * 修改审批  相关的单号状态
     * @param type_id  审批类型id
     * @param odd_number  单号
     * @param status  状态
     * @param project_id  project_id
     * @return
     */
    public int updateElcmStatus(String type_id,String odd_number,String status,String project_id);


    /**
     * 删除审批
     * @param odd_number  单号
     * @param type_id  类型
     * @return
     */
    public int deleteApproval(String odd_number,String type_id);


    /**
     * 修改设备状态
     * @param device_id
     * @param status
     * @return
     */
    public int updateDeviceStatus(String device_id,String status);


    /**
     * 检测设备状态
     * @param device_id
     * @return
     */
    public int checkDeviceStatus(String device_id);

    /**
     * 获取设备状态
     * @param device_id
     * @return
     */
    public int getDeviceStatus(String device_id);

    /**
     * 添加维保记录
     * @param
     * @return
     */
    public int addElcmMaintenanceRecord(Map<String,Object> map);
}
