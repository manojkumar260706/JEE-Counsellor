package com.jee_counsellor.jee_counsellor.dto;

import com.jee_counsellor.jee_counsellor.model.CounsellingPhase;
import com.jee_counsellor.jee_counsellor.model.CounsellingState;
import java.time.LocalDateTime;

public class CounsellingStateResponse {

    private Integer currentRound;
    private CounsellingPhase phase;
    private String phaseDisplayName;
    private Long academicYear;
    private boolean choiceFillingOpen;
    private LocalDateTime updatedAt;

    public CounsellingStateResponse() {
    }

    public CounsellingStateResponse(Integer currentRound, CounsellingPhase phase, String phaseDisplayName, Long academicYear, boolean choiceFillingOpen, LocalDateTime updatedAt) {
        this.currentRound = currentRound;
        this.phase = phase;
        this.phaseDisplayName = phaseDisplayName;
        this.academicYear = academicYear;
        this.choiceFillingOpen = choiceFillingOpen;
        this.updatedAt = updatedAt;
    }

    public static CounsellingStateResponse fromEntity(CounsellingState state) {
        if (state == null) return null;
        boolean isOpen = state.getPhase() == CounsellingPhase.CHOICE_FILLING;
        return new CounsellingStateResponse(
                state.getCurrentRound(),
                state.getPhase(),
                state.getPhase().getDisplayName(),
                state.getAcademicYear(),
                isOpen,
                state.getUpdatedAt()
        );
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public CounsellingPhase getPhase() {
        return phase;
    }

    public void setPhase(CounsellingPhase phase) {
        this.phase = phase;
    }

    public String getPhaseDisplayName() {
        return phaseDisplayName;
    }

    public void setPhaseDisplayName(String phaseDisplayName) {
        this.phaseDisplayName = phaseDisplayName;
    }

    public Long getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Long academicYear) {
        this.academicYear = academicYear;
    }

    public boolean isChoiceFillingOpen() {
        return choiceFillingOpen;
    }

    public void setChoiceFillingOpen(boolean choiceFillingOpen) {
        this.choiceFillingOpen = choiceFillingOpen;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
