package com.honeynet.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ hook CORS config
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/summaries", "/api/summaries/**").permitAll() // ✅ public
                        .requestMatchers("/api/test/**").permitAll()                        // ✅ public test
                        .requestMatchers("/api/blockchain-logs/list").permitAll()
                        .requestMatchers("/api/threats/countries").authenticated()
                        .requestMatchers("/demo/api/blockchain-logs/**").permitAll()
                        .requestMatchers("/api/threats/**").hasRole("ADMIN")               // 🔐 protected
                        .anyRequest().authenticated()                                      // default: login required
                )
               .httpBasic(Customizer.withDefaults()); // allows Postman & basic testing

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow React frontend ports
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5176",
                "http://localhost:5177",
                "http://localhost:5173"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true); // Needed if you ever use cookies or login

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
