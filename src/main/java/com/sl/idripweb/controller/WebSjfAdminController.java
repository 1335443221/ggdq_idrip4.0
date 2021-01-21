package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebSjfAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/webSjfAdmin")
public class WebSjfAdminController {
	@Autowired
	WebSjfAdminService webSjfAdminService;


	/**
	 * 园区-建筑接口
	 * @param
	 * @param map
	 * @return
	 */
	@RequestMapping("/getFactory")
	@ResponseBody
	public WebResult getFactoryBuilding(@RequestAttribute Map<String,Object> map){
		return webSjfAdminService.getFactoryBuilding(map);
	}

    /**
     * 园区-电表列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getElecMeterList")
    @ResponseBody
    public WebResult getElecMeterList(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.getElecMeterList(map);
    }

    /**
     * 园区-下载模板
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/downloadMeter")
    @ResponseBody
    public void downloadMeter(@RequestAttribute Map<String,Object> map, HttpServletResponse response)throws Exception{
       webSjfAdminService.downloadMeter(map,response);
    }

    /**
     * 园区-导入电表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/importMeter")
    @ResponseBody
    public WebResult importMeter(@RequestAttribute Map<String,Object> map, MultipartFile file){
        return  webSjfAdminService.importMeter(map,file);
    }
    /**
     * 园区-删除电表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/deleteMeter")
    @ResponseBody
    public WebResult deleteMeter(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.deleteMeter(map);
    }

    /**
     * 园区-增加电表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/addMeter")
    @ResponseBody
    public WebResult addMeter(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.addMeter(map);
    }
    /**
     * 园区-获取电表信息
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getMeter")
    @ResponseBody
    public WebResult getMeter(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.getMeter(map);
    }

    /**
     * 修改电表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/updateMeter")
    @ResponseBody
    public WebResult updateMeter(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.updateMeter(map);
    }
//=================

    /**
     * 园区-用户类型列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/houseType")
    @ResponseBody
    public WebResult houseType(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.houseType(map);
    }

    /**
     * 分戶列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getHouseList")
    @ResponseBody
    public WebResult getHouseList(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.getHouseList(map);
    }
    /**
     * 新增分戶
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/addHouse")
    @ResponseBody
    public WebResult addHouse(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.addHouse(map);
    }


    /**
     * 分戶详情
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getHouseDetail")
    @ResponseBody
    public WebResult getHouseDetail(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.getHouseDetail(map);
    }

    /**
     * 修改分戶
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/updateHouse")
    @ResponseBody
    public WebResult updateHouse(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.updateHouse(map);
    }
    /**
     * 补加电费
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/supplementFees")
    @ResponseBody
    public WebResult supplementFees(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.supplementFees(map);
    }


    /**
     * 下载分户的模板
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/downloadHouse")
    @ResponseBody
    public void downloadHouse(@RequestAttribute Map<String,Object> map,HttpServletResponse response)throws Exception{
        webSjfAdminService.downloadHouse(map,response);
    }


    /**
     * 园区-导入分户电表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/importHouse")
    @ResponseBody
    public WebResult importHouse(@RequestAttribute Map<String,Object> map, MultipartFile file){
        return  webSjfAdminService.importHouse(map,file);
    }
    /**
     * 园区-获取户号
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getHouseNumber")
    @ResponseBody
    public WebResult getHouseNumber(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.getHouseNumber(map);
    }
    /**
     * 园区-获取电表编号
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getElecMeterNumber")
    @ResponseBody
    public WebResult getElecMeterNumber(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.getElecMeterNumber(map);
    }

    /**
     * 分户删除
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/deleteHouse")
    @ResponseBody
    public WebResult deleteHouse(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.deleteHouse(map);
    }
//=================================

    /**
     * 支付方式
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getPaymentType")
    @ResponseBody
    public WebResult getPaymentType(@RequestAttribute Map<String,Object> map){
        List<String> list=new ArrayList<>();
        list.add("支付宝支付");
        list.add("微信支付");
        list.add("补加电费");
        return  WebResult.success(list);
    }

    /**
     * 缴费记录
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getPaymentRecord")
    @ResponseBody
    public WebResult getPaymentRecord(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.getPaymentRecord(map);
    }
    /**
     * 缴费记录
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getAllPaymentRecord")
    @ResponseBody
    public WebResult getAllPaymentRecord(@RequestAttribute Map<String,Object> map){
        return  webSjfAdminService.getAllPaymentRecord(map);
    }


    /**
     * 下载
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/downloadPayment")
    @ResponseBody
    public void downloadPayment(@RequestAttribute Map<String,Object> map,HttpServletResponse response)throws Exception{
        webSjfAdminService.downloadPayment(map,response);
    }

//=================================费率设置============================//

    /**
     * 所有分类列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getAllTypeList")
    @ResponseBody
    public WebResult getAllTypeList(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.getAllTypeList(map);
    }

    /**
     * 缴费-获取收费单位
     * @param
     * @return
     */
    @RequestMapping("/getChargeUnit")
    @ResponseBody
    public WebResult getChargeUnit(@RequestAttribute Map<String, Object> map){
        return webSjfAdminService.getChargeUnit(map);
    }


    /**
     * 新增收费方式
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/addHouseType")
    @ResponseBody
    public WebResult addHouseType(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.addHouseType(map);
    }


    /**
     * 获取收费方式详情
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getHouseTypeDetail")
    @ResponseBody
    public WebResult getHouseTypeDetail(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.getHouseTypeDetail(map);
    }


    /**
     * 修改收费方式
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/updateHouseType")
    @ResponseBody
    public WebResult updateHouseType(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.updateHouseType(map);
    }

    /**
     * 获取峰平谷和阶梯
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getFpgAndLadder")
    @ResponseBody
    public WebResult getFpgAndLadder(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.getFpgAndLadder(map);
    }
    /**
     * 峰平谷
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/updateFpg")
    @ResponseBody
    public WebResult updateFpg(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.updateFpg(map);
    }

    /**
     *电费设置-删除峰平谷待生效
     * @param
     * @return
     */
    @RequestMapping("/deleteFpgFuture")
    @ResponseBody
    public WebResult deleteFpgFuture(@RequestAttribute Map<String, Object> map){
        return webSjfAdminService.deleteFpgFuture(map);
    }

    /**
     *电费设置-删除阶梯待生效
     * @param
     * @return
     */
    @RequestMapping("/deleteLadderFuture")
    @ResponseBody
    public WebResult deleteLadderFuture(@RequestAttribute Map<String, Object> map){
        return webSjfAdminService.deleteLadderFuture(map);
    }

    /**
     * 阶梯
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/updateLadder")
    @ResponseBody
    public WebResult updateLadder(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.updateLadder(map);
    }
    /**
     * 阶梯
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/deleteHouseType")
    @ResponseBody
    public WebResult deleteHouseType(@RequestAttribute Map<String,Object> map){
        return webSjfAdminService.deleteHouseType(map);
    }




}
