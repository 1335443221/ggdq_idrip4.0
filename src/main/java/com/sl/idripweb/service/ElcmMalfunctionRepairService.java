package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface ElcmMalfunctionRepairService {

    public WebResult getMalfunctionStatus(Map<String, Object> map);
    public WebResult getMalfunctionList(Map<String, Object> map);
    public WebResult addMalfunction(Map<String, Object> map);
    public WebResult getMalfunction(Map<String, Object> map);
    public WebResult dealMalfunction(Map<String, Object> map);
    public WebResult updateMalfunction(Map<String, Object> map);
    public WebResult revokeMalfunction(Map<String, Object> map);
    public WebResult deleteMalfunction(Map<String, Object> map);


    //=============================维修工单==============================//

    public WebResult getRepairStatus(Map<String, Object> map);
    public WebResult getRepairList(Map<String, Object> map);
    public WebResult addRepair(Map<String, Object> map);
    public WebResult assignUser(Map<String, Object> map);
    public WebResult getRepair(Map<String, Object> map);
    public WebResult getRepairByIds(Map<String, Object> map);
    public WebResult revokeRepair(Map<String, Object> map);
    public WebResult getMyRepairList(Map<String, Object> map);
    public WebResult receiveRepair(Map<String, Object> map);
    public WebResult chargeback(Map<String, Object> map);
    public WebResult transfer(Map<String, Object> map);
    public WebResult complete(Map<String, Object> map);

}
