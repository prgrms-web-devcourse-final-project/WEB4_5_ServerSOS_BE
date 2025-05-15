package com.pickgo.domain.member.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/monitoring")
public class AdminMonitoringController {

    @GetMapping
    public String monitoringDashboard() {
        return "admin/monitoring";
    }

    @GetMapping("/health")
    @ResponseBody
    public String healthCheck() {
        return "Ok";
    }
}
