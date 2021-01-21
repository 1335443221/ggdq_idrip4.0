package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;
import java.util.Map;

public interface WebAccountStockService {

    public WebResult getType(Map<String, Object> map);
    public WebResult getStockList(Map<String, Object> map);
    public WebResult addStock(Map<String, Object> map);
    public WebResult updateStock(Map<String, Object> map);
    public WebResult deleteStock(Map<String, Object> map);



    public WebResult getAllStock(Map<String, Object> map);
    public WebResult getStockOutList(Map<String, Object> map);
    public WebResult addStockOut(Map<String, Object> map);
    public WebResult updateStockOut(Map<String, Object> map);
    public WebResult deleteStockOut(Map<String, Object> map);


    public WebResult getStockInList(Map<String, Object> map);
    public WebResult addStockIn(Map<String, Object> map);
    public WebResult updateStockIn(Map<String, Object> map);
    public WebResult deleteStockIn(Map<String, Object> map);

    public WebResult statistics(Map<String, Object> map);
    public WebResult outAndInRanking(Map<String, Object> map);
}
