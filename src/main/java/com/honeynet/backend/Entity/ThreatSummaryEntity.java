package com.honeynet.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ThreatSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private String sectors;
    private String countries;
    private int riskScore;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    private LocalDateTime generatedAt;
}
