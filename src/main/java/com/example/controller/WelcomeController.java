package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.ReportService;

@Controller
public class WelcomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        if (userDetails != null) {
            User user = userRepository.findByEmail(userDetails.getUsername()).get();
            model.addAttribute("userName", user.getName());
            model.addAttribute("reports", reportService.findByUser(user, PageRequest.of(page, 5)));
        }
        return "welcome/index";
    }
}