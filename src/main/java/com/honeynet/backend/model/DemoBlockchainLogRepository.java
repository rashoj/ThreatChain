package com.honeynet.backend.model;


import com.honeynet.backend.entity.DemoBlockchainLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemoBlockchainLogRepository extends JpaRepository<DemoBlockchainLog, Long> {
    List<DemoBlockchainLog> findByThreatIdOrderByIdAsc(Long threatId);
    DemoBlockchainLog findTopByOrderByIdDesc(); // for latest hash reference
}

