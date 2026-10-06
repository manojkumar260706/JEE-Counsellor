package com.jee_counsellor.jee_counsellor.dto;

import com.jee_counsellor.jee_counsellor.model.CandidateChoice;
import java.time.LocalDateTime;

public class CandidateChoiceResponse {

    private Long id;
    private Integer priorityOrder;
    private String institute;
    private String academicProgramName;
    private LocalDateTime createdAt;

    public CandidateChoiceResponse() {
    }

    public CandidateChoiceResponse(Long id, Integer priorityOrder, String institute, String academicProgramName, LocalDateTime createdAt) {
        this.id = id;
        this.priorityOrder = priorityOrder;
        this.institute = institute;
        this.academicProgramName = academicProgramName;
        this.createdAt = createdAt;
    }

    public static CandidateChoiceResponse fromEntity(CandidateChoice entity) {
        if (entity == null) return null;
        return new CandidateChoiceResponse(
                entity.getId(),
                entity.getPriorityOrder(),
                entity.getInstitute(),
                entity.getAcademicProgramName(),
                entity.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
