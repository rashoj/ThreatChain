package com.honeynet.backend.Entity;

import jakarta.persistence.*;



@Entity
public class ThreatTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tag;

    @ManyToOne
    @JoinColumn(name = "threat_analysis_id")
    private ThreatAnalysisEntity threatAnalysis;

    // Getter and Setter for 'tag'
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    // Getter and Setter for 'threatAnalysis'
    public ThreatAnalysisEntity getThreatAnalysis() {
        return threatAnalysis;
    }

    public void setThreatAnalysis(ThreatAnalysisEntity threatAnalysis) {
        this.threatAnalysis = threatAnalysis;
    }

    // other fields, constructors, etc.
}

