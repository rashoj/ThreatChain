package com.honeynet.backend.model;

import com.honeynet.backend.Entity.ThreatAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThreatAnalysisRepo extends JpaRepository<ThreatAnalysisEntity, Long> {
    boolean existsByNameAndRevision(String name, int revision);

    Optional<ThreatAnalysisEntity> findByNameAndRevision(String name, int revision);

    // Custom query to find entities with null or empty aiSummary
    @Query("SELECT t FROM ThreatAnalysisEntity t WHERE t.aiSummary IS NULL OR t.aiSummary = ''")
    List<ThreatAnalysisEntity> findByAiSummaryIsNullOrEmpty();

    @Query("SELECT DISTINCT t.country FROM ThreatAnalysisEntity t WHERE t.country IS NOT NULL AND t.country <> ''")
    List<String> findDistinctCountries();

}
