package com.honeynet.backend.controller;

import com.honeynet.backend.entity.AlertEntity;
import com.honeynet.backend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // Create alert (you already have this)
    @PostMapping
    public ResponseEntity<AlertEntity> createAlert(@RequestBody AlertEntity alert) {
        AlertEntity savedAlert = alertService.createAlert(alert);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAlert);
    }

    // Get all alerts
    @GetMapping
    public ResponseEntity<List<AlertEntity>> getAllAlerts() {
        List<AlertEntity> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    // Get alert by ID (optional)
    @GetMapping("/{id}")
    public ResponseEntity<AlertEntity> getAlertById(@PathVariable Long id) {
        Optional<AlertEntity> alertOpt = alertService.getAlertById(id);
        return alertOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete alert by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        boolean deleted = alertService.deleteAlert(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/by-client")
    public ResponseEntity<List<AlertEntity>> getAlertsByClient(@RequestParam String clientName) {
        List<AlertEntity> alerts = alertService.getAlertsByClient(clientName);
        return ResponseEntity.ok(alerts);
    }

}

