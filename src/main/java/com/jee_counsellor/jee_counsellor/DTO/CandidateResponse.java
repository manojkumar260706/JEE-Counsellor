package com.jee_counsellor.jee_counsellor.dto;

import com.jee_counsellor.jee_counsellor.model.Candidate;
import com.jee_counsellor.jee_counsellor.model.Gender;
import com.jee_counsellor.jee_counsellor.model.SeatType;

import java.time.LocalDateTime;

public class CandidateResponse {

    private Long id;
    private String rollNo;
    private String name;
    private Integer mainsRank;
    private Integer advanceRank;
    private Gender gender;
    private SeatType seatType;
    private String homeState;
    private LocalDateTime createdAt;

    public CandidateResponse() {
    }

    public CandidateResponse(Long id, String rollNo, String name, Integer mainsRank, Integer advanceRank, Gender gender, SeatType seatType, String homeState, LocalDateTime createdAt) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.mainsRank = mainsRank;
        this.advanceRank = advanceRank;
        this.gender = gender;
        this.seatType = seatType;
        this.homeState = homeState;
        this.createdAt = createdAt;
    }

    public static CandidateResponse fromEntity(Candidate candidate) {
        if (candidate == null) return null;
        return new CandidateResponse(
            candidate.getId(),
            candidate.getRollNo(),
            candidate.getName(),
            candidate.getMainsRank(),
            candidate.getAdvanceRank(),
            candidate.getGender(),
            candidate.getSeatType(),
            candidate.getHomeState(),
            candidate.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public String getHomeState() {
        return homeState;
    }

    public void setHomeState(String homeState) {
        this.homeState = homeState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
