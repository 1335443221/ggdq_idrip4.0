package com.sl.idripapp.service;

import com.sl.common.utils.AppResult;

import java.util.Map;

public interface AccountPartsService {

    public AppResult getPartsList(Map<String, Object> map);
    public AppResult getAllParts(Map<String, Object> map);

    public AppResult getPartsOutList(Map<String, Object> map);
    public AppResult addPartsOut(Map<String, Object> map);
    public AppResult updatePartsOut(Map<String, Object> map);
    public AppResult deletePartsOut(Map<String, Object> map);
    public AppResult getPartsOutDetail(Map<String, Object> map);


    public AppResult getPartsInList(Map<String, Object> map);
    public AppResult addPartsIn(Map<String, Object> map);
    public AppResult updatePartsIn(Map<String, Object> map);
    public AppResult deletePartsIn(Map<String, Object> map);
    public AppResult getPartsInDetail(Map<String, Object> map);
}
