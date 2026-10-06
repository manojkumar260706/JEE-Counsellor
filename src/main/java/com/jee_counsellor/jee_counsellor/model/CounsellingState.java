package com.jee_counsellor.jee_counsellor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "counselling_state")
public class CounsellingState {

    @Id
    private Long id = 1L;

    @Column(name = "current_round", nullable = false)
    private Integer currentRound = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false)
    private CounsellingPhase phase = CounsellingPhase.CHOICE_FILLING;

    @Column(name = "academic_year", nullable = false)
    private Long academicYear = 2026L;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CounsellingState() {
    }

    public CounsellingState(Long id, Integer currentRound, CounsellingPhase phase, Long academicYear, LocalDateTime updatedAt) {
        this.id = id;
        this.currentRound = currentRound;
        this.phase = phase;
        this.academicYear = academicYear;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public CounsellingPhase getPhase() {
        return phase;
    }

    public void setPhase(CounsellingPhase phase) {
        this.phase = phase;
    }

    public Long getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Long academicYear) {
        this.academicYear = academicYear;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
