package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("webAccountSupplier")
public class WebAccountSupplierController {

    @Autowired
    private WebAccountSupplierService supplierService;

    //##########################供应商台账############################
    /**
     * 获取供应商列表数据
     * @param map
     * @return
     */
    @GetMapping("getSupplierList")
    public WebResult getSupplierList(@RequestAttribute Map<String,Object> map){
        return supplierService.getSupplierList(map);
    }

    /**
     * 通过供应商id获取详情
     * @param map
     * @return
     */
    @GetMapping("getSupplierById")
    public WebResult getSupplierById(@RequestAttribute Map<String,Object> map){
        return supplierService.getSupplierById(map);
    }

    /**
     * 新增供应商
     * @param map
     * @return
     */
    @PostMapping("addSupplier")
    public WebResult addSupplier(@RequestAttribute Map<String,Object> map){
        return supplierService.addSupplier(map);
    }

    /**
     * 修改供应商
     * @param map
     * @return
     */
    @PostMapping("updateSupplier")
    public WebResult updateSupplier(@RequestAttribute Map<String,Object> map){
        return supplierService.updateSupplier(map);
    }

    /**
     * 批量删除供应商
     * @param map
     * @return
     */
    @GetMapping("deleteSupplier")
    public WebResult deleteSupplier(@RequestAttribute Map<String,Object> map){
        return supplierService.deleteSupplier(map);
    }

    /**
     * 加入黑名单
     * @param map
     * @return
     */
    @PostMapping("joinBlacklist")
    public WebResult joinBlacklist(@RequestAttribute Map<String,Object> map){
        return supplierService.joinBlacklist(map);
    }


    //##########################黑名单############################
    /**
     * 获取黑名单列表数据
     * @param map
     * @return
     */
    @GetMapping("getBlackList")
    public WebResult getBlackList(@RequestAttribute Map<String,Object> map){
        return supplierService.getBlackList(map);
    }

    /**
     * 黑名单批量恢复
     * @param map
     * @return
     */
    @GetMapping("restoreBlacks")
    public WebResult restoreBlacks(@RequestAttribute Map<String,Object> map){
        return supplierService.restoreBlacks(map);
    }




    //##########################数据统计############################
}
