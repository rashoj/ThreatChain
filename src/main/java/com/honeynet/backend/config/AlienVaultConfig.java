package com.honeynet.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "alienvault")
@Getter
@Setter
public class AlienVaultConfig {
    private String apiKey;
}
