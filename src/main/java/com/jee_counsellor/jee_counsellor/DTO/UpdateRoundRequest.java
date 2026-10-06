package com.jee_counsellor.jee_counsellor.dto;

import com.jee_counsellor.jee_counsellor.model.CounsellingPhase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateRoundRequest {

    @NotNull(message = "Round cannot be null")
    @Min(value = 1, message = "Round must be between 1 and 5")
    @Max(value = 5, message = "Round must be between 1 and 5")
    private Integer round;

    @NotNull(message = "Phase cannot be null")
    private CounsellingPhase phase;

    public UpdateRoundRequest() {
    }

    public UpdateRoundRequest(Integer round, CounsellingPhase phase) {
        this.round = round;
        this.phase = phase;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public CounsellingPhase getPhase() {
        return phase;
    }

    public void setPhase(CounsellingPhase phase) {
        this.phase = phase;
    }
}
