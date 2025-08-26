package com.honeynet.backend.model;

import com.honeynet.backend.entity.BlockchainLog;  // assuming package name is entity, not Entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // Optional, but good practice
public interface BlockchainLogRepository extends JpaRepository<BlockchainLog, Long> {
}
