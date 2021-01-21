package com.sl.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/9/30 15:48
 * FileName: ConstantConfig
 * Description: 配置文件
 */
@Validated
@ConfigurationProperties(prefix = "constant")
@Service
@Data
public class ConstantConfig {
    public  String  environment;
    public  String  alermLevel;
    public  String  downloadUrl;
    public  String  jiGaungEnv;
    public  String  oveSession;
    public  String  xuansiIp;
    public  String  fileUploadUrl;
    public  String  fileDownloadUrl;

    public String sjfClosingTag;
    public String sjfOpeningTag;
    public String sjfTimeTag;
    public String sjfDayTag;
    public String sjfEpTag;
    public String sjfFTag;
    public String sjfPTag;
    public String sjfGTag;

    public String elecMeter;
    public String deviceTagList;
    public String qiniuAccessKey;
    public String qiniuSecretKey;
    public String qiniuBucketName;
    public String qiniuUrl;


    public String getElecMeter() {
        return elecMeter;
    }

    public void setElecMeter(String elecMeter) {
        this.elecMeter = elecMeter;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getAlermLevel() {
        return alermLevel;
    }

    public void setAlermLevel(String alermLevel) {
        this.alermLevel = alermLevel;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getJiGaungEnv() {
        return jiGaungEnv;
    }

    public void setJiGaungEnv(String jiGaungEnv) {
        this.jiGaungEnv = jiGaungEnv;
    }

    public String getOveSession() {
        return oveSession;
    }

    public void setOveSession(String oveSession) {
        this.oveSession = oveSession;
    }

    public String getXuansiIp() {
        return xuansiIp;
    }

    public void setXuansiIp(String xuansiIp) {
        this.xuansiIp = xuansiIp;
    }

    public String getSjfClosingTag() {
        return sjfClosingTag;
    }

    public void setSjfClosingTag(String sjfClosingTag) {
        this.sjfClosingTag = sjfClosingTag;
    }

    public String getSjfOpeningTag() {
        return sjfOpeningTag;
    }

    public void setSjfOpeningTag(String sjfOpeningTag) {
        this.sjfOpeningTag = sjfOpeningTag;
    }

    public String getSjfTimeTag() {
        return sjfTimeTag;
    }

    public void setSjfTimeTag(String sjfTimeTag) {
        this.sjfTimeTag = sjfTimeTag;
    }

    public String getSjfDayTag() {
        return sjfDayTag;
    }

    public void setSjfDayTag(String sjfDayTag) {
        this.sjfDayTag = sjfDayTag;
    }

    public String getSjfEpTag() {
        return sjfEpTag;
    }

    public void setSjfEpTag(String sjfEpTag) {
        this.sjfEpTag = sjfEpTag;
    }

    public String getSjfFTag() {
        return sjfFTag;
    }

    public void setSjfFTag(String sjfFTag) {
        this.sjfFTag = sjfFTag;
    }

    public String getSjfPTag() {
        return sjfPTag;
    }

    public void setSjfPTag(String sjfPTag) {
        this.sjfPTag = sjfPTag;
    }

    public String getSjfGTag() {
        return sjfGTag;
    }

    public void setSjfGTag(String sjfGTag) {
        this.sjfGTag = sjfGTag;
    }

    public String getFileUploadUrl() {
        return fileUploadUrl;
    }

    public void setFileUploadUrl(String fileUploadUrl) {
        this.fileUploadUrl = fileUploadUrl;
    }

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }

    public String getDeviceTagList() {
        return deviceTagList;
    }

    public void setDeviceTagList(String deviceTagList) {
        this.deviceTagList = deviceTagList;
    }

    public String getQiniuAccessKey() {
        return qiniuAccessKey;
    }

    public void setQiniuAccessKey(String qiniuAccessKey) {
        this.qiniuAccessKey = qiniuAccessKey;
    }

    public String getQiniuSecretKey() {
        return qiniuSecretKey;
    }

    public void setQiniuSecretKey(String qiniuSecretKey) {
        this.qiniuSecretKey = qiniuSecretKey;
    }

    public String getQiniuBucketName() {
        return qiniuBucketName;
    }

    public void setQiniuBucketName(String qiniuBucketName) {
        this.qiniuBucketName = qiniuBucketName;
    }

    public String getQiniuUrl() {
        return qiniuUrl;
    }

    public void setQiniuUrl(String qiniuUrl) {
        this.qiniuUrl = qiniuUrl;
    }
}
