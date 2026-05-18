package com.example.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Comment;
import com.example.repository.CommentRepository;

@Controller
@RequestMapping("/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Comment comment = commentRepository.findById(id).get();
        Long reportId = comment.getDailyReport().getId();

        commentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "コメントを削除しました。");

        return "redirect:/reports/" + reportId;
    }
}
