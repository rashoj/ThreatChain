package com.honeynet.backend.service;

import com.honeynet.backend.DTO.ThreatAnalysis;
import org.springframework.stereotype.Service;

@Service
public class AiSummaryGeneratorService {

    public String generateSummary(ThreatAnalysis threat) {
        // For demo, return a fake summary based on threat name or id
        return "AI-generated summary for threat: " + threat.getName();
    }
}
