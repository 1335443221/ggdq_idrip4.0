package com.sl.idripapp.entity;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/11/25 14:32
 * FileName: TaskPark
 * Description: ${DESCRIPTION}
 */
public class TaskPark {
    int id;  //id
    int status;  //状态
    String statusName;  //状态名称
    int taskType;    //任务类型  保养/维修/巡检/id
    String taskTypeName;    //任务类型  保养/维修/巡检/
    int module;    //app模块   1设备   2电气火灾
    TaskDevice taskDevice;  //设备模块  任务
    TaskFire taskFire;   //火灾模块  任务


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public TaskDevice getTaskDevice() {
        return taskDevice;
    }

    public void setTaskDevice(TaskDevice taskDevice) {
        this.taskDevice = taskDevice;
    }

    public TaskFire getTaskFire() {
        return taskFire;
    }

    public void setTaskFire(TaskFire taskFire) {
        this.taskFire = taskFire;
    }
}
