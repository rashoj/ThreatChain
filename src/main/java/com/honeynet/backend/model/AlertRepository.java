package com.honeynet.backend.model;

import com.honeynet.backend.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {
    List<AlertEntity> findByClientName(String clientName);

}
