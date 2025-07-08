package com.honeynet.backend.service;

import com.honeynet.backend.Entity.BlockchainLog;
import com.honeynet.backend.model.BlockchainLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockchainLogService {

    private final BlockchainLogRepository repository;

    public BlockchainLogService(BlockchainLogRepository repository) {
        this.repository = repository;
    }

    public List<BlockchainLog> getAllLogs(){
        return repository.findAll();
    }

    public BlockchainLog saveLog(BlockchainLog log){
        return repository.save(log);
    }
}
