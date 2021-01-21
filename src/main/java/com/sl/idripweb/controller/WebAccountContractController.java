package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("webAccountContract")
public class WebAccountContractController {

    @Autowired
    private WebAccountContractService contractService;

    //##########################合同台账############################
    /**
     * 获取合同台账列表数据
     * @param map
     * @return
     */
    @GetMapping("getContractList")
    public WebResult getContractList(@RequestAttribute Map<String,Object> map){
        return contractService.getContractList(map);
    }

    /**
     * 供应商台账合作项目列表
     * @param map
     * @return
     */
    @GetMapping("getContractListSupplier")
    public WebResult getContractListSupplier(@RequestAttribute Map<String,Object> map){
        return contractService.getContractListSupplier(map);
    }

    /**
     * 通过id获取合同台账
     * @param map
     * @return
     */
    @GetMapping("getContractById")
    public WebResult getContractById(@RequestAttribute Map<String,Object> map){
        return contractService.getContractById(map);
    }

    /**
     * 新增合同台账
     * @param map
     * @return
     */
    @PostMapping("addContract")
    public WebResult addContract(@RequestAttribute Map<String,Object> map){
        return contractService.addContract(map);
    }

    /**
     * 编辑合同台账
     * @param map
     * @return
     */
    @PostMapping("updateContract")
    public WebResult updateContract(@RequestAttribute Map<String,Object> map){
        return contractService.updateContract(map);
    }

    /**
     * 删除合同台账
     * @param map
     * @return
     */
    @PostMapping("deleteContract")
    public WebResult deleteContract(@RequestAttribute Map<String,Object> map){
        return contractService.deleteContract(map);
    }

    /**
     * 通过合同id获取提醒事项
     * @param map
     * @return
     */
    @GetMapping("getRemindersByContractId")
    public WebResult getRemindersByContractId(@RequestAttribute Map<String,Object> map){
        return contractService.getRemindersByContractId(map);
    }

    /**
     * 新增合同提醒事项
     * @param map
     * @return
     */
    @PostMapping("addContractReminders")
    public WebResult addContractReminders(@RequestAttribute Map<String,Object> map){
        return contractService.addContractReminders(map);
    }

    /**
     * 编辑合同提醒事项
     * @param map
     * @return
     */
    @PostMapping("updateContractReminders")
    public WebResult updateContractReminders(@RequestAttribute Map<String,Object> map){
        return contractService.updateContractReminders(map);
    }




    //##########################数据统计############################
    /**
     * 数据统计页面大接口
     * @param map
     * @return
     */
    @PostMapping("statistics")
    public WebResult statistics(@RequestAttribute Map<String,Object> map){
        return contractService.statistics(map);
    }

}
