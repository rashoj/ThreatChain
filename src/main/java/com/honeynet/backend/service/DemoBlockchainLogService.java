package com.honeynet.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeynet.backend.entity.DemoBlockchainLog;
import com.honeynet.backend.entity.ThreatAnalysisEntity;
import com.honeynet.backend.model.DemoBlockchainLogRepository;
import com.honeynet.backend.model.ThreatAnalysisRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoBlockchainLogService {

    private final DemoBlockchainLogRepository blockchainRepo;
    private final ObjectMapper objectMapper;
    private final AlienVaultService alienVaultService;
    private final ThreatAnalysisRepo threatAnalysisRepo;

    public void logThreatCreation(ThreatAnalysisEntity threatEntity) {
        try {
            String snapshotJson = objectMapper.writeValueAsString(threatEntity);
            String previousHash = getLatestHash();
            LocalDateTime timestamp = LocalDateTime.now();
            String dataToHash = snapshotJson + previousHash + timestamp;

            String newHash = calculateSHA256(dataToHash);

            DemoBlockchainLog log = DemoBlockchainLog.builder()
                    .threat(threatEntity)
                    .action("CREATED")
                    .timestamp(timestamp)
                    .dataSnapshot(snapshotJson)
                    .hash(newHash)
                    .previousHash(previousHash)
                    .build();

            blockchainRepo.save(log);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create blockchain log", e);
        }
    }

    public List<DemoBlockchainLog> getLogsForThreat(String threatIdStr) {

        Long threatId = Long.parseLong(threatIdStr);
        return blockchainRepo.findByThreatIdOrderByIdAsc(threatId);
    }

    private String getLatestHash() {
        DemoBlockchainLog latestLog = blockchainRepo.findTopByOrderByIdDesc();
        return latestLog != null ? latestLog.getHash() : "0";
    }

    private String calculateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encoded);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }

    @Transactional
    public void saveDemoLogForThreat(String threatIdStr) {
        Long threatId = Long.parseLong(threatIdStr);
        ThreatAnalysisEntity threatEntity = threatAnalysisRepo.findById(threatId)
                .orElseThrow(() -> new RuntimeException("Threat not found with ID " + threatId));

        alienVaultService.saveDemoBlockchainLogForThreat(threatEntity);
    }
}
