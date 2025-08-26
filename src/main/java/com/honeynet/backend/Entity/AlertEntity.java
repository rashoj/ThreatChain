package com.honeynet.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String severity;  // e.g., LOW, MEDIUM, HIGH
    private String country;

    @JsonProperty("malwareFamily")
    @Column(name = "malware_family")
    private String malwareFamily;
    private String description;

    private LocalDateTime timestamp;

    @JsonProperty("clientName")
    @Column(name = "client_name")
    private String clientName;

}
