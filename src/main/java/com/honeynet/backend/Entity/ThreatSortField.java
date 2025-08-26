package com.honeynet.backend.entity;

public enum ThreatSortField {
    ID("id"),
    TITLE("name"),
    RISK_SCORE("riskLevel"),
    GENERATED_AT("created");
//    MODIFIED("modified");

    private final String field;

    ThreatSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public static ThreatSortField fromString(String str) {
        for (ThreatSortField f : values()) {
            if (f.name().equalsIgnoreCase(str) || f.field.equalsIgnoreCase(str)) {
                return f;
            }
        }
        // default fallback
        return GENERATED_AT;
    }
}