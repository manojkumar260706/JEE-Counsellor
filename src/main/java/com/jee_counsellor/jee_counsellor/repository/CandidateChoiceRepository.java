package com.jee_counsellor.jee_counsellor.repository;

import com.jee_counsellor.jee_counsellor.model.CandidateChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateChoiceRepository extends JpaRepository<CandidateChoice, Long> {

    List<CandidateChoice> findByCandidateIdOrderByPriorityOrderAsc(Long candidateId);

    @Modifying
    @Query("DELETE FROM CandidateChoice c WHERE c.candidate.id = :candidateId")
    void deleteByCandidateId(@Param("candidateId") Long candidateId);

    long countByCandidateId(Long candidateId);
}
