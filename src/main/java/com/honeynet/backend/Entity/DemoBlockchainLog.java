package com.honeynet.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DemoBlockchainLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionHash;

    private String action;


    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String details;

    private String hash;          // Added hash field


    private String previousHash;  // Added previousHash field

    private String dataSnapshot;

    @ManyToOne
    @JoinColumn(name = "threat_id")
    private ThreatAnalysisEntity threat;

}
