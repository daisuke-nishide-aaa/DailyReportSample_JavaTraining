package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.DailyReport;
import com.example.entity.User;
import com.example.repository.DailyReportRepository;
import com.example.repository.UserRepository;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private DailyReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // すべての日報を提出日の降順で取得
        model.addAttribute("reports", reportRepository.findAllByOrderBySubmissionDateDesc());
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

        User user = userRepository.findByEmail(userDetails.getUsername()).get();
        report.setUser(user);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        redirectAttributes.addFlashAttribute("successMessage", "日報を作成しました");

        return "redirect:/reports";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("report", reportRepository.findById(id).get());
        return "reports/detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        DailyReport report = reportRepository.findById(id).get();
        
        // 投稿者本人でない場合は日報一覧にリダイレクト
        if (!report.getUser().getEmail().equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "他のユーザーの日報は編集できません。");
            return "redirect:/reports";
        }
        
        model.addAttribute("report", report);
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

        DailyReport existingReport = reportRepository.findById(id).get();
        
        // 投稿者本人でない場合は日報一覧にリダイレクト
        if (!existingReport.getUser().getEmail().equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "他のユーザーの日報は編集できません。");
            return "redirect:/reports";
        }

        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());
        existingReport.setSubmissionDate(report.getSubmissionDate());
        existingReport.setUpdatedAt(LocalDateTime.now());

        reportRepository.save(existingReport);
        redirectAttributes.addFlashAttribute("successMessage", "日報を更新しました");

        return "redirect:/reports";
    }

    @PostMapping("/delete/{id}")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        DailyReport report = reportRepository.findById(id).get();
        
        // 投稿者本人でない場合は日報一覧にリダイレクト
        if (!report.getUser().getEmail().equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "他のユーザーの日報は削除できません。");
            return "redirect:/reports";
        }
        
        reportRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "日報を削除しました");
        return "redirect:/reports";
    }
}