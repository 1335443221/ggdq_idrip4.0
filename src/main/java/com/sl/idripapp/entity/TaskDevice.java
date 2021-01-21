package com.sl.idripapp.entity;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/11/25 14:32
 * FileName: TaskDevice
 * Description: ${DESCRIPTION}
 */
public class TaskDevice {

    String deviceName;  //设备名称
    String planStartTime;  //任务开始时间
    String planEndTime;  //任务结束时间
    String reportTime;  //保修时间
    String urgency;  //紧急程度
    String executor;    //执行人

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }
}
