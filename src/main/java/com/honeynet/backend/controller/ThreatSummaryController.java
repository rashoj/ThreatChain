package com.honeynet.backend.controller;

import com.honeynet.backend.DTO.ThreatSummaryDTO;
import com.honeynet.backend.service.AlienVaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/summaries")
public class ThreatSummaryController {

    @Autowired
    private AlienVaultService alienVaultService;

    @GetMapping("/paginated")
    public ResponseEntity<Page<ThreatSummaryDTO>> getPaginatedSummaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "generated_at") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {

        try {
            System.out.println("✅ /api/summaries endpoint hit");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Auth info: " + (auth != null ? auth.getName() : "none"));

            System.out.println("Fetching paginated threat summaries with params -> page: " + page + ", size: " + size + ", sortBy: " + sortBy + ", order: " + order);
            Page<ThreatSummaryDTO> summaries = alienVaultService.getPaginatedThreatSummaries(page, size, sortBy, order);
            System.out.println("Fetched " + summaries.getNumberOfElements() + " summaries");

            summaries.forEach(summary -> {
                System.out.println("ThreatSummary ID: " + summary.getId() + ", AI Summary: " + summary.getAiSummary());
            });

            return ResponseEntity.ok(alienVaultService.getPaginatedThreatSummaries(page, size, sortBy, order));

        } catch (Exception e) {
            System.err.println("Exception in /api/summaries: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreatSummaryDTO> getSummaryById(@PathVariable Long id) {
        try {
            System.out.println("✅ /api/summaries/" + id + " endpoint hit");
            ThreatSummaryDTO summary = alienVaultService.getThreatSummaryById(id);
            if (summary == null) {
                System.out.println("Summary not found for ID: " + id);
                return ResponseEntity.notFound().build();
            } else {
                System.out.println("Found summary for ID: " + id + ", AI Summary: " + summary.getAiSummary());
                return ResponseEntity.ok(summary);
            }
        } catch (Exception e) {
            System.err.println("Exception in /api/summaries/" + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/regenerate-missing")
    public ResponseEntity<String> regenerateMissingSummaries() {
        try {
            System.out.println("🔁 /api/summaries/regenerate-missing endpoint hit");
            alienVaultService.regenerateMissingSummaries();
            return ResponseEntity.ok("AI summaries regenerated for missing entries.");
        } catch (Exception e) {
            System.err.println("Exception in /api/summaries/regenerate-missing: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to regenerate summaries.");
        }
    }
    @GetMapping("/missing-summaries")
    public ResponseEntity<List<ThreatSummaryDTO>> getMissingAiSummaries() {
        try {
            List<ThreatSummaryDTO> missingSummaries = alienVaultService.getSummariesWithMissingAiSummary();
            return ResponseEntity.ok(missingSummaries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
//    @GetMapping("/api/demo/threats-old")
//    public ResponseEntity<List<ThreatSummaryDTO>> getDemoThreatSummaries() {
//        List<ThreatSummaryDTO> demoSummaries = alienVaultService.getDemoThreatSummaries();
//        return ResponseEntity.ok(demoSummaries);
//    }

}

