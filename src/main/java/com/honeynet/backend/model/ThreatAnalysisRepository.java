package com.honeynet.backend.model;

import com.honeynet.backend.entity.ThreatAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreatAnalysisRepository extends JpaRepository<ThreatAnalysisEntity, Long> {

}