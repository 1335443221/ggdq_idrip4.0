package com.sl.idripapp.controller;

import com.sl.common.utils.AppResult;
import com.sl.idripapp.service.AccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accountUser")
public class AccountUserController {

    @Autowired
    private AccountUserService userService;


    //#####################人员台账###############################
    /**
     * 获取所有公司
     * @param map
     * @return
     */
    @GetMapping("getAllCompany")
    public AppResult getAllCompany(@RequestParam Map<String,Object> map){
        return userService.getAllCompany(map);
    }

    /**
     * 获取所有部门
     * @param map
     * @return
     */
    @GetMapping("getAllDepartment")
    public AppResult getAllDepartment(@RequestParam Map<String,Object> map){
        return userService.getAllDepartment(map);
    }

    /**
     * 获取所有角色
     * @param map
     * @return
     */
    @GetMapping("getAllRoles")
    public AppResult getAllRoles(@RequestParam Map<String,Object> map){
        return userService.getAllRoles(map);
    }

    /**
     * 获取人员台账列表数据
     * @param map
     * @return
     */
    @PostMapping("getUserList")
    public AppResult getUserList(@RequestParam Map<String,Object> map){
        return userService.getUserList(map);
    }

    /**
     * 获取人员台账详情数据
     * @param map
     * @return
     */
    @PostMapping("getUserDetail")
    public AppResult getUserDetail(@RequestParam Map<String,Object> map){
        return userService.getUserDetail(map);
    }

    /**
     * 人员台账编辑
     * @param map
     * @return
     */
    @PostMapping("updateUser")
    public AppResult updateUser(@RequestBody Map<String,Object> map){
        return userService.updateUser(map);
    }


    //#####################提醒事项###############################
    /**
     * 提醒事项列表查询
     * @param map
     * @return
     */
    @PostMapping("getReminderList")
    public AppResult getReminderList(@RequestParam Map<String,Object> map){
        return userService.getReminderList(map);
    }

    /**
     * 修改提醒事项
     * @param map
     * @return
     */
    @PostMapping("updateReminder")
    public AppResult updateReminder(@RequestParam Map<String,Object> map){
        return userService.updateReminder(map);
    }

    /**
     * 删除提醒事项
     * @param map
     * @return
     */
    @PostMapping("deleteReminder")
    public AppResult deleteReminder(@RequestParam Map<String,Object> map){
        return userService.deleteReminder(map);
    }


}
