package com.honeynet.backend.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ThreatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;

    private String category;

    @JsonProperty("detectionMethod")
    @Column(name = "detection_method")
    private String detectionMethod;

    @JsonProperty("sourceIP")
    @Column(name="src_ip", length = 45, nullable = false)
    private String sourceIP;

    @JsonProperty("destinationIP")
    @Column(name = "dest_ip", length = 45)
    private String destinationIP;

    @JsonProperty("threatLevel")
    @Column (name = "threat_level", length = 100)
    private String threatLevel;

    @JsonProperty("threatType")
    @Column (name = "threat_type", length = 100)
    private String threatType;

    @Column(name="country", length = 100)
    private String country;

    @Column(name="region", length = 100)
    private String region;

    @Column(name="city", length = 100)
    private String city;

    @Column(name="latitude", length = 100)
    private String latitude;

    @Column(name="longitude", length = 100)
    private String longitude;

    @Column(name="timestamp")
    private LocalDateTime timestamp;

}
