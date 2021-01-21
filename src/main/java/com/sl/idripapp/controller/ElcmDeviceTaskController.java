package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.ElcmDeviceTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("deviceTask")
public class ElcmDeviceTaskController {

    @Autowired
    private ElcmDeviceTaskService deviceTaskService;

    /**
     * 获取即将开始任务数量
     * @param map
     * @return
     */
    @PostMapping("getAboutToStartCount")
    public AppResult getAboutToStartCount(@RequestParam Map<String, Object> map){
        return deviceTaskService.getAboutToStartCount(map);
    }

    /**
     * 任务中心列表
     * @param map
     * @return
     */
    @PostMapping("getTaskRecordList")
    public AppResult getTaskList(@RequestParam Map<String, Object> map){
        return deviceTaskService.getTaskList(map);
    }


    /**
     * 我的任务中心
     * @param map
     * @return
     */
    @PostMapping("getMyTaskRecordList")
    public AppResult getMyTaskRecordList(@RequestParam Map<String, Object> map){
        return deviceTaskService.getMyTaskRecordList(map);
    }



    /**
     * 任务状态
     * @param map
     * @return
     */
    @PostMapping("getTaskRecordStatus")
    public AppResult getTaskRecordStatus(@RequestParam Map<String, Object> map){
        return deviceTaskService.getTaskRecordStatus(map);
    }


    /**
     * 任务类型
     * @param map
     * @return
     */
    @PostMapping("getTaskRecordType")
    public AppResult getTaskRecordType(@RequestParam Map<String, Object> map){
        return deviceTaskService.getTaskRecordType(map);
    }

    /**
     * 根据任务id获取单个任务
     * @param map
     * @return
     */
    @PostMapping("getTaskDetail")
    public AppResult getTaskById(@RequestParam Map<String, Object> map){
        return deviceTaskService.getTaskById(map);
    }


    /**
     * 调整保养时间
     * @param map
     * @return
     */
    @PostMapping("updateStartTime")
    public AppResult updateStartTime(@RequestParam Map<String, Object> map){
        return deviceTaskService.updateStartTime(map);
    }



    /**
     * 分配人员
     * @param map
     * @return
     */
    @PostMapping("assignUser")
    public AppResult assignUser(@RequestParam Map<String, Object> map){
        return deviceTaskService.assignUser(map);
    }


        /**
         * 接单
         * @param map
         * @return
         */
        @PostMapping("receiveRecord")
        public AppResult receiveRecord(@RequestParam Map<String, Object> map){
            return deviceTaskService.receiveRecord(map);
        }


        /**
         * 退单
         * @param map
         * @return
         */
        @PostMapping("chargeback")
        public AppResult chargeback(@RequestParam Map<String, Object> map){
            return deviceTaskService.chargeback(map);
        }


        /**
         * 转派
         * @param map
         * @return
         */
        @PostMapping("transfer")
        public AppResult transfer(@RequestParam Map<String, Object> map){
            return deviceTaskService.transfer(map);
        }
    /**
     * 任务完成
     * @param map
     * @return
     */
    @PostMapping("complete")
    public AppResult completeTask(@RequestParam Map<String, Object> map){
        return deviceTaskService.completeTask(map);
    }



    /**
     * 备件列表
     * @param map
     * @return
     */
    @PostMapping("getSparepartsList")
    public AppResult getSparepartList(@RequestParam Map<String, Object> map){
        return deviceTaskService.getSparepartList(map);
    }

    /**
     * 备件申请记录
     * @param map
     * @return
     */
    @PostMapping("getSparepartsApplyList")
    public AppResult getSparepartApplyList(@RequestParam Map<String, Object> map){
        return deviceTaskService.getSparepartApplyList(map);
    }

    /**
     * 备件申请详情
     * @param map
     * @return
     */
    @PostMapping("getSparepartsApplyDetail")
    public AppResult getSparepartApplyDetail(@RequestParam Map<String, Object> map){
        return deviceTaskService.getSparepartApplyDetail(map);
    }
    /**
     * 备件使用情况-选择(新)
     * @param map
     * @return
     */
    @PostMapping("getSparepartsUseChoice")
    public AppResult getSparepartsUseChoice(@RequestParam Map<String, Object> map){
        return deviceTaskService.getSparepartsUseChoice(map);
    }
    /**
     * 备件使用情况-查看/选择
     * @param map
     * @return
     */
    @PostMapping("getSparepartsUseDetail")
    public AppResult getSparepartsUseDetail(@RequestParam Map<String, Object> map){
        return deviceTaskService.getSparepartsUseDetail(map);
    }



    /**
     * 备件申请
     * @param map
     * @return
     */
    @PostMapping("addSparepartsApply")
    public AppResult addSeparepartsApply(@RequestParam Map<String, Object> map){
        return deviceTaskService.addSeparepartsApply(map);
    }


    /**
     * 取消任务
     * @param map
     * @return
     */
    @PostMapping("revoke")
    public AppResult revoke(@RequestParam Map<String, Object> map){
        return deviceTaskService.revoke(map);
    }


    /**
     * 审批
     * @param map
     * @return
     */
    @PostMapping("approve")
    public AppResult approve(@RequestParam Map<String, Object> map){
        return deviceTaskService.approve(map);
    }

}
