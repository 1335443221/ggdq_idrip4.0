package com.sl.common.entity;

public class ElcmTaskRecord {
   private String relationId;
   private String beginTime;
   private String endTime;
   private int isRepeat;
   private int isStart;
   private int maintainIntervals;
   private String maintainIntervalsUnit;
   private String planStartTime;

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getIsRepeat() {
		return isRepeat;
	}

	public void setIsRepeat(int isRepeat) {
		this.isRepeat = isRepeat;
	}

	public int getIsStart() {
		return isStart;
	}

	public void setIsStart(int isStart) {
		this.isStart = isStart;
	}

	public int getMaintainIntervals() {
		return maintainIntervals;
	}

	public void setMaintainIntervals(int maintainIntervals) {
		this.maintainIntervals = maintainIntervals;
	}

	public String getMaintainIntervalsUnit() {
		return maintainIntervalsUnit;
	}

	public void setMaintainIntervalsUnit(String maintainIntervalsUnit) {
		this.maintainIntervalsUnit = maintainIntervalsUnit;
	}

	public String getPlanStartTime() {
		return planStartTime;
	}

	public void setPlanStartTime(String planStartTime) {
		this.planStartTime = planStartTime;
	}
}
