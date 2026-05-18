package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Comment;
import com.example.entity.DailyReport;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDailyReportOrderByCreatedAtAsc(DailyReport dailyReport);
}
