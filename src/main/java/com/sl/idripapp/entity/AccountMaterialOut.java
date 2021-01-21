package com.sl.idripapp.entity;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/11/25 14:32
 * FileName: TaskDevice
 * Description: ${DESCRIPTION}
 */
public class AccountMaterialOut {

    int id;
    String name;
    String qrCode;
    String receiveUser;
    String receiveTime;
    String receiveAmount;
    String examineUser;
    String destroyRecord;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser) {
        this.receiveUser = receiveUser;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(String receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public String getExamineUser() {
        return examineUser;
    }

    public void setExamineUser(String examineUser) {
        this.examineUser = examineUser;
    }

    public String getDestroyRecord() {
        return destroyRecord;
    }

    public void setDestroyRecord(String destroyRecord) {
        this.destroyRecord = destroyRecord;
    }
}
