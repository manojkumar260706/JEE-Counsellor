package com.jee_counsellor.jee_counsellor.dto;

import jakarta.validation.constraints.NotBlank;

public class ChoiceItemRequest {

    @NotBlank(message = "Institute cannot be blank")
    private String institute;

    @NotBlank(message = "Academic program name cannot be blank")
    private String academicProgramName;

    public ChoiceItemRequest() {
    }

    public ChoiceItemRequest(String institute, String academicProgramName) {
        this.institute = institute;
        this.academicProgramName = academicProgramName;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getAcademicProgramName() {
        return academicProgramName;
    }

    public void setAcademicProgramName(String academicProgramName) {
        this.academicProgramName = academicProgramName;
    }
}
