package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.User;
import com.example.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(@ModelAttribute("user") User user) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") User user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "このメールアドレスは既に使用されています");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        userService.register(user);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザー登録が完了しました");

        return "redirect:/auth/login";
    }
}
