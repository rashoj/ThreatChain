package com.honeynet.backend.service;

import com.honeynet.backend.entity.AlertEntity;
import com.honeynet.backend.model.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertEntity createAlert(AlertEntity alert) {
        return alertRepository.save(alert);
    }

    public List<AlertEntity> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Optional<AlertEntity> getAlertById(Long id) {
        return alertRepository.findById(id);
    }

    public boolean deleteAlert(Long id) {
        if (alertRepository.existsById(id)) {
            alertRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public List<AlertEntity> getAlertsByClient(String clientName) {
        return alertRepository.findByClientName(clientName);
    }

}


