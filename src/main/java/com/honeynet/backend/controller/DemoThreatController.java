package com.honeynet.backend.controller;

import com.honeynet.backend.DTO.ThreatAnalysis;
import com.honeynet.backend.DTO.ThreatSummaryDTO;
import com.honeynet.backend.service.AlienVaultService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class DemoThreatController {

    private final AlienVaultService alienVaultService;

    public DemoThreatController(AlienVaultService alienVaultService) {
        this.alienVaultService = alienVaultService;
    }

    @GetMapping("/summaries")
    public ResponseEntity<Page<ThreatSummaryDTO>> getDemoThreatSummaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String countryFilter,
            @RequestParam(required = false) String malwareFilter,
            @RequestParam(required = false) String riskFilter,
            @RequestParam(required = false) String clientName)


    {

        Page<ThreatSummaryDTO> summaries = alienVaultService.getDemoThreatSummaries(page, size, countryFilter, malwareFilter, riskFilter,clientName);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/threats/{id}")
    public ResponseEntity<ThreatAnalysis> getDemoThreatById(@PathVariable Long id) {
        List<ThreatAnalysis> allThreats = alienVaultService.loadAllDemoThreats();

        return allThreats.stream()
                .filter(t -> {
                    try {
                        return Long.parseLong(t.getId()) == id;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    @PutMapping("summaries/regenerate/{id}")
//    public ResponseEntity<String> regenerateAiSummary(@PathVariable Long id) {
//        try {
//            alienVaultService.regenerateAiSummary(id);  // ✅ Works now
//            return ResponseEntity.ok("AI Summary regenerated for threat ID: " + id);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }
//    @PostMapping("/summaries/import")
//    public ResponseEntity<String> importThreat() {
//        alienVaultService.importThreatFromJsonFile("demo-data/threatsone.json");
//        return ResponseEntity.ok("Imported");
//    }


}

