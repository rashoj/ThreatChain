package com.honeynet.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "threat_analysis")
public class ThreatAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private String authorName;

    private LocalDateTime created;

    private LocalDateTime modified;

    private int revision;

    private String tlp;

    private String adversary;

    private boolean moreIndicators;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;


    @Column( length = 4000)
    private String aiSummary;

    @Column(name = "country")
    private String country;


    // === COLLECTIONS ===

//    @ElementCollection
//    @CollectionTable(name = "threat_tags", joinColumns = @JoinColumn(name = "threat_analysis_id"))
//    @Column(name = "tag")
//    private List<String> tags = new ArrayList<>();

    @OneToMany(mappedBy = "threatAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThreatTagEntity> tags = new ArrayList<>();


    public List<ThreatTagEntity> getTags() {
        return tags;
    }

    public void setTags(List<ThreatTagEntity> tags) {
        this.tags = tags;
    }

    @ElementCollection
    @CollectionTable(name = "threat_targeted_countries", joinColumns = @JoinColumn(name = "threat_analysis_id"))
    @Column(name = "country")
    private Set<String> targetedCountries = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "threat_malware_families", joinColumns = @JoinColumn(name = "threat_analysis_id"))
    @Column(name = "malware_family")
    private Set<String> malwareFamilies = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "threat_attack_ids", joinColumns = @JoinColumn(name = "threat_analysis_id"))
    @Column(name = "attack_id")
    private List<String> attackIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "threat_references", joinColumns = @JoinColumn(name = "threat_analysis_id"))
    @Column(name = "reference_link")
    private List<String> referenceLinks = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)  // or EAGER if needed
    @CollectionTable(
            name = "threat_industries",
            joinColumns = @JoinColumn(name = "threat_analysis_id")
    )
    @Column(name = "industry")
    private Set<String> industries = new HashSet<>();


    // Changed from List<String> to single String
    @Column(name = "extra_source")
    private String extraSource;

    @OneToMany(mappedBy = "threatAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndicatorEntity> indicators = new ArrayList<>();

    // === GETTERS AND SETTERS ===

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getTlp() {
        return tlp;
    }

    public void setTlp(String tlp) {
        this.tlp = tlp;
    }

    public String getAdversary() {
        return adversary;
    }

    public void setAdversary(String adversary) {
        this.adversary = adversary;
    }

    public boolean isMoreIndicators() {
        return moreIndicators;
    }

    public void setMoreIndicators(boolean moreIndicators) {
        this.moreIndicators = moreIndicators;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

//    public List<String> getTags() {
//        return tags;

//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }
//    }
//




//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }


    public Set<String> getTargetedCountries() {
        return targetedCountries;
    }

    public void setTargetedCountries(Set<String> targetedCountries) {
        this.targetedCountries = targetedCountries;
    }

    public Set<String> getMalwareFamilies() {
        return malwareFamilies;
    }

    public void setMalwareFamilies(Set<String> malwareFamilies) {
        this.malwareFamilies = malwareFamilies;
    }

    public List<String> getAttackIds() {
        return attackIds;
    }

    public void setAttackIds(List<String> attackIds) {
        this.attackIds = attackIds;
    }

    public List<String> getReferenceLinks() {
        return referenceLinks;
    }

    public void setReferenceLinks(List<String> referenceLinks) {
        this.referenceLinks = referenceLinks;
    }

    public Set<String> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<String> industries) {
        this.industries = industries;
    }

    public String getExtraSource() {
        return extraSource;
    }

    public void setExtraSource(String extraSource) {
        this.extraSource = extraSource;
    }

    public List<IndicatorEntity> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<IndicatorEntity> indicators) {
        this.indicators = indicators;
    }

    // === CONVENIENCE METHODS ===

    public void addIndicator(IndicatorEntity indicator) {
        indicators.add(indicator);
        indicator.setThreatAnalysis(this);
    }

    public void removeIndicator(IndicatorEntity indicator) {
        indicators.remove(indicator);
        indicator.setThreatAnalysis(null);
    }

}
