package com.sl.idripweb.controller;

import com.sl.idripweb.service.ElcmTaskService;
import com.sl.common.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/elcmTask")
public class ElcmTaskController {
	@Autowired
    ElcmTaskService elcmTaskService;

    /**
     * 维保计划状态
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getTaskStatus")
    @ResponseBody
    public WebResult getTaskStatus(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getTaskStatus(map);
    }
    /**
     * 维保计划类型
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getTaskType")
    @ResponseBody
    public WebResult getTaskType(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getTaskType(map);
    }
    /**
     * 新增维保计划
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/addTask")
    @ResponseBody
    public WebResult addTask(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.addTask(map);
    }

    /**
     * 维保计划列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getTaskList")
    @ResponseBody
    public WebResult getTaskList(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getTaskList(map);
    }


    /**
     * 撤销申请
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/revokeTask")
    @ResponseBody
    public WebResult revokeTask(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.revokeTask(map);
    }


    /**
     * 修改申请
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/updateTask")
    @ResponseBody
    public WebResult updateTask(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.updateTask(map);
    }

    /**
     * 删除申请
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/deleteTask")
    @ResponseBody
    public WebResult deleteTask(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.deleteTask(map);
    }


    /**
     * 计划详情
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getTaskDetail")
    @ResponseBody
    public WebResult getTaskDetail(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getTaskDetail(map);
    }


    /**
     * 开启/关闭
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/startOrStop")
    @ResponseBody
    public WebResult startOrStop(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.startOrStop(map);
    }



    /**
     * 设备/部位/关系树
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getDeviceMaintainRelation")
    @ResponseBody
    public WebResult getDeviceMaintainRelation(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getDeviceMaintainRelation(map);
    }


    //========================维保工单=====================//


    /**
     * 工单状态
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getRecordStatus")
    @ResponseBody
    public WebResult getRecordStatus(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getRecordStatus(map);
    }


    /**
     * 工单列表
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getRecordList")
    @ResponseBody
    public WebResult getRecordList(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getRecordList(map);
    }


    /**
     * 分配人员
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/assignUser")
    @ResponseBody
    public WebResult assignUser(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.assignUser(map);
    }




    /**
     * 分配提醒时间
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/assignRemindTime")
    @ResponseBody
    public WebResult assignRemindTime(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.assignRemindTime(map);
    }



    /**
     * 调整包养时间
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/updateStartTime")
    @ResponseBody
    public WebResult updateStartTime(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.updateStartTime(map);
    }



    /**
     * 工单详情
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getRecordDetail")
    @ResponseBody
    public WebResult getRecordDetail(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getRecordDetail(map);
    }
    /**
     * 工单详情
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getRecordByIds")
    @ResponseBody
    public WebResult getRecordByIds(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getRecordByIds(map);
    }



    /**
     * 我的工单
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/getMyRecordList")
    @ResponseBody
    public WebResult getMyRecordList(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.getMyRecordList(map);
    }



    /**
     * 接单
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/receiveRecord")
    @ResponseBody
    public WebResult receiveRecord(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.receiveRecord(map);
    }




    /**
     * 退单
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/chargeback")
    @ResponseBody
    public WebResult chargeback(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.chargeback(map);
    }

    /**
     * 转派
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/transfer")
    @ResponseBody
    public WebResult transfer(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.transfer(map);
    }

    /**
     * 解决
     * @param
     * @param map
     * @return
     */
    @RequestMapping("/complete")
    @ResponseBody
    public WebResult complete(@RequestAttribute Map<String,Object> map){
        return  elcmTaskService.complete(map);
    }








}
