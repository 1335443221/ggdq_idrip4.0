package com.sl.common.entity;

public class SjfYesterdayData {
    private int houseId;
    private int elecMeterId;
    private int chargeType;
    private float parityPrice;
    private float peakPrice;
    private float plainPrice;
    private float valleyPrice;
    private float secondPrice;
    private float thirdPrice;
    private float peak;
    private float plain;
    private float valley;
    private float power;
    private float firstLadder;
    private float secondLadder;
    private String codeName;
    private String tgName;
    private String checkInTime;

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public float getPlain() {
        return plain;
    }

    public void setPlain(float plain) {
        this.plain = plain;
    }

    public String getTgName() {
        return tgName;
    }

    public void setTgName(String tgName) {
        this.tgName = tgName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public int getElecMeterId() {
        return elecMeterId;
    }

    public void setElecMeterId(int elecMeterId) {
        this.elecMeterId = elecMeterId;
    }

    public int getChargeType() {
        return chargeType;
    }

    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
    }

    public float getParityPrice() {
        return parityPrice;
    }

    public void setParityPrice(float parityPrice) {
        this.parityPrice = parityPrice;
    }

    public float getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(float peakPrice) {
        this.peakPrice = peakPrice;
    }

    public float getPlainPrice() {
        return plainPrice;
    }

    public void setPlainPrice(float plainPrice) {
        this.plainPrice = plainPrice;
    }

    public float getValleyPrice() {
        return valleyPrice;
    }

    public void setValleyPrice(float valleyPrice) {
        this.valleyPrice = valleyPrice;
    }

    public float getSecondPrice() {
        return secondPrice;
    }

    public void setSecondPrice(float secondPrice) {
        this.secondPrice = secondPrice;
    }

    public float getThirdPrice() {
        return thirdPrice;
    }

    public void setThirdPrice(float thirdPrice) {
        this.thirdPrice = thirdPrice;
    }

    public float getPeak() {
        return peak;
    }

    public void setPeak(float peak) {
        this.peak = peak;
    }

    public float getValley() {
        return valley;
    }

    public void setValley(float valley) {
        this.valley = valley;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getFirstLadder() {
        return firstLadder;
    }

    public void setFirstLadder(int firstLadder) {
        this.firstLadder = firstLadder;
    }


    public float getSecondLadder() {
        return secondLadder;
    }

    public void setSecondLadder(float secondLadder) {
        this.secondLadder = secondLadder;
    }

}
