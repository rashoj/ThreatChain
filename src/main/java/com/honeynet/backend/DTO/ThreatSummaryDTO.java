package com.honeynet.backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.honeynet.backend.entity.RiskLevel;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ThreatSummaryDTO {
    private Long id;
    private String title;
    private String category;
    private String sectors;
    private String countries;

    @JsonProperty("malware_families")
    private List<String> malwareFamilies;

    @JsonProperty("risk_score")
    private RiskLevel riskLevel;

    @JsonProperty("ai_summary")
    private String aiSummary;

    @JsonProperty("clientName")
    @Column(name = "client_name")
    private String clientName;

    private Double confidenceScore;

    @JsonProperty("generated_at")
    private LocalDateTime generatedAt;
    private List<String> extraSource; // ✅ Add this line


}
