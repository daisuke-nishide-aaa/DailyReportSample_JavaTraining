package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.DailyReport;
import com.example.entity.User;
import com.example.form.CommentForm;
import com.example.service.CommentService;
import com.example.service.ReportService;
import com.example.service.UserService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("reports", reportService.findAll(PageRequest.of(page, 10)));
        return "reports/list";
    }

    @GetMapping("/create")
    public String showCreateForm(@ModelAttribute DailyReport report) {
        return "reports/create";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserDetails userDetails,
            @Validated @ModelAttribute DailyReport report,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "reports/create";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        reportService.create(report, user);
        redirectAttributes.addFlashAttribute("successMessage", "日報を作成しました");

        return "redirect:/reports";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        DailyReport report = reportService.findById(id);
        model.addAttribute("report", report);
        model.addAttribute("comments", commentService.findByReport(report));
        // コメントフォームのバリデーションエラー再表示に備えて空の DTO を渡す
        model.addAttribute("commentForm", new CommentForm());
        return "reports/detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (!reportService.isOwner(id, userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "他のユーザーの日報は編集できません。");
            return "redirect:/reports";
        }

        model.addAttribute("report", reportService.findById(id));
        return "reports/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Validated @ModelAttribute DailyReport report,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "reports/edit";
        }

        if (!reportService.isOwner(id, userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "他のユーザーの日報は編集できません。");
            return "redirect:/reports";
        }

        reportService.update(id, report);
        redirectAttributes.addFlashAttribute("successMessage", "日報を更新しました");

        return "redirect:/reports";
    }

    @PostMapping("/delete/{id}")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        if (!reportService.isOwner(id, userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "他のユーザーの日報は削除できません。");
            return "redirect:/reports";
        }

        reportService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "日報を削除しました");
        return "redirect:/reports";
    }
}