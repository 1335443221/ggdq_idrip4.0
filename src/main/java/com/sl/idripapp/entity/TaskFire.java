package com.sl.idripapp.entity;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/11/25 14:32
 * FileName: TaskPark
 * Description: ${DESCRIPTION}
 */
public class TaskFire {
    String patrolName;  //巡检名称
    String fireNumber;    //火灾探测器编号
    String patrolDate;  //巡检时间
    String executor;  //执行人

    public String getPatrolName() {
        return patrolName;
    }

    public void setPatrolName(String patrolName) {
        this.patrolName = patrolName;
    }

    public String getFireNumber() {
        return fireNumber;
    }

    public void setFireNumber(String fireNumber) {
        this.fireNumber = fireNumber;
    }

    public String getPatrolDate() {
        return patrolDate;
    }

    public void setPatrolDate(String patrolDate) {
        this.patrolDate = patrolDate;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }
}
