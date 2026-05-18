package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.example.repository.UserRepository;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername()).get();
        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername()).get();
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @Validated @ModelAttribute User user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "users/edit";
        }

        User currentUser = userRepository.findByEmail(userDetails.getUsername()).get();

        // メールアドレスが変更されており、かつ新しいメールアドレスが既に使用されている場合
        if (!currentUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "このメールアドレスは既に使用されています");
            return "users/edit";
        }

        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPostalCode(user.getPostalCode());
        currentUser.setAddress(user.getAddress());
        currentUser.setUpdatedAt(LocalDateTime.now());

        // パスワードが入力されている場合のみ更新
        if (!user.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました");

        return "redirect:/users/profile";
    }
}