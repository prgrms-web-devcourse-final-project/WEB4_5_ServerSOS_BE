package com.pickgo.domain.member.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/monitoring")
public class AdminMonitoringController {

    @GetMapping
    public String monitoringDashboard() {
        return "admin/monitoring";
    }
}
