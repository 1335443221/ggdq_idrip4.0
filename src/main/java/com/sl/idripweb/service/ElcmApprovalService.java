package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface ElcmApprovalService {

    public WebResult getApprovalType(Map<String, Object> map);
    public WebResult getPendingApprovalList(Map<String, Object> map);
    public WebResult getApprovedList(Map<String, Object> map);
    public WebResult getApproveDetail(Map<String, Object> map);
    public WebResult approve(Map<String, Object> map);
    public int getApprovalResult(Map<String, Object> map);
}
