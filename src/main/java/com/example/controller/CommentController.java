package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.DailyReport;
import com.example.form.CommentForm;
import com.example.service.CommentService;
import com.example.service.ReportService;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReportService reportService;

    @PostMapping("/add/{reportId}")
    public String add(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reportId,
            @Validated @ModelAttribute CommentForm commentForm,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // バリデーションエラー時はリダイレクトせず、detail.html を直接返す。
            // redirect すると BindingResult が失われるため th:errors でエラーを表示できない。
            // そのため、detail.html の描画に必要なデータを自分で Model に積む。
            DailyReport report = reportService.findById(reportId);
            model.addAttribute("report", report);
            model.addAttribute("comments", commentService.findByReport(report));
            return "reports/detail";
        }

        commentService.create(reportId, userDetails.getUsername(), commentForm.getContent());
        redirectAttributes.addFlashAttribute("successMessage", "コメントを追加しました");

        return "redirect:/reports/" + reportId;
    }
}
