package com.jee_counsellor.jee_counsellor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SeatType {
    OPEN("OPEN"),
    ST("ST"),
    SC("SC"),
    EWS("EWS"),
    OBC_NCL("OBC-NCL");

    private final String displayName;

    SeatType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static SeatType fromString(String value) {
        if (value == null) return null;
        for (SeatType type : SeatType.values()) {
            if (type.displayName.equalsIgnoreCase(value.trim()) || type.name().equalsIgnoreCase(value.trim().replace("-", "_"))) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid seat type: '" + value + "'. Allowed values: OPEN, ST, SC, EWS, OBC-NCL");
    }
}
