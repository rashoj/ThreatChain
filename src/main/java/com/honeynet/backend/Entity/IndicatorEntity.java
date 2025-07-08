package com.honeynet.backend.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "indicator")
public class IndicatorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String indicator;

    private String type;

    private LocalDateTime created;

    @Column(length = 2000)
    private String content;

    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDateTime expiration;

    private Boolean active;  // renamed from isActive to active

    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threat_analysis_id")
    private ThreatAnalysisEntity threatAnalysis;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ThreatAnalysisEntity getThreatAnalysis() {
        return threatAnalysis;
    }

    public void setThreatAnalysis(ThreatAnalysisEntity threatAnalysis) {
        this.threatAnalysis = threatAnalysis;
    }
}
