package com.example.report.model;

import java.sql.Date;
import java.util.List;

public class ReportData {
    private long empId;
    private String name;
    private String model;
    private Date purchaseDate;
    private List<MonthData> monthlyData;

    public long getEmpId() {
        return empId;
    }

    public void setEmpId(long empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<MonthData> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<MonthData> monthlyData) {
        this.monthlyData = monthlyData;
    }
}
