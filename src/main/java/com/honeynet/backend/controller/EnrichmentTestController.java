package com.honeynet.backend.controller;

import com.honeynet.backend.entity.ThreatAnalysisEntity;
import com.honeynet.backend.service.AlienVaultService;
import com.honeynet.backend.service.ThreatSummaryPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/test")
public class EnrichmentTestController {

    @Autowired
    private AlienVaultService alienVaultService;

    @Autowired
    private ThreatSummaryPipeline summaryPipeline;

    @GetMapping("/enrich-all")
    public String enrichAll() {
        List<ThreatAnalysisEntity> rawThreats = alienVaultService.getAllStoredThreatEntities();

        rawThreats.forEach(threat -> {
            System.out.println("Enriching: " + threat.getName());
            summaryPipeline.enrichAndSave(threat);
        });

        return "✅ Enrichment completed for " + rawThreats.size() + " threats.";
    }
}
