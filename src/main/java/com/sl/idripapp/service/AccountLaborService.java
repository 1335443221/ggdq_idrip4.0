package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountLaborService {

    public AppResult getLaborList(Map<String, Object> map);
    public AppResult getAllLabor(Map<String, Object> map);

    public AppResult getLaborOutList(Map<String, Object> map);
    public AppResult addLaborOut(Map<String, Object> map);
    public AppResult updateLaborOut(Map<String, Object> map);
    public AppResult deleteLaborOut(Map<String, Object> map);
    public AppResult getLaborOutDetail(Map<String, Object> map);


    public AppResult getLaborInList(Map<String, Object> map);
    public AppResult addLaborIn(Map<String, Object> map);
    public AppResult updateLaborIn(Map<String, Object> map);
    public AppResult deleteLaborIn(Map<String, Object> map);
    public AppResult getLaborInDetail(Map<String, Object> map);
}
