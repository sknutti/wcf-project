package com.example.report.controller;

import com.example.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/report")
    public void getReportData() {
        try {
            reportService.runCellPhoneUsageReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}