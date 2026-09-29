package com.jee_counsellor.jee_counsellor.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckRollNoRequest {

    @NotBlank(message = "Roll number is required")
    private String rollNo;

    public CheckRollNoRequest() {
    }

    public CheckRollNoRequest(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }
}
