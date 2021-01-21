package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountTrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("accountTrain")
public class AccountTrainController {

    @Autowired
    private AccountTrainService trainService;

    /**
     * 获取培训信息
     * @param map
     * @return
     */
    @GetMapping("getTrainList")
    public AppResult getTrainList(@RequestParam Map<String,Object> map){
        return trainService.getTrainList(map);
    }

    /**
     * 获取培训相关文件
     * @param map
     * @return
     */
    @GetMapping("getTrainSheetFiles")
    public AppResult getTrainSheetFiles(@RequestParam Map<String,Object> map){
        return trainService.getTrainSheetFiles(map);
    }

    /**
     * 获取参与人员
     * @param map
     * @return
     */
    @GetMapping("getAttendUser")
    public AppResult getAttendUser(@RequestParam Map<String,Object> map){
        return trainService.getAttendUser(map);
    }

    /**
     * 获取参与人员详情
     * @param map
     * @return
     */
    @GetMapping("getAttendUserDetail")
    public AppResult getAttendUserDetail(@RequestParam Map<String,Object> map){
        return trainService.getAttendUserDetail(map);
    }
}
