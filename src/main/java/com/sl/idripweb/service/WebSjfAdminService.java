package com.sl.idripweb.service;

import com.sl.common.utils.WebResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface WebSjfAdminService {

    public WebResult getFactoryBuilding(Map<String, Object> map);
    public WebResult getElecMeterList(Map<String, Object> map);
    public void downloadMeter(Map<String, Object> map, HttpServletResponse response)throws Exception;
    public WebResult importMeter(Map<String, Object> map, MultipartFile file);
    public WebResult addMeter(Map<String, Object> map);
    public WebResult deleteMeter(Map<String, Object> map);
    public WebResult getMeter(Map<String, Object> map);
    public WebResult updateMeter(Map<String, Object> map);



    public WebResult houseType(Map<String, Object> map);
    public WebResult getHouseList(Map<String, Object> map);
    public WebResult addHouse(Map<String, Object> map);
    public WebResult getHouseDetail(Map<String, Object> map);
    public WebResult updateHouse(Map<String, Object> map);
    public WebResult supplementFees(Map<String, Object> map);
    public void downloadHouse(Map<String, Object> map,HttpServletResponse response)throws Exception;
    public WebResult importHouse(Map<String, Object> map, MultipartFile file);
    public WebResult getHouseNumber(Map<String, Object> map);
    public WebResult getElecMeterNumber(Map<String, Object> map);
    public WebResult deleteHouse(Map<String, Object> map);



    public WebResult getPaymentRecord(Map<String, Object> map);
    public WebResult getAllPaymentRecord(Map<String, Object> map);
    public void downloadPayment(Map<String, Object> map,HttpServletResponse response)throws Exception;


    public WebResult getAllTypeList(Map<String, Object> map);
    public WebResult getChargeUnit(Map<String, Object> map);
    public WebResult addHouseType(Map<String, Object> map);
    public WebResult getHouseTypeDetail(Map<String, Object> map);
    public WebResult updateHouseType(Map<String, Object> map);
    public WebResult getFpgAndLadder(Map<String, Object> map);
    public WebResult updateFpg(Map<String, Object> map);
    public WebResult deleteLadderFuture(Map<String, Object> map);
    public WebResult deleteFpgFuture(Map<String, Object> map);
    public WebResult updateLadder(Map<String, Object> map);
    public WebResult deleteHouseType(Map<String, Object> map);

}
