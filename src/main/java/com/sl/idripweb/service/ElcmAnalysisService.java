package com.sl.idripweb.service;


import com.sl.common.utils.WebResult;

import java.util.Map;

public interface ElcmAnalysisService {

    public WebResult malAnalysisIndex(Map<String, Object> map);
    public WebResult malCountByCycle(Map<String, Object> map);

}
