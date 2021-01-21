package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import java.util.Map;

public interface WebAccountPartsService {

    public WebResult getType(Map<String, Object> map);
    public WebResult getPartsList(Map<String, Object> map);
    public WebResult addParts(Map<String, Object> map);
    public WebResult updateParts(Map<String, Object> map);
    public WebResult deleteParts(Map<String, Object> map);



    public WebResult getAllParts(Map<String, Object> map);
    public WebResult getPartsOutList(Map<String, Object> map);
    public WebResult addPartsOut(Map<String, Object> map);
    public WebResult updatePartsOut(Map<String, Object> map);
    public WebResult deletePartsOut(Map<String, Object> map);


    public WebResult getPartsInList(Map<String, Object> map);
    public WebResult addPartsIn(Map<String, Object> map);
    public WebResult updatePartsIn(Map<String, Object> map);
    public WebResult deletePartsIn(Map<String, Object> map);

    public WebResult statistics(Map<String, Object> map);
    public WebResult outAndInRanking(Map<String, Object> map);
}
