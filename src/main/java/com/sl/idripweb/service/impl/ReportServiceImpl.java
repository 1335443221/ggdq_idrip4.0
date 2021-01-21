package com.sl.idripweb.service.impl;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.dao.ReportDao;
import com.sl.idripweb.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("reportService")
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDao reportDao;

    @Override
    public WebResult selectByDate(HttpServletRequest request, Map<String, Object> map) {
        int sheetNo = Integer.parseInt((String)map.get("sheetNo"));
        String startDate = (String)map.get("startDate");
        String endDate = map.get("endDate")==null?startDate:(String)map.get("endDate");
        List<String> dateList = getDateList(startDate,endDate);
        List<Map<String,Object>> result = new ArrayList<>();

        switch (sheetNo){
            case 0:
                result = reportDao.selectTemperatureByDate(dateList);
                break;
            case 1:
                result = reportDao.selectPressureByDate(dateList);
                break;
            case 2:
                result = reportDao.selectWaterByDate(dateList);
                break;
            case 3:
                //String water_number = (String)map.get("water_number");
                result = reportDao.selectWaterPumpRunningByDate(dateList);
                break;
            case 4:
                result = reportDao.selectRunningDataByDate(dateList);
        }
        return WebResult.success(result);
    }


    public List<String> getDateList(String startDate,String endDate){
        List<String> dateList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                dateList.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateList;
    }


}
