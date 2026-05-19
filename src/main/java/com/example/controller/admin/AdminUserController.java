package com.example.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.User;
import com.example.service.UserService;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String showCreateForm(@ModelAttribute("user") User user) {
        return "admin/users/create";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "このメールアドレスは既に使用されています");
        }

        if (result.hasErrors()) {
            return "admin/users/create";
        }

        userService.create(user);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーを登録しました");

        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (userService.isAdmin(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "管理者は削除できません。");
            return "redirect:/admin/users";
        }

        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーを削除しました。");
        return "redirect:/admin/users";
    }
}