package com.honeynet.backend.model;


import com.honeynet.backend.entity.ThreatSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreatSummaryRepository extends JpaRepository<ThreatSummaryEntity, Long> {
    boolean existsByTitleAndAiSummary(String title, String aiSummary);
}