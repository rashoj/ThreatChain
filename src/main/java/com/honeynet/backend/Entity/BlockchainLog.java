package com.honeynet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Entity
public class BlockchainLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String event;
    private String blockNumber;
    private String txHash;
    private String threatHash;
    private String transactionHash;

    @Column
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threat_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_blockchain_threat"))
    private ThreatAnalysisEntity threat;


    @Override
    public String toString() {
        return "BlockchainLog{" +
                "id=" + id +
                ", event='" + event + '\'' +
                ", blockNumber='" + blockNumber + '\'' +
                ", txHash='" + txHash + '\'' +
                ", threatHash='" + threatHash + '\'' +
                ", transactionHash='" + transactionHash + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}



