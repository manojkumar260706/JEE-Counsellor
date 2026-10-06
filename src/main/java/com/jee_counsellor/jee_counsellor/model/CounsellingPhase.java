package com.jee_counsellor.jee_counsellor.model;

/**
 * Represents the current phase within a JoSAA counselling round.
 */
public enum CounsellingPhase {
    CHOICE_FILLING("Choice Filling"),
    ALLOTMENT_ANNOUNCED("Seat Allotment Announced"),
    SEAT_ACCEPTANCE("Seat Acceptance and Reporting"),
    COMPLETED("Counselling Completed");

    private final String displayName;

    CounsellingPhase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
