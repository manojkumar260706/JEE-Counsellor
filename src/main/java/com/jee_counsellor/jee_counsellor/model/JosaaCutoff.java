package com.jee_counsellor.jee_counsellor.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

/**
 * Read-only JPA entity mapped to the externally-created josaa_cutoffs_2026 table.
 * Column names are quoted because the table was created by a Python/pandas scraper
 * which preserves the original JoSAA column names (mixed case, spaces).
 */
@Entity
@Immutable
@Table(name = "josaa_cutoffs_2026")
public class JosaaCutoff {

    @Id
    private Long id;

    @Column(name = "\"Year\"")
    private Long year;

    @Column(name = "\"Round\"")
    private Long round;

    @Column(name = "\"Institute\"")
    private String institute;

    @Column(name = "\"Academic Program Name\"")
    private String academicProgramName;

    @Column(name = "\"Quota\"")
    private String quota;

    @Column(name = "\"Seat Type\"")
    private String seatType;

    @Column(name = "\"Gender\"")
    private String gender;

    @Column(name = "\"Opening Rank\"")
    private String openingRank;

    @Column(name = "\"Closing Rank\"")
    private String closingRank;

    @Column(name = "state")
    private String state;

    @Column(name = "\"Seats Available\"")
    private Long seatsAvailable;

    public JosaaCutoff() {
    }

    public Long getId() {
        return id;
    }

    public Long getYear() {
        return year;
    }

    public Long getRound() {
        return round;
    }

    public String getInstitute() {
        return institute;
    }

    public String getAcademicProgramName() {
        return academicProgramName;
    }

    public String getQuota() {
        return quota;
    }

    public String getSeatType() {
        return seatType;
    }

    public String getGender() {
        return gender;
    }

    public String getOpeningRank() {
        return openingRank;
    }

    public String getClosingRank() {
        return closingRank;
    }

    public String getState() {
        return state;
    }

    public Long getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public void setRound(Long round) {
        this.round = round;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public void setAcademicProgramName(String academicProgramName) {
        this.academicProgramName = academicProgramName;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOpeningRank(String openingRank) {
        this.openingRank = openingRank;
    }

    public void setClosingRank(String closingRank) {
        this.closingRank = closingRank;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setSeatsAvailable(Long seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }
}
