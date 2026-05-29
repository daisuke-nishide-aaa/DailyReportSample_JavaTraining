package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.DailyReport;
import com.example.entity.User;
import com.example.repository.DailyReportRepository;

@Service                   // ← Service 層であることを Spring に伝える
@Transactional             // ← クラス全体にトランザクションを適用（読み取りのみのメソッドは後で調整）
public class ReportService {

    @Autowired
    private DailyReportRepository reportRepository;

    // 全件取得（読み取りのみなので readOnly = true を指定）
    @Transactional(readOnly = true)
    public List<DailyReport> findAll() {
        return reportRepository.findAllByOrderBySubmissionDateDesc();
    }

    // ID で1件取得
    @Transactional(readOnly = true)
    public DailyReport findById(Long id) {
        return reportRepository.findById(id).get();
    }

    // 日報を新規作成
    public void create(DailyReport report, User user) {
        report.setUser(user);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        reportRepository.save(report);
    }

    // 日報を更新
    public void update(Long id, DailyReport newData) {
        DailyReport existing = reportRepository.findById(id).get();
        existing.setTitle(newData.getTitle());
        existing.setContent(newData.getContent());
        existing.setSubmissionDate(newData.getSubmissionDate());
        existing.setStartTime(newData.getStartTime());
        existing.setEndTime(newData.getEndTime());
        existing.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(existing);
    }

    // 日報を削除
    public void delete(Long id) {
        reportRepository.deleteById(id);
    }

    // 投稿者本人かチェック
    @Transactional(readOnly = true)
    public boolean isOwner(Long reportId, String email) {
        DailyReport report = reportRepository.findById(reportId).get();
        return report.getUser().getEmail().equals(email);
    }
}
