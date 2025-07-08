package com.honeynet.backend.service;

import com.honeynet.backend.Entity.ThreatSummaryEntity;
import com.honeynet.backend.model.ThreatSummaryRepository;
import com.honeynet.backend.Entity.ThreatAnalysisEntity;
import com.honeynet.backend.Entity.IndicatorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThreatSummaryPipeline {

    @Autowired
    private ThreatSummaryRepository summaryRepository;

    @Autowired
    private OpenAIService openAIService;

    public void enrichAndSave(ThreatAnalysisEntity threat) {
        String sector = String.join(", ", threat.getIndustries());
        String country = String.join(", ", threat.getTargetedCountries());
        String name = threat.getName();
        String description = threat.getDescription();

        List<String> indicatorValues = threat.getIndicators().stream()
                .map(IndicatorEntity::getIndicator)
                .collect(Collectors.toList());

        int riskScore = calculateRisk(indicatorValues.size(), sector, country);
        String aiSummary = openAIService.generateThreatSummary(name, description, indicatorValues);
        System.out.println("AI Summary generated: " + aiSummary); // or use logger.info(...)

        ThreatSummaryEntity summary = new ThreatSummaryEntity();
        summary.setTitle("Summary: " + name);
        summary.setCategory("Cyber Threat");
        summary.setSectors(sector);
        summary.setCountries(country);
        summary.setRiskScore(riskScore);
        summary.setAiSummary(aiSummary);
        summary.setGeneratedAt(LocalDateTime.now());

        summaryRepository.save(summary);
    }

    private int calculateRisk(int indicatorCount, String sector, String country) {
        int score = indicatorCount * 3;
        if (sector.contains("Finance")) score += 15;
        if (country.contains("US")) score += 10;
        return Math.min(score, 100);
    }
}
