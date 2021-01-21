package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("accountContract")
public class AccountContractController {

    @Autowired
    private AccountContractService contractService;

    /**
     * 获取合同信息
     * @param map
     * @return
     */
    @GetMapping("getContractList")
    public AppResult getContractList(@RequestParam Map<String,Object> map){
        return contractService.getContractList(map);
    }

    /**
     * 获取id获取合同详情
     * @param map
     * @return
     */
    @GetMapping("getContractById")
    public AppResult getContractById(@RequestParam Map<String,Object> map){
        return contractService.getContractById(map);
    }

    /**
     * 修改合同信息
     * @param map
     * @return
     */
    @PostMapping("updateContract")
    public AppResult updateContract(@RequestBody Map<String,Object> map){
        return contractService.updateContract(map);
    }

}
