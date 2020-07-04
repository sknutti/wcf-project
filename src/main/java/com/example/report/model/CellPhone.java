package com.example.report.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "cell_phone")
public class CellPhone {
    @Id
    @Column(name = "employeeid", nullable = false)
    private long employeeId;
    @Column(name = "employeename", nullable = false)
    private String employeeName;
    @Column(name = "purchasedate", nullable = false)
    private Date purchaseDate;
    @Column(name = "model", nullable = false)
    private String model;

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
