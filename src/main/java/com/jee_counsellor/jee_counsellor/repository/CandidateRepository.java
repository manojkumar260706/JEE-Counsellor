package com.jee_counsellor.jee_counsellor.repository;

import com.jee_counsellor.jee_counsellor.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByRollNo(String rollNo);
    boolean existsByRollNo(String rollNo);
    boolean existsByMainsRank(Integer mainsRank);
    boolean existsByAdvanceRank(Integer advanceRank);
}
