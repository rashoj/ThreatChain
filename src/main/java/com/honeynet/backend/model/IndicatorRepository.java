package com.honeynet.backend.model;

import com.honeynet.backend.Entity.IndicatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicatorRepository extends JpaRepository<IndicatorEntity, Long> {
}
