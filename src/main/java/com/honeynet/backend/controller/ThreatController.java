//package com.honeynet.backend.controller;
//
//import com.honeynet.backend.model.Threat;
//import com.honeynet.backend.service.AlienVaultService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//
//@RestController
//@RequestMapping("/api/threats")
//@CrossOrigin(origins = "*")
//public class ThreatController {
//
//    private final AlienVaultService alienVaultService;
//
//    @Autowired
//    public ThreatController(AlienVaultService alienVaultService) {
//        this.alienVaultService = alienVaultService;
//    }
//
//    @GetMapping("/live")
//    public String getLiveThreats() {
//        return alienVaultService.getLatestThreats();
//    }
//
//}
