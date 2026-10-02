package com.jee_counsellor.jee_counsellor.dto;

/**
 * Details of a specific quota and category cutoff for a college branch option.
 */
public class QuotaCutoffDetail {

    private String quota;
    private String seatType;
    private String gender;
    private Integer openingRank;
    private Integer closingRank;
    private String difficulty;
    private Long seatsAvailable;

    public QuotaCutoffDetail() {
    }

    public QuotaCutoffDetail(String quota, String seatType, String gender,
                             Integer openingRank, Integer closingRank,
                             String difficulty, Long seatsAvailable) {
        this.quota = quota;
        this.seatType = seatType;
        this.gender = gender;
        this.openingRank = openingRank;
        this.closingRank = closingRank;
        this.difficulty = difficulty;
        this.seatsAvailable = seatsAvailable;
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Long getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(Long seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }
}
