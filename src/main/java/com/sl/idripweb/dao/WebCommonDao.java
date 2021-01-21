package com.sl.idripweb.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface WebCommonDao {

    ArrayList<Map<String, Object>> getFactory(Map<String, Object> map);
    ArrayList<Map<String, Object>> getBuilding(Map<String, Object> map);
    ArrayList<Map<String, Object>> getSjfHouse(Map<String, Object> map);
    ArrayList<Map<String, Object>> getUserList(Map<String, Object> map);

    //========设备管理=============//

    //报废 获取最后一个单号的末尾三位数
    String elcmScrapNumber(@Param("number")String number,
                           @Param("project_id")String project_id);
    //保修 获取最后一个单号的末尾三位数
    String elcmMalfunctionNumber(@Param("number")String number,
                           @Param("project_id")String project_id);
    //维修 获取最后一个单号的末尾三位数
    String elcmRepairNumber(@Param("number")String number,
                           @Param("project_id")String project_id);
    //维保计划 获取最后一个单号的末尾三位数
    String elcmTaskNumber(@Param("number")String number,
                            @Param("project_id")String project_id);
    //维保工单 获取最后一个单号的末尾三位数
    String elcmTaskRecordNumber(@Param("number")String number,
                            @Param("project_id")String project_id);

    //入库 获取最后一个单号的末尾三位数
    String elcmSeparepartsNumberIn(@Param("number")String number,
                            @Param("project_id")String project_id);

    //出库 获取最后一个单号的末尾三位数
    String elcmSeparepartsNumberOut(@Param("number")String number,
                                   @Param("project_id")String project_id);

    //备件号 获取最后一个单号的末尾三位数
    String elcmSeparepartsNumber(@Param("number")String number,
                                    @Param("project_id")String project_id);


    ArrayList<Map<String, Object>> getApprovalRecord(@Param("oddNumber")String oddNumber,@Param("project_id")String project_id);

    ArrayList<Map<String, Object>> getSparepartsByNumber(@Param("oddNumber")String oddNumber,
                                                         @Param("type_id")String type_id,
                                                         @Param("project_id")String project_id);

    int addElcmApproval(@Param("odd_number")String odd_number,
                              @Param("type")String type,
                              @Param("approval_name")String approval_name,
                              @Param("apply_by")String apply_by,
                              @Param("project_id")String project_id,
                              @Param("approval_id")Map<String, Object> approval_id);


    int addElcmApprovalRecord(@Param("approval_id")String approval_id,
                              @Param("user_id")String user_id,
                              @Param("content")String content,
                              @Param("status")String status);

    int getElcmApprovalId(@Param("odd_number")String odd_number,@Param("type_id")String type_id);

    int updateApproveStatus(@Param("approval_id")String approval_id,@Param("status")String status);

    int updateApprove(@Param("approval_id")String approval_id,
                      @Param("approval_name")String approval_name,
                      @Param("apply_at")String apply_at,
                      @Param("apply_by")String apply_by
                      );


    int updateScrapStatus(@Param("odd_number")String odd_number,
                          @Param("status")String status,
                          @Param("project_id")String project_id);

    int updateMalfunctionStatus(@Param("odd_number")String odd_number,
                                @Param("status")String status,
                                @Param("repair_status")String repair_status,
                                @Param("project_id")String project_id);
    int updateTaskRecordStatus(@Param("odd_number")String odd_number,
                                @Param("status")String status,
                                @Param("project_id")String project_id);


    int updateTaskStatus(@Param("odd_number")String odd_number,@Param("status")String status,@Param("project_id")String project_id);


    int deleteApproval(@Param("odd_number")String odd_number,@Param("type_id")String type_id);

    int updateDeviceStatus(@Param("device_id")String device_id,@Param("status")String status);

    int getMalfunctionCount(@Param("device_id")String device_id);
    int getTaskCount(@Param("device_id")String device_id);

    int getDeviceStatus(@Param("device_id") String device_id);

    int insertMaintenanceRecord(Map<String,Object> map);
}
