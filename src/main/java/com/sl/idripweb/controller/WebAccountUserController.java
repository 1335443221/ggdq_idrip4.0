package com.sl.idripweb.controller;

import com.sl.common.utils.WebResult;
import com.sl.idripweb.service.WebAccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webAccountUser")
public class WebAccountUserController {

    @Autowired
    private WebAccountUserService userService;


    //#####################人员台账###############################
    /**
     * 获取所有公司
     * @param map
     * @return
     */
    @GetMapping("getAllCompany")
    public WebResult getAllCompany(@RequestAttribute Map<String,Object> map){
        return userService.getAllCompany(map);
    }

    /**
     * 获取所有部门
     * @param map
     * @return
     */
    @GetMapping("getAllDepartment")
    public WebResult getAllDepartment(@RequestAttribute Map<String,Object> map){
        return userService.getAllDepartment(map);
    }

    /**
     * 获取所有角色
     * @param map
     * @return
     */
    @GetMapping("getAllRoles")
    public WebResult getAllRoles(@RequestAttribute Map<String,Object> map){
        return userService.getAllRoles(map);
    }

    /**
     * 获取所有标签
     * @param map
     * @return
     */
    @GetMapping("getAllLabel")
    public WebResult getAllLabel(@RequestAttribute Map<String,Object> map){
        return userService.getAllLabel(map);
    }

    /**
     * 标签新增
     * @param map
     * @return
     */
    @PostMapping("addLabel")
    public WebResult addLabel(@RequestAttribute Map<String,Object> map){
        return userService.addLabel(map);
    }

    /**
     * 标签删除
     * @param map
     * @return
     */
    @GetMapping("deleteLabel")
    public WebResult deleteLabel(@RequestAttribute Map<String,Object> map){
        return userService.deleteLabel(map);
    }

    /**
     * 标签验证是否被人员使用
     * @param map
     * @return
     */
    @GetMapping("checkLabel")
    public WebResult checkLabel(@RequestAttribute Map<String,Object> map){
        return userService.checkLabel(map);
    }

    /**
     * 给人员关联标签
     * @param map
     * @return
     */
    @PostMapping("relationLabel")
    public WebResult relationLabel(@RequestAttribute Map<String,Object> map){
        return userService.relationLabel(map);
    }

    /**
     * 获取人员台账列表数据
     * @param map
     * @return
     */
    @PostMapping("getUserList")
    public WebResult getUserList(@RequestAttribute Map<String,Object> map){
        return userService.getUserList(map);
    }

    /**
     * 维保台账维保人员列表
     * @param map
     * @return
     */
    @PostMapping("getMaintenanceUserList")
    public WebResult getMaintenanceUserList(@RequestAttribute Map<String,Object> map){
        return userService.getMaintenanceUserList(map);
    }

    /**
     * 获取人员台账详情数据
     * @param map
     * @return
     */
    @PostMapping("getUserDetail")
    public WebResult getUserDetail(@RequestAttribute Map<String,Object> map){
        return userService.getUserDetail(map);
    }

    /**
     * 人员台账新增
     * @param map
     * @return
     */
    @PostMapping("addUser")
    public WebResult addUser(@RequestAttribute Map<String,Object> map){
        return userService.addUser(map);
    }

    /**
     * 人员台账删除
     * @param map
     * @return
     */
    @PostMapping("deleteUser")
    public WebResult deleteUser(@RequestAttribute Map<String,Object> map){
        return userService.deleteUser(map);
    }

    /**
     * 人员台账编辑
     * @param map
     * @return
     */
    @PostMapping("updateUser")
    public WebResult updateUser(@RequestAttribute Map<String,Object> map){
        return userService.updateUser(map);
    }


    //#####################提醒事项###############################
    /**
     * 提醒事项列表查询
     * @param map
     * @return
     */
    @PostMapping("getReminderList")
    public WebResult getReminderList(@RequestAttribute Map<String,Object> map){
        return userService.getReminderList(map);
    }

    /**
     * 根据id获取单个提醒事项
     * @param map
     * @return
     */
    @PostMapping("getReminderById")
    public WebResult getReminderById(@RequestAttribute Map<String,Object> map){
        return userService.getReminderById(map);
    }

    /**
     * 获取公司部门下的人员
     * @param map
     * @return
     */
    @PostMapping("getReminderUsers")
    public WebResult getReminderUsers(@RequestAttribute Map<String,Object> map){
        return userService.getReminderUsers(map);
    }

    /**
     * 新增提醒事项
     * @param map
     * @return
     */
    @PostMapping("addReminder")
    public WebResult addReminder(@RequestAttribute Map<String,Object> map){
        return userService.addReminder(map);
    }

    /**
     * 修改提醒事项
     * @param map
     * @return
     */
    @PostMapping("updateReminder")
    public WebResult updateReminder(@RequestAttribute Map<String,Object> map){
        return userService.updateReminder(map);
    }

    /**
     * 删除提醒事项
     * @param map
     * @return
     */
    @PostMapping("deleteReminder")
    public WebResult deleteReminder(@RequestAttribute Map<String,Object> map){
        return userService.deleteReminder(map);
    }



    //#####################数据统计###############################
    /**
     * 数据统计页面大接口
     * @param map
     * @return
     */
    @PostMapping("statistics")
    public WebResult statistics(@RequestAttribute Map<String,Object> map){
        return userService.statistics(map);
    }

}
