package com.example.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Comment;
import com.example.entity.DailyReport;
import com.example.entity.User;
import com.example.repository.CommentRepository;
import com.example.repository.DailyReportRepository;
import com.example.repository.UserRepository;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DailyReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add/{reportId}")
    public String add(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reportId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {

        DailyReport report = reportRepository.findById(reportId).get();
        User user = userRepository.findByEmail(userDetails.getUsername()).get();

        Comment comment = new Comment();
        comment.setDailyReport(report);
        comment.setUser(user);
        comment.setContent(content);

        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        commentRepository.save(comment);
        redirectAttributes.addFlashAttribute("successMessage", "コメントを追加しました");

        return "redirect:/reports/" + reportId;
    }
}
