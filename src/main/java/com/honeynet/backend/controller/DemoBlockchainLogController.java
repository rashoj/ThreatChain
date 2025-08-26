package com.honeynet.backend.controller;


import com.honeynet.backend.entity.DemoBlockchainLog;
import com.honeynet.backend.service.DemoBlockchainLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("demo/api/blockchain-logs")
@RequiredArgsConstructor
public class DemoBlockchainLogController {

    private final DemoBlockchainLogService blockchainService;

    @GetMapping("demo/threat/{threatId}")
    public List<DemoBlockchainLog> getLogsForThreat(@PathVariable String threatId) {
        return blockchainService.getLogsForThreat(threatId);

    }


    @PostMapping("demo/threat/{threatId}/save-log")
    public String saveDemoLogForThreat(@PathVariable String threatId) {
        blockchainService.saveDemoLogForThreat(threatId);
        return "Demo blockchain log saved for threat ID " + threatId;
    }
}

