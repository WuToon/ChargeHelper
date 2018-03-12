package com.zhangwenl1993163.chargehelper.model;

/**
 * Created by zhangwenliang on 2018/3/12.
 */

public class Attendance {
    private int id;
    private String attendancePeople;
    private int attendanceHours;
    //1 请假 2 加班
    private int attendanceType;
    private long addTime;
    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAttendancePeople() {
        return attendancePeople;
    }

    public void setAttendancePeople(String attendancePeople) {
        this.attendancePeople = attendancePeople;
    }

    public int getAttendanceHours() {
        return attendanceHours;
    }

    public void setAttendanceHours(int attendanceHours) {
        this.attendanceHours = attendanceHours;
    }

    public int getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(int attendanceType) {
        this.attendanceType = attendanceType;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", attendancePeople='" + attendancePeople + '\'' +
                ", attendanceHours=" + attendanceHours +
                ", attendanceType=" + attendanceType +
                ", addTime=" + addTime +
                ", comment='" + comment + '\'' +
                '}';
    }
}
