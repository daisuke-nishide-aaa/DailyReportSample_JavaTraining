package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.DailyReport;
import com.example.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public List<DailyReport> list() {
        return reportService.findAll();
    }
}
