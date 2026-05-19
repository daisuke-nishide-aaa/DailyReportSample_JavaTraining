package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.service.CommentService;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add/{reportId}")
    public String add(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reportId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {

        commentService.create(reportId, userDetails.getUsername(), content);
        redirectAttributes.addFlashAttribute("successMessage", "コメントを追加しました");

        return "redirect:/reports/" + reportId;
    }
}
