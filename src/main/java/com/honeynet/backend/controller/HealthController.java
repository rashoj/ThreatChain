package com.honeynet.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class HealthController {

    @GetMapping
    public ResponseEntity<String> test(){
        System.out.println("Blockchain Controller is working");
        return ResponseEntity.ok("Blockchain Controller is working");
    }
}
