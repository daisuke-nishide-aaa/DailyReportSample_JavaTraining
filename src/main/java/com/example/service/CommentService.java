package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Comment;
import com.example.entity.DailyReport;
import com.example.entity.User;
import com.example.repository.CommentRepository;
import com.example.repository.DailyReportRepository;
import com.example.repository.UserRepository;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DailyReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Comment> findByReport(DailyReport report) {
        return commentRepository.findByDailyReportOrderByCreatedAtAsc(report);
    }

    public void create(Long reportId, String email, String content) {
        DailyReport report = reportRepository.findById(reportId).get();
        User user = userRepository.findByEmail(email).get();

        Comment comment = new Comment();
        comment.setDailyReport(report);
        comment.setUser(user);
        comment.setContent(content);

        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        commentRepository.save(comment);
    }
}
