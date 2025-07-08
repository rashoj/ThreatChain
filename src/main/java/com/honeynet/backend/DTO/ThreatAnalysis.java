package com.honeynet.backend.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatAnalysis {
    private String id;
    private String name;
    private String description;

    @JsonProperty("author_name")
    private String authorName;

    private LocalDateTime modified;
    private LocalDateTime created;
    private int revision;
    private String tlp;
    private String adversary;

    @JsonProperty("more_indicators")
    private boolean moreIndicators;

    private List<IndicatorDTO> indicators; // ✅ Fixed here

    private String aiSummary;

    private List<String> tags;

    @JsonProperty("targeted_countries")
    private List<String> targetedCountries;

    @JsonProperty("malware_families")
    private List<String> malwareFamilies;

    @JsonProperty("attacks_ids")
    private List<String> attackIds;

    private List<String> references;

    private List<String> industries;

    @JsonProperty("extraSource")
    private String extraSource;
}
