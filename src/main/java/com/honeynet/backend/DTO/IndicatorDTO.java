package com.honeynet.backend.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.honeynet.backend.Entity.IndicatorEntity;
import lombok.Data;

@Data
public class IndicatorDTO {

    private long id;
    private String indicator;
    private String type;
    private LocalDateTime created;

    @JsonProperty("content")
    private String content;
    private String title;

    private String description;
    private LocalDateTime expiration;

    @JsonProperty("is_active")
    private boolean isActive;

    private String role;

    public static String getIndicatorSummary(IndicatorDTO dto) {
        return dto.getIndicator() + " (" + dto.getType() + ")";
    }

    // Convert from Entity to DTO
    public static IndicatorDTO fromEntity(IndicatorEntity entity) {
        if (entity == null) return null;
        IndicatorDTO dto = new IndicatorDTO();
        dto.setId(entity.getId() != null ? entity.getId() : 0);
        dto.setIndicator(entity.getIndicator());
        dto.setType(entity.getType());
        dto.setCreated(entity.getCreated());
        dto.setContent(entity.getContent());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setExpiration(entity.getExpiration());
        dto.setActive(Boolean.TRUE.equals(entity.getActive()));
        dto.setRole(entity.getRole());
        return dto;
    }

    // Convert from DTO to Entity
    public IndicatorEntity toEntity() {
        IndicatorEntity entity = new IndicatorEntity();
        entity.setIndicator(this.getIndicator());
        entity.setType(this.getType());
        entity.setCreated(this.getCreated());
        entity.setContent(this.getContent());
        entity.setTitle(this.getTitle());
        entity.setDescription(this.getDescription());
        entity.setExpiration(this.getExpiration());
        entity.setActive(this.isActive());
        entity.setRole(this.getRole());
        return entity;
    }
}
