package com.honeynet.backend.model;

import org.springframework.data.jpa.repository.JpaRepository;
import com.honeynet.backend.model.ThreatLog;


    public interface ThreatLogRepository extends JpaRepository<ThreatLog, Long>{

    }

