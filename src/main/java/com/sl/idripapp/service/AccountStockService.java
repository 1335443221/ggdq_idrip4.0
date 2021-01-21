package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountStockService {

    public AppResult getStockList(Map<String, Object> map);
    public AppResult getAllStock(Map<String, Object> map);

    public AppResult getStockOutList(Map<String, Object> map);
    public AppResult addStockOut(Map<String, Object> map);
    public AppResult updateStockOut(Map<String, Object> map);
    public AppResult deleteStockOut(Map<String, Object> map);
    public AppResult getStockOutDetail(Map<String, Object> map);


    public AppResult getStockInList(Map<String, Object> map);
    public AppResult addStockIn(Map<String, Object> map);
    public AppResult updateStockIn(Map<String, Object> map);
    public AppResult deleteStockIn(Map<String, Object> map);
    public AppResult getStockInDetail(Map<String, Object> map);
}
