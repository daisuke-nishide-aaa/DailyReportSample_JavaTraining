package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.entity.User;
import com.example.repository.DailyReportRepository;    // 追記
import com.example.repository.UserRepository;

@Controller
public class WelcomeController {

    @Autowired
    private UserRepository userRepository;

    // 以下を追記
    @Autowired
    private DailyReportRepository reportRepository;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userRepository.findByEmail(userDetails.getUsername()).get();
            model.addAttribute("userName", user.getName());
            // 以下を追記
            model.addAttribute("reports", reportRepository.findByUserOrderBySubmissionDateDesc(user));
        }
        return "welcome/index";
    }
}