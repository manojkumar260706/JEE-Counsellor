package com.jee_counsellor.jee_counsellor;

import com.jee_counsellor.jee_counsellor.dto.CollegeOptionResponse;
import com.jee_counsellor.jee_counsellor.dto.QuotaCutoffDetail;
import com.jee_counsellor.jee_counsellor.exception.ResourceNotFoundException;
import com.jee_counsellor.jee_counsellor.model.Candidate;
import com.jee_counsellor.jee_counsellor.model.Gender;
import com.jee_counsellor.jee_counsellor.model.JosaaCutoff;
import com.jee_counsellor.jee_counsellor.model.SeatType;
import com.jee_counsellor.jee_counsellor.repository.CandidateRepository;
import com.jee_counsellor.jee_counsellor.repository.JosaaCutoffRepository;
import com.jee_counsellor.jee_counsellor.service.CounsellingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CounsellingServiceTest {

    private CandidateRepository candidateRepository;
    private JosaaCutoffRepository cutoffRepository;
    private CounsellingService counsellingService;

    @BeforeEach
    void setUp() {
        candidateRepository = mock(CandidateRepository.class);
        cutoffRepository = mock(JosaaCutoffRepository.class);
        counsellingService = new CounsellingService(candidateRepository, cutoffRepository);
    }

    @Test
    void testGetCollegeOptions_CandidateNotFound() {
        when(candidateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                counsellingService.getCollegeOptions(999L, 2026L, 1L, 0, 10));
    }

    @Test
    void testGetCollegeOptions_MaleCandidate_ResolvesGenderNeutralOnly() {
        Candidate maleCandidate = new Candidate();
        maleCandidate.setId(1L);
        maleCandidate.setRollNo("2603100001");
        maleCandidate.setMainsRank(5000);
        maleCandidate.setAdvanceRank(3000);
        maleCandidate.setGender(Gender.GENDER_NEUTRAL);
        maleCandidate.setSeatType(SeatType.OPEN);
        maleCandidate.setHomeState("Karnataka");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(maleCandidate));

        JosaaCutoff cutoff = new JosaaCutoff();
        cutoff.setInstitute("National Institute of Technology Karnataka, Surathkal");
        cutoff.setAcademicProgramName("Computer Science");
        cutoff.setQuota("HS");
        cutoff.setSeatType("OPEN");
        cutoff.setGender("Gender-Neutral");
        cutoff.setOpeningRank("1000");
        cutoff.setClosingRank("8000");
        cutoff.setSeatsAvailable(20L);

        when(cutoffRepository.findAllApplicableCutoffs(
                eq(2026L), eq(1L), eq("OPEN"), eq(List.of("Gender-Neutral")),
                eq("Karnataka")))
                .thenReturn(List.of(cutoff));

        Page<CollegeOptionResponse> result = counsellingService.getCollegeOptions(1L, 2026L, 1L, 0, 10);

        assertEquals(1, result.getTotalElements());
        CollegeOptionResponse option = result.getContent().get(0);
        assertEquals("National Institute of Technology Karnataka, Surathkal", option.getInstitute());
        assertEquals("EASY", option.getDifficulty()); // 5000 <= 8000 * 0.7 (5600)
        assertEquals(1, option.getEligibleQuotas().size());
        assertEquals("HS", option.getEligibleQuotas().get(0).getQuota());
    }

    @Test
    void testGetCollegeOptions_FemaleCandidate_ResolvesBothGenders() {
        Candidate femaleCandidate = new Candidate();
        femaleCandidate.setId(2L);
        femaleCandidate.setRollNo("2603100002");
        femaleCandidate.setMainsRank(8000);
        femaleCandidate.setAdvanceRank(7500);
        femaleCandidate.setGender(Gender.FEMALE_ONLY);
        femaleCandidate.setSeatType(SeatType.OBC_NCL);
        femaleCandidate.setHomeState("Maharashtra");

        when(candidateRepository.findById(2L)).thenReturn(Optional.of(femaleCandidate));

        when(cutoffRepository.findAllApplicableCutoffs(
                eq(2026L), eq(1L), eq("OBC-NCL"),
                eq(List.of("Gender-Neutral", "Female-only (including Supernumerary)")),
                eq("Maharashtra")))
                .thenReturn(Collections.emptyList());

        Page<CollegeOptionResponse> result = counsellingService.getCollegeOptions(2L, 2026L, 1L, 0, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(cutoffRepository).findAllApplicableCutoffs(
                eq(2026L), eq(1L), eq("OBC-NCL"),
                eq(List.of("Gender-Neutral", "Female-only (including Supernumerary)")),
                eq("Maharashtra"));
    }

    @Test
    void testDifficultyPrediction_IIT_UsesAdvanceRank() {
        Candidate candidate = new Candidate();
        candidate.setId(3L);
        candidate.setMainsRank(500);     // Excellent mains rank
        candidate.setAdvanceRank(12000); // Higher advance rank
        candidate.setGender(Gender.GENDER_NEUTRAL);
        candidate.setSeatType(SeatType.OPEN);
        candidate.setHomeState("Delhi");

        when(candidateRepository.findById(3L)).thenReturn(Optional.of(candidate));

        JosaaCutoff iitCutoff = new JosaaCutoff();
        iitCutoff.setInstitute("Indian Institute of Technology Delhi");
        iitCutoff.setAcademicProgramName("Chemical Engineering");
        iitCutoff.setQuota("AI");
        iitCutoff.setSeatType("OPEN");
        iitCutoff.setGender("Gender-Neutral");
        iitCutoff.setOpeningRank("2000");
        iitCutoff.setClosingRank("10000"); // 12000 > 10000 -> REACH

        when(cutoffRepository.findAllApplicableCutoffs(any(), any(), any(), any(), any()))
                .thenReturn(List.of(iitCutoff));

        Page<CollegeOptionResponse> result = counsellingService.getCollegeOptions(3L, 2026L, 1L, 0, 10);
        CollegeOptionResponse option = result.getContent().get(0);

        // Candidate advanceRank (12000) > closingRank (10000) => REACH (even though mainsRank is 500)
        assertEquals("REACH", option.getDifficulty());
    }

    @Test
    void testDifficultyPrediction_NonIIT_UsesMainsRank() {
        Candidate candidate = new Candidate();
        candidate.setId(4L);
        candidate.setMainsRank(3000);   // Good mains rank
        candidate.setAdvanceRank(20000);// Weak advance rank
        candidate.setGender(Gender.GENDER_NEUTRAL);
        candidate.setSeatType(SeatType.OPEN);
        candidate.setHomeState("Tamil Nadu");

        when(candidateRepository.findById(4L)).thenReturn(Optional.of(candidate));

        JosaaCutoff nitCutoff = new JosaaCutoff();
        nitCutoff.setInstitute("National Institute of Technology, Tiruchirappalli");
        nitCutoff.setAcademicProgramName("Mechanical Engineering");
        nitCutoff.setQuota("HS");
        nitCutoff.setSeatType("OPEN");
        nitCutoff.setGender("Gender-Neutral");
        nitCutoff.setOpeningRank("2000");
        nitCutoff.setClosingRank("4000"); // 3000 <= 4000 and 3000 > 4000*0.7 (2800) -> MEDIUM

        when(cutoffRepository.findAllApplicableCutoffs(any(), any(), any(), any(), any()))
                .thenReturn(List.of(nitCutoff));

        Page<CollegeOptionResponse> result = counsellingService.getCollegeOptions(4L, 2026L, 1L, 0, 10);
        CollegeOptionResponse option = result.getContent().get(0);

        // Non-IIT uses mainsRank (3000): 3000 > 2800 and 3000 <= 4000 => MEDIUM
        assertEquals("MEDIUM", option.getDifficulty());
    }

    @Test
    void testMultipleQuotasForSameCollege_GroupedIntoSingleOptionWithAllQuotas() {
        Candidate candidate = new Candidate();
        candidate.setId(5L);
        candidate.setMainsRank(3500);
        candidate.setAdvanceRank(25000);
        candidate.setGender(Gender.GENDER_NEUTRAL);
        candidate.setSeatType(SeatType.OPEN);
        candidate.setHomeState("Tamil Nadu");

        when(candidateRepository.findById(5L)).thenReturn(Optional.of(candidate));

        // Cutoff 1: Home State quota (Closing rank 8000 -> 3500 <= 5600 -> EASY)
        JosaaCutoff hsCutoff = new JosaaCutoff();
        hsCutoff.setInstitute("National Institute of Technology, Tiruchirappalli");
        hsCutoff.setAcademicProgramName("Computer Science and Engineering");
        hsCutoff.setQuota("HS");
        hsCutoff.setSeatType("OPEN");
        hsCutoff.setGender("Gender-Neutral");
        hsCutoff.setOpeningRank("1000");
        hsCutoff.setClosingRank("8000");
        hsCutoff.setSeatsAvailable(25L);

        // Cutoff 2: Other State quota for same college (Closing rank 4000 -> 3500 <= 4000 -> MEDIUM)
        JosaaCutoff osCutoff = new JosaaCutoff();
        osCutoff.setInstitute("National Institute of Technology, Tiruchirappalli");
        osCutoff.setAcademicProgramName("Computer Science and Engineering");
        osCutoff.setQuota("OS");
        osCutoff.setSeatType("OPEN");
        osCutoff.setGender("Gender-Neutral");
        osCutoff.setOpeningRank("500");
        osCutoff.setClosingRank("4000");
        osCutoff.setSeatsAvailable(25L);

        when(cutoffRepository.findAllApplicableCutoffs(any(), any(), any(), any(), any()))
                .thenReturn(List.of(hsCutoff, osCutoff));

        Page<CollegeOptionResponse> result = counsellingService.getCollegeOptions(5L, 2026L, 1L, 0, 10);

        // Exactly 1 unique college option returned
        assertEquals(1, result.getTotalElements());

        CollegeOptionResponse option = result.getContent().get(0);
        assertEquals("National Institute of Technology, Tiruchirappalli", option.getInstitute());
        assertEquals("Computer Science and Engineering", option.getAcademicProgramName());

        // Overall difficulty is the most favorable one (EASY from HS quota)
        assertEquals("EASY", option.getDifficulty());
        assertEquals(8000, option.getBestClosingRank());
        assertEquals(50L, option.getTotalSeatsAvailable());

        // Contains both quota cutoffs
        assertEquals(2, option.getEligibleQuotas().size());

        QuotaCutoffDetail firstQuota = option.getEligibleQuotas().get(0);
        assertEquals("HS", firstQuota.getQuota());
        assertEquals(8000, firstQuota.getClosingRank());
        assertEquals("EASY", firstQuota.getDifficulty());

        QuotaCutoffDetail secondQuota = option.getEligibleQuotas().get(1);
        assertEquals("OS", secondQuota.getQuota());
        assertEquals(4000, secondQuota.getClosingRank());
        assertEquals("MEDIUM", secondQuota.getDifficulty());
    }
}
