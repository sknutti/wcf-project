package com.example.report.model;

public class MonthData implements Comparable<MonthData>{
    private int monthValue;
    private int minutes;
    private float data;

    public MonthData() {
    }

    public MonthData(int monthValue, int minutes, float data) {
        this.monthValue = monthValue;
        this.minutes = minutes;
        this.data = data;
    }

    public int getMonthValue() {
        return monthValue;
    }

    public void setMonthValue(int monthValue) {
        this.monthValue = monthValue;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    @Override
    public int compareTo(MonthData otherData) {
        return (this.getMonthValue() - otherData.getMonthValue());
    }
}