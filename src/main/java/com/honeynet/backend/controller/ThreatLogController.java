package com.honeynet.backend.controller;

import com.honeynet.backend.model.ThreatLog;
import com.honeynet.backend.DTO.ThreatAnalysis;
import com.honeynet.backend.service.AlienVaultService;
import com.honeynet.backend.service.ThreatLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/threats")
public class ThreatLogController {

    private final ThreatLogService threatLogService;

    @Autowired
    private final AlienVaultService alienVaultService;

    @Autowired
    public ThreatLogController(ThreatLogService threatLogService, AlienVaultService alienVaultService) {
        this.threatLogService = threatLogService;
        this.alienVaultService = alienVaultService;
    }

    @PostMapping
    public ResponseEntity<ThreatLog> createLog(@RequestBody ThreatLog threatLog) {
        ThreatLog savedLog = threatLogService.saveThreatLog(threatLog);
        return ResponseEntity.ok(savedLog);
    }

    @GetMapping
    public ResponseEntity<List<ThreatAnalysis>> getAllThreatAnalysis() {
        List<ThreatAnalysis> threats = alienVaultService.getAllStoredThreats();
        return ResponseEntity.ok(threats);
    }


    @GetMapping("/count")
    public ResponseEntity<Long> getThreatCount() {
        return ResponseEntity.ok(threatLogService.getThreatCount());
    }

    @GetMapping("/log/{id}")
    public ResponseEntity<ThreatLog> getLogById(@PathVariable long id) {
        ThreatLog log = threatLogService.getThreatLogById(id);
        return log != null ? ResponseEntity.ok(log) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreatAnalysis> getThreatById(@PathVariable Long id) {
        ThreatAnalysis threat = alienVaultService.getThreatById(id);
        return threat != null ? ResponseEntity.ok(threat) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogById(@PathVariable long id) {
        threatLogService.deleteThreatLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alienvault-fetch")
    public ResponseEntity<List<ThreatAnalysis>> fetchAlienVaultAnalysis() {
        try {
            List<ThreatAnalysis> threats = alienVaultService.fetchAndParsePulses();
            return ResponseEntity.ok(threats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ This is the new endpoint for fetching persisted data
    @GetMapping("/alienvault-stored")
    public ResponseEntity<List<ThreatAnalysis>> getStoredAlienVaultThreats() {
        try {
            List<ThreatAnalysis> storedThreats = alienVaultService.getAllStoredThreats();
            return ResponseEntity.ok(storedThreats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        List<ThreatLog> logs = threatLogService.getAllThreatLogs();

        long totalThreats = logs.size();
        long highSeverity = logs.stream()
                .filter(log -> "High".equalsIgnoreCase(log.getThreatLevel()))
                .count();

        String topTag = logs.stream()
                .map(ThreatLog::getThreatType)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        String topCountry = logs.stream()
                .map(ThreatLog::getCountry)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalThreats", totalThreats);
        summary.put("highSeverity", highSeverity);
        summary.put("topTag", topTag);
        summary.put("topCountry", topCountry);

        return ResponseEntity.ok(summary);
    }
    @GetMapping("/countries")
    public List<String> getAllCountries() {
        return alienVaultService.getAllCountries();
    }


}
