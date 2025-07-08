package com.honeynet.backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.honeynet.backend.Entity.RiskLevel;
import lombok.Data;
import com.honeynet.backend.DTO.ThreatSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ThreatSummaryDTO {
    private Long id;
    private String title;
    private String category;
    private String sectors;
    private String countries;

    @JsonProperty("risk_score")
    private RiskLevel riskLevel;

    @JsonProperty("ai_summary")
    private String aiSummary;

    @JsonProperty("generated_at")
    private LocalDateTime generatedAt;
    private List<String> extraSource; // ✅ Add this line


}
