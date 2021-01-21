package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountLaborService {

    public WebResult getType(Map<String, Object> map);
    public WebResult getLaborList(Map<String, Object> map);
    public WebResult addLabor(Map<String, Object> map);
    public WebResult updateLabor(Map<String, Object> map);
    public WebResult deleteLabor(Map<String, Object> map);



    public WebResult getAllLabor(Map<String, Object> map);
    public WebResult getLaborOutList(Map<String, Object> map);
    public WebResult addLaborOut(Map<String, Object> map);
    public WebResult updateLaborOut(Map<String, Object> map);
    public WebResult deleteLaborOut(Map<String, Object> map);


    public WebResult getLaborInList(Map<String, Object> map);
    public WebResult addLaborIn(Map<String, Object> map);
    public WebResult updateLaborIn(Map<String, Object> map);
    public WebResult deleteLaborIn(Map<String, Object> map);

    public WebResult statistics(Map<String, Object> map);
    public WebResult outAndInRanking(Map<String, Object> map);
}
