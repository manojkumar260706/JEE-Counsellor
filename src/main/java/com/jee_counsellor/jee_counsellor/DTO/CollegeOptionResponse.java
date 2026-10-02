package com.jee_counsellor.jee_counsellor.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO representing a unique college branch option with consolidated
 * quota cutoffs and an overall predicted difficulty.
 */
public class CollegeOptionResponse {

    private String institute;
    private String academicProgramName;
    private String difficulty;
    private Integer bestClosingRank;
    private Long totalSeatsAvailable;
    private List<QuotaCutoffDetail> eligibleQuotas = new ArrayList<>();

    // Fields maintained for backwards compatibility
    private String quota;
    private String seatType;
    private String gender;
    private Integer openingRank;
    private Integer closingRank;
    private Long seatsAvailable;

    public CollegeOptionResponse() {
    }

    public CollegeOptionResponse(String institute, String academicProgramName, String difficulty,
                                 Integer bestClosingRank, Long totalSeatsAvailable,
                                 List<QuotaCutoffDetail> eligibleQuotas) {
        this.institute = institute;
        this.academicProgramName = academicProgramName;
        this.difficulty = difficulty;
        this.bestClosingRank = bestClosingRank;
        this.totalSeatsAvailable = totalSeatsAvailable;
        this.seatsAvailable = totalSeatsAvailable;
        this.eligibleQuotas = eligibleQuotas != null ? eligibleQuotas : new ArrayList<>();
        if (!this.eligibleQuotas.isEmpty()) {
            QuotaCutoffDetail primary = this.eligibleQuotas.get(0);
            this.quota = primary.getQuota();
            this.seatType = primary.getSeatType();
            this.gender = primary.getGender();
            this.openingRank = primary.getOpeningRank();
            this.closingRank = this.bestClosingRank != null ? this.bestClosingRank : primary.getClosingRank();
        }
    }

    public CollegeOptionResponse(String institute, String academicProgramName, String quota,
                                  String seatType, String gender, Integer openingRank,
                                  Integer closingRank, String difficulty, Long seatsAvailable) {
        this.institute = institute;
        this.academicProgramName = academicProgramName;
        this.quota = quota;
        this.seatType = seatType;
        this.gender = gender;
        this.openingRank = openingRank;
        this.closingRank = closingRank;
        this.difficulty = difficulty;
        this.seatsAvailable = seatsAvailable;
        this.totalSeatsAvailable = seatsAvailable;
        this.bestClosingRank = closingRank;
        this.eligibleQuotas = new ArrayList<>();
        if (quota != null) {
            this.eligibleQuotas.add(new QuotaCutoffDetail(quota, seatType, gender, openingRank, closingRank, difficulty, seatsAvailable));
        }
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getBestClosingRank() {
        return bestClosingRank;
    }

    public void setBestClosingRank(Integer bestClosingRank) {
        this.bestClosingRank = bestClosingRank;
    }

    public Long getTotalSeatsAvailable() {
        return totalSeatsAvailable;
    }

    public void setTotalSeatsAvailable(Long totalSeatsAvailable) {
        this.totalSeatsAvailable = totalSeatsAvailable;
        this.seatsAvailable = totalSeatsAvailable;
    }

    public List<QuotaCutoffDetail> getEligibleQuotas() {
        return eligibleQuotas;
    }

    public void setEligibleQuotas(List<QuotaCutoffDetail> eligibleQuotas) {
        this.eligibleQuotas = eligibleQuotas;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getOpeningRank() {
        return openingRank;
    }

    public void setOpeningRank(Integer openingRank) {
        this.openingRank = openingRank;
    }

    public Integer getClosingRank() {
        return closingRank;
    }

    public void setClosingRank(Integer closingRank) {
        this.closingRank = closingRank;
    }

    public Long getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(Long seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
        this.totalSeatsAvailable = seatsAvailable;
    }
}
