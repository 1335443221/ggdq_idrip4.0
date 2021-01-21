package com.sl.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;

@Configuration
@PropertySource(value = {"classpath:account-dictionary.properties"}, encoding = "utf-8")
@ConfigurationProperties(prefix = "account")
public class AccountDictionary {

    @Value("#{'${account.userNation}'.split(',')}")
    private List<String> userNation;
    @Value("#{'${account.userType}'.split(',')}")
    private List<String> userType;
    @Value("#{'${account.userPoliticalOutlook}'.split(',')}")
    private List<String> userPoliticalOutlook;
    @Value("#{'${account.userMaritalStatus}'.split(',')}")
    private List<String> userMaritalStatus;
    @Value("#{'${account.userHighestEducation}'.split(',')}")
    private List<String> userHighestEducation;
    @Value("#{'${account.trainAchievement}'.split(',')}")
    private List<String> trainAchievement;
    @Value("#{'${account.contractType}'.split(',')}")
    private List<String> contractType;
    @Value("#{'${account.remindDate}'.split(',')}")
    private List<String> remindDate;
    @Value("#{'${account.remindFrequency}'.split(',')}")
    private List<String> remindFrequency;
    @Value("#{'${account.creditRating}'.split(',')}")
    private List<String> creditRating;

    public List<String> getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(List<String> creditRating) {
        this.creditRating = creditRating;
    }

    public List<String> getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(List<String> remindDate) {
        this.remindDate = remindDate;
    }

    public List<String> getRemindFrequency() {
        return remindFrequency;
    }

    public void setRemindFrequency(List<String> remindFrequency) {
        this.remindFrequency = remindFrequency;
    }

    public List<String> getContractType() {
        return contractType;
    }

    public void setContractType(List<String> contractType) {
        this.contractType = contractType;
    }

    public List<String> getUserNation() {
        return userNation;
    }

    public void setUserNation(List<String> userNation) {
        this.userNation = userNation;
    }

    public List<String> getUserType() {
        return userType;
    }

    public void setUserType(List<String> userType) {
        this.userType = userType;
    }

    public List<String> getUserPoliticalOutlook() {
        return userPoliticalOutlook;
    }

    public void setUserPoliticalOutlook(List<String> userPoliticalOutlook) {
        this.userPoliticalOutlook = userPoliticalOutlook;
    }

    public List<String> getUserMaritalStatus() {
        return userMaritalStatus;
    }

    public void setUserMaritalStatus(List<String> userMaritalStatus) {
        this.userMaritalStatus = userMaritalStatus;
    }

    public List<String> getUserHighestEducation() {
        return userHighestEducation;
    }

    public void setUserHighestEducation(List<String> userHighestEducation) {
        this.userHighestEducation = userHighestEducation;
    }

    public List<String> getTrainAchievement() {
        return trainAchievement;
    }

    public void setTrainAchievement(List<String> trainAchievement) {
        this.trainAchievement = trainAchievement;
    }
}
