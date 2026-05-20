package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.DailyReport;
import com.example.entity.User;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    List<DailyReport> findByUserOrderBySubmissionDateDesc(User user);
    List<DailyReport> findAllByOrderBySubmissionDateDesc();
    Page<DailyReport> findAllByOrderBySubmissionDateDesc(Pageable pageable);
    Page<DailyReport> findByUserOrderBySubmissionDateDesc(User user, Pageable pageable);
}