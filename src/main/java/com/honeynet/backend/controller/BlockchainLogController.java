package com.honeynet.backend.controller;
import com.honeynet.backend.Entity.ThreatAnalysisEntity;
import com.honeynet.backend.Entity.BlockchainLog;
import com.honeynet.backend.service.BlockchainLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.honeynet.backend.DTO.BlockchainLogDTO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blockchain-logs/")
public class BlockchainLogController {

    private final BlockchainLogService service;

    // ✅ Inject internal API key from application.yml
    @Value("${security.api-key}")
    private String internalApiKey;

    public BlockchainLogController(BlockchainLogService service) {
        this.service = service;
    }

    // ✅ GET all logs with API key validation
    @GetMapping("/list")
    public ResponseEntity<List<BlockchainLogDTO>> getAllLogs(
            @RequestHeader(value = "x-api-key", required = false) String apiKey) {

        System.out.println("Received GET /blockchain-logs");
        System.out.println("Received api key: " + apiKey);
        System.out.println("Expected api key: " + internalApiKey);

        if (apiKey == null || !apiKey.equals(internalApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<BlockchainLogDTO> dtoList = service.getAllLogs().stream()
                        .map(log -> new BlockchainLogDTO(
                                log.getId(),
                                log.getEvent(),
                                log.getBlockNumber(),
                                log.getTxHash(),
                                log.getThreatHash(),
                                log.getTransactionHash(),
                                log.getTimestamp(),
                                log.getThreat() != null ? log.getThreat().getId() : null
                        )).toList();

        return ResponseEntity.ok(dtoList);
    }

    // ✅ POST log with same API key validation
    @PostMapping("/save")
    public ResponseEntity<BlockchainLog> createLog(
            @RequestHeader(value = "x-api-key", required = false) String apiKey,
            @RequestBody BlockchainLog log) {

        if (apiKey == null || !apiKey.equals(internalApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

       System.out.println("Inside controller (POST)");
        BlockchainLog saved = service.saveLog(log);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    //DebugTestEndpoint

    @GetMapping("/test")
    public ResponseEntity<String>test(){
        System.out.println("Blockchain Controller is working");
        return ResponseEntity.ok("Controller reached");
    }
}

