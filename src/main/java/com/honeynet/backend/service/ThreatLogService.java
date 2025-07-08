package com.honeynet.backend.service;

import com.honeynet.backend.model.ThreatLog;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.honeynet.backend.model.ThreatLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreatLogService {

    private final ThreatLogRepository repository;

    @Autowired
    public ThreatLogService(ThreatLogRepository repository) {
        this.repository = repository;
    }

    public long getThreatCount() {
        return repository.count();
    }
    public ThreatLog saveThreatLog(ThreatLog log) {
        return repository.save(log);
    }

    public List<ThreatLog> getAllThreatLogs() {
        return repository.findAll();
    }

    public ThreatLog getThreatLogById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteThreatLog(Long id) {
        repository.deleteById(id);
    }
}