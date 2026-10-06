package com.jee_counsellor.jee_counsellor.repository;

import com.jee_counsellor.jee_counsellor.model.CounsellingState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounsellingStateRepository extends JpaRepository<CounsellingState, Long> {
}
