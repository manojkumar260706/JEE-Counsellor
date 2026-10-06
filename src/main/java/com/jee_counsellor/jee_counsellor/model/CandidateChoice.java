package com.jee_counsellor.jee_counsellor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_choices", uniqueConstraints = {
    @UniqueConstraint(name = "uk_candidate_priority", columnNames = {"candidate_id", "priority_order"}),
    @UniqueConstraint(name = "uk_candidate_option", columnNames = {"candidate_id", "institute", "academic_program_name"})
})
public class CandidateChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder;

    @Column(name = "institute", nullable = false)
    private String institute;

    @Column(name = "academic_program_name", nullable = false)
    private String academicProgramName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CandidateChoice() {
    }

    public CandidateChoice(Candidate candidate, Integer priorityOrder, String institute, String academicProgramName) {
        this.candidate = candidate;
        this.priorityOrder = priorityOrder;
        this.institute = institute;
        this.academicProgramName = academicProgramName;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Integer getPriorityOrder() {
        return priorityOrder;
    }

    public void setPriorityOrder(Integer priorityOrder) {
        this.priorityOrder = priorityOrder;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
