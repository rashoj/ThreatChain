package com.honeynet.backend;


import com.honeynet.backend.config.AlienVaultConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.honeynet.backend.controller.ThreatLogController;

@SpringBootApplication
@EnableConfigurationProperties(AlienVaultConfig.class)
public class BackendApplication {

	public static void main(String[] args) {
		System.out.println("App bootstrapping...");

		SpringApplication.run(BackendApplication.class, args);
	}

}
