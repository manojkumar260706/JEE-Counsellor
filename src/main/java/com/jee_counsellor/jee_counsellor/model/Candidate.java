package com.jee_counsellor.jee_counsellor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates", uniqueConstraints = {
    @UniqueConstraint(columnNames = "roll_no"),
    @UniqueConstraint(columnNames = "mains_rank"),
    @UniqueConstraint(columnNames = "advance_rank")
})
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_no", nullable = false, unique = true)
    private String rollNo;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "mains_rank", nullable = false, unique = true)
    private Integer mainsRank;

    @Column(name = "advance_rank", nullable = false, unique = true)
    private Integer advanceRank;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(name = "home_state", nullable = false)
    private String homeState;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Candidate() {
    }

    public Candidate(Long id, String rollNo, String name, String passwordHash, Integer mainsRank, Integer advanceRank, Gender gender, SeatType seatType, String homeState, LocalDateTime createdAt) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.passwordHash = passwordHash;
        this.mainsRank = mainsRank;
        this.advanceRank = advanceRank;
        this.gender = gender;
        this.seatType = seatType;
        this.homeState = homeState;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
