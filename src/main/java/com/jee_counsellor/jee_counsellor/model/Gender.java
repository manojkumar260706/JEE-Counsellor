package com.jee_counsellor.jee_counsellor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    FEMALE_ONLY("Female-only (including Supernumerary)"),
    GENDER_NEUTRAL("Gender-Neutral");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) return null;
        for (Gender g : Gender.values()) {
            if (g.displayName.equalsIgnoreCase(value.trim()) || g.name().equalsIgnoreCase(value.trim())) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid gender: '" + value + "'. Allowed values: 'Female-only (including Supernumerary)' or 'Gender-Neutral'");
    }
}
