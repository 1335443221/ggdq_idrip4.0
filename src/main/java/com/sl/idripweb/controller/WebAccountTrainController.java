package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountTrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("webAccountTrain")
public class WebAccountTrainController {

    @Autowired
    private WebAccountTrainService trainService;

    //##########################培训管理############################
    /**
     * 获取培训台账列表数据
     * @param map
     * @return
     */
    @GetMapping("getTrainList")
    public WebResult getTrainList(@RequestAttribute Map<String,Object> map){
        return trainService.getTrainList(map);
    }

    /**
     * 通过id获取培训台账
     * @param map
     * @return
     */
    @GetMapping("getTrainById")
    public WebResult getTrainById(@RequestAttribute Map<String,Object> map){
        return trainService.getTrainById(map);
    }

    /**
     * 新增培训台账
     * @param map
     * @return
     */
    @PostMapping("addTrain")
    public WebResult addTrain(@RequestAttribute Map<String,Object> map){
        return trainService.addTrain(map);
    }

    /**
     * 编辑培训台账
     * @param map
     * @return
     */
    @PostMapping("updateTrain")
    public WebResult updateTrain(@RequestAttribute Map<String,Object> map){
        return trainService.updateTrain(map);
    }

    /**
     * 删除培训台账
     * @param map
     * @return
     */
    @PostMapping("deleteTrain")
    public WebResult deleteTrain(@RequestAttribute Map<String,Object> map){
        return trainService.deleteTrain(map);
    }

    /**
     * 获取所有培训内容
     * @param map
     * @return
     */
    @GetMapping("getTrainContents")
    public WebResult getTrainContents(@RequestAttribute Map<String,Object> map){
        return trainService.getTrainContents(map);
    }



    //##########################培训单############################
    /**
     * 获取培训单列表数据
     * @param map
     * @return
     */
    @GetMapping("getTrainSheetList")
    public WebResult getTrainSheetList(@RequestAttribute Map<String,Object> map){
        return trainService.getTrainSheetList(map);
    }

    /**
     * 通过id获取单个培训单
     * @param map
     * @return
     */
    @GetMapping("getTrainSheetById")
    public WebResult getTrainSheetById(@RequestAttribute Map<String,Object> map){
        return trainService.getTrainSheetById(map);
    }

    /**
     * 新增培训单
     * @param map
     * @return
     */
    @PostMapping("addTrainSheet")
    public WebResult addTrainSheet(@RequestAttribute Map<String,Object> map){
        return trainService.addTrainSheet(map);
    }

    /**
     * 编辑培训单
     * @param map
     * @return
     */
    @PostMapping("updateTrainSheet")
    public WebResult updateTrainSheet(@RequestAttribute Map<String,Object> map){
        return trainService.updateTrainSheet(map);
    }

    /**
     * 删除培训单
     * @param map
     * @return
     */
    @PostMapping("deleteTrainSheet")
    public WebResult deleteTrainSheet(@RequestAttribute Map<String,Object> map){
        return trainService.deleteTrainSheet(map);
    }




    //##########################数据统计############################
    /**
     * 数据统计页面大接口
     * @param map
     * @return
     */
    @PostMapping("statistics")
    public WebResult statistics(@RequestAttribute Map<String,Object> map){
        return trainService.statistics(map);
    }
}
