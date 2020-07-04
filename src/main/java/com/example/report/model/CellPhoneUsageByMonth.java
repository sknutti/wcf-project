package com.example.report.model;

import javax.persistence.*;

@Entity
@Table(name = "cell_phone_usage_by_month")
@IdClass(CompositeKey.class)
public class CellPhoneUsageByMonth {
    @Id
    @Column(name = "employeeid", nullable = false)
    private long employeeId;
    @Id
    @Column(name = "week", nullable = false)
    private String week;
    @Column(name = "totalminutes", nullable = false)
    private int totalMinutes;
    @Column(name = "totaldata", nullable = false)
    private float totalData;

    public CellPhoneUsageByMonth() {
    }

    public CellPhoneUsageByMonth(long employeeId, String date, int minutes, float data) {
        this.employeeId = employeeId;
        this.week = date;
        this.totalMinutes = minutes;
        this.totalData = data;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public float getTotalData() {
        return totalData;
    }

    public void setTotalData(float totalData) {
        this.totalData = totalData;
    }
}
