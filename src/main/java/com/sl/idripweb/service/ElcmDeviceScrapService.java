package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface ElcmDeviceScrapService {

    public WebResult getScrapStatus(Map<String, Object> map);
    public WebResult getScrapList(Map<String, Object> map);
    public WebResult revokeApply(Map<String, Object> map);
    public WebResult reapply(Map<String, Object> map);
    public WebResult deleteScrap(Map<String, Object> map);
    public WebResult addScrap(Map<String, Object> map);
    public WebResult updateScrap(Map<String, Object> map);
    public WebResult getScrapDetail(Map<String, Object> map);
}
