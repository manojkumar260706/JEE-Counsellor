package com.jee_counsellor.jee_counsellor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterCandidateRequest {

    @NotBlank(message = "Roll number is required")
    private String rollNo;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "JEE Mains rank is required")
    @Min(value = 1, message = "JEE Mains rank must be at least 1")
    private Integer mainsRank;

    @NotNull(message = "JEE Advanced rank is required")
    @Min(value = 1, message = "JEE Advanced rank must be at least 1")
    private Integer advanceRank;

    @NotBlank(message = "Gender is required. Allowed: 'Female-only (including Supernumerary)' or 'Gender-Neutral'")
    private String gender;

    @NotBlank(message = "Seat type is required. Allowed: OPEN, ST, SC, EWS, OBC-NCL")
    private String seatType;

    @NotBlank(message = "Home state is required")
    private String homeState;

    public RegisterCandidateRequest() {
    }

    public RegisterCandidateRequest(String rollNo, String name, String password, Integer mainsRank, Integer advanceRank, String gender, String seatType, String homeState) {
        this.rollNo = rollNo;
        this.name = name;
        this.password = password;
        this.mainsRank = mainsRank;
        this.advanceRank = advanceRank;
        this.gender = gender;
        this.seatType = seatType;
        this.homeState = homeState;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMainsRank() {
        return mainsRank;
    }

    public void setMainsRank(Integer mainsRank) {
        this.mainsRank = mainsRank;
    }

    public Integer getAdvanceRank() {
        return advanceRank;
    }

    public void setAdvanceRank(Integer advanceRank) {
        this.advanceRank = advanceRank;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getHomeState() {
        return homeState;
    }

    public void setHomeState(String homeState) {
        this.homeState = homeState;
    }
}
