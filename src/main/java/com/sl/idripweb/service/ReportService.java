package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ReportService {
    WebResult selectByDate(HttpServletRequest request, Map<String, Object> map);
}
