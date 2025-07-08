package com.honeynet.backend.model;

import com.honeynet.backend.Entity.BlockchainLog;
import com.honeynet.backend.model.BlockchainLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BlockchainLogRepository extends JpaRepository<BlockchainLog, Long>{
}
