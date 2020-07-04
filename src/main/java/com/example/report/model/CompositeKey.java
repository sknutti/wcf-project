package com.example.report.model;

import java.io.Serializable;

public class CompositeKey implements Serializable {
    private long employeeId;
    private String week;

    public CompositeKey() {
    }

    public CompositeKey(long employeeId, String date) {
        this.employeeId = employeeId;
        this.week = date;
    }
}