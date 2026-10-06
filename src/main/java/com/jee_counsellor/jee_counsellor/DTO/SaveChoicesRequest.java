package com.jee_counsellor.jee_counsellor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SaveChoicesRequest {

    @NotNull(message = "Choices list cannot be null")
    @NotEmpty(message = "At least one choice must be provided")
    private List<@Valid ChoiceItemRequest> choices;

    public SaveChoicesRequest() {
    }

    public SaveChoicesRequest(List<ChoiceItemRequest> choices) {
        this.choices = choices;
    }

    public List<ChoiceItemRequest> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceItemRequest> choices) {
        this.choices = choices;
    }
}
