package com.honeynet.backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class BlockchainLogDTO {

        private Long id;
        private String event;
        private String blockNumber;
        private String txHash;
        private String threatHash;
        private String transactionHash;
        private Object timestamp;
        @JsonProperty("threat_id")
        private Long threatId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getThreatHash() {
        return threatHash;
    }

    public void setThreatHash(String threatHash) {
        this.threatHash = threatHash;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public Long getThreatId() {
        return threatId;
    }

    public void setThreatId(Long threatId) {
        this.threatId = threatId;
    }

    public BlockchainLogDTO(Long id, String event, String blockNumber, String txHash, String threatHash, String transactionHash, Object timestamp, Long threat) {
            this.id = id;
            this.event = event;
            this.blockNumber = blockNumber;
            this.txHash = txHash;
            this.threatHash = threatHash;
            this.transactionHash = transactionHash;
            this.timestamp = timestamp;
            this.threatId = threat;






        }
    }

