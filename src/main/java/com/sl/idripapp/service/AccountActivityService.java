package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountActivityService {

    public AppResult getType(Map<String, Object> map);
    public AppResult getDate(Map<String, Object> map);
    public AppResult getActivityList(Map<String, Object> map);

}
