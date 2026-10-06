package com.jee_counsellor.jee_counsellor;

import com.jee_counsellor.jee_counsellor.dto.*;
import com.jee_counsellor.jee_counsellor.exception.InvalidOperationException;
import com.jee_counsellor.jee_counsellor.exception.ResourceNotFoundException;
import com.jee_counsellor.jee_counsellor.model.*;
import com.jee_counsellor.jee_counsellor.repository.CandidateChoiceRepository;
import com.jee_counsellor.jee_counsellor.repository.CandidateRepository;
import com.jee_counsellor.jee_counsellor.repository.CounsellingStateRepository;
import com.jee_counsellor.jee_counsellor.repository.JosaaCutoffRepository;
import com.jee_counsellor.jee_counsellor.service.CounsellingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CounsellingServiceTest {

    private CandidateRepository candidateRepository;
    private JosaaCutoffRepository cutoffRepository;
    private CandidateChoiceRepository candidateChoiceRepository;
    private CounsellingStateRepository counsellingStateRepository;
    private CounsellingService counsellingService;

    @BeforeEach
    void setUp() {
        candidateRepository = mock(CandidateRepository.class);
        cutoffRepository = mock(JosaaCutoffRepository.class);
        candidateChoiceRepository = mock(CandidateChoiceRepository.class);
        counsellingStateRepository = mock(CounsellingStateRepository.class);
        counsellingService = new CounsellingService(
                candidateRepository,
                cutoffRepository,
                candidateChoiceRepository,
                counsellingStateRepository
        );
    }

    // --- College Option Prediction Tests ---

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
        assertEquals("EASY", option.getDifficulty());
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
        candidate.setMainsRank(500);
        candidate.setAdvanceRank(12000);
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
        iitCutoff.setClosingRank("10000");

        when(cutoffRepository.findAllApplicableCutoffs(any(), any(), any(), any(), any()))
                .thenReturn(List.of(iitCutoff));

        Page<CollegeOptionResponse> result = counsellingService.getCollegeOptions(3L, 2026L, 1L, 0, 10);
        CollegeOptionResponse option = result.getContent().get(0);

        assertEquals("REACH", option.getDifficulty());
    }

    @Test
    void testDifficultyPrediction_NonIIT_UsesMainsRank() {
        Candidate candidate = new Candidate();
        candidate.setId(4L);
        candidate.setMainsRank(3000);
        candidate.setAdvanceRank(20000);
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
        nitCutoff.setClosingRank("4000");

        when(cutoffRepository.findAllApplicableCutoffs(any(), any(), any(), any(), any()))
                .thenReturn(List.of(nitCutoff));

        Page<CollegeOptionResponse> result = counsellingService.getCollegeOptions(4L, 2026L, 1L, 0, 10);
        CollegeOptionResponse option = result.getContent().get(0);

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

        JosaaCutoff hsCutoff = new JosaaCutoff();
        hsCutoff.setInstitute("National Institute of Technology, Tiruchirappalli");
        hsCutoff.setAcademicProgramName("Computer Science and Engineering");
        hsCutoff.setQuota("HS");
        hsCutoff.setSeatType("OPEN");
        hsCutoff.setGender("Gender-Neutral");
        hsCutoff.setOpeningRank("1000");
        hsCutoff.setClosingRank("8000");
        hsCutoff.setSeatsAvailable(25L);

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

        assertEquals(1, result.getTotalElements());
        CollegeOptionResponse option = result.getContent().get(0);
        assertEquals("National Institute of Technology, Tiruchirappalli", option.getInstitute());
        assertEquals("Computer Science and Engineering", option.getAcademicProgramName());
        assertEquals("EASY", option.getDifficulty());
        assertEquals(8000, option.getBestClosingRank());
        assertEquals(50L, option.getTotalSeatsAvailable());
        assertEquals(2, option.getEligibleQuotas().size());
    }

    // --- Round & Counselling State Tests ---

    @Test
    void testGetCounsellingState_InitializesDefaultWhenEmpty() {
        when(counsellingStateRepository.findById(1L)).thenReturn(Optional.empty());
        when(counsellingStateRepository.save(any(CounsellingState.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CounsellingStateResponse state = counsellingService.getCounsellingState();

        assertNotNull(state);
        assertEquals(1, state.getCurrentRound());
        assertEquals(CounsellingPhase.CHOICE_FILLING, state.getPhase());
        assertTrue(state.isChoiceFillingOpen());
        verify(counsellingStateRepository).save(any(CounsellingState.class));
    }

    @Test
    void testUpdateCounsellingState_ValidRoundAndPhase() {
        CounsellingState existing = new CounsellingState(1L, 1, CounsellingPhase.CHOICE_FILLING, 2026L, LocalDateTime.now());
        when(counsellingStateRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(counsellingStateRepository.save(any(CounsellingState.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CounsellingStateResponse updated = counsellingService.updateCounsellingState(2, CounsellingPhase.ALLOTMENT_ANNOUNCED);

        assertEquals(2, updated.getCurrentRound());
        assertEquals(CounsellingPhase.ALLOTMENT_ANNOUNCED, updated.getPhase());
        assertFalse(updated.isChoiceFillingOpen());
    }

    @Test
    void testUpdateCounsellingState_InvalidRound_ThrowsException() {
        assertThrows(InvalidOperationException.class, () ->
                counsellingService.updateCounsellingState(0, CounsellingPhase.CHOICE_FILLING));
        assertThrows(InvalidOperationException.class, () ->
                counsellingService.updateCounsellingState(6, CounsellingPhase.CHOICE_FILLING));
    }

    // --- Candidate Choice Filling Tests ---

    @Test
    void testSaveCandidateChoices_SuccessSequentialPriorities() {
        Candidate candidate = new Candidate();
        candidate.setId(10L);
        when(candidateRepository.findById(10L)).thenReturn(Optional.of(candidate));

        CounsellingState state = new CounsellingState(1L, 1, CounsellingPhase.CHOICE_FILLING, 2026L, LocalDateTime.now());
        when(counsellingStateRepository.findById(1L)).thenReturn(Optional.of(state));

        when(candidateChoiceRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<ChoiceItemRequest> request = List.of(
                new ChoiceItemRequest("IIT Bombay", "Computer Science"),
                new ChoiceItemRequest("IIT Delhi", "Computer Science"),
                new ChoiceItemRequest("IIT Madras", "Electrical Engineering")
        );

        List<CandidateChoiceResponse> saved = counsellingService.saveCandidateChoices(10L, request);

        assertEquals(3, saved.size());
        assertEquals(1, saved.get(0).getPriorityOrder());
        assertEquals("IIT Bombay", saved.get(0).getInstitute());

        assertEquals(2, saved.get(1).getPriorityOrder());
        assertEquals("IIT Delhi", saved.get(1).getInstitute());

        assertEquals(3, saved.get(2).getPriorityOrder());
        assertEquals("IIT Madras", saved.get(2).getInstitute());

        verify(candidateChoiceRepository).deleteByCandidateId(10L);
        verify(candidateChoiceRepository).flush();
    }

    @Test
    void testSaveCandidateChoices_DuplicateChoiceInList_ThrowsException() {
        Candidate candidate = new Candidate();
        candidate.setId(10L);
        when(candidateRepository.findById(10L)).thenReturn(Optional.of(candidate));

        CounsellingState state = new CounsellingState(1L, 1, CounsellingPhase.CHOICE_FILLING, 2026L, LocalDateTime.now());
        when(counsellingStateRepository.findById(1L)).thenReturn(Optional.of(state));

        List<ChoiceItemRequest> request = List.of(
                new ChoiceItemRequest("IIT Bombay", "Computer Science"),
                new ChoiceItemRequest("IIT Bombay", "Computer Science") // duplicate!
        );

        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () ->
                counsellingService.saveCandidateChoices(10L, request));

        assertTrue(ex.getMessage().contains("Duplicate choice"));
    }

    @Test
    void testSaveCandidateChoices_WhenPhaseClosed_ThrowsException() {
        Candidate candidate = new Candidate();
        candidate.setId(10L);
        when(candidateRepository.findById(10L)).thenReturn(Optional.of(candidate));

        CounsellingState state = new CounsellingState(1L, 1, CounsellingPhase.ALLOTMENT_ANNOUNCED, 2026L, LocalDateTime.now());
        when(counsellingStateRepository.findById(1L)).thenReturn(Optional.of(state));

        List<ChoiceItemRequest> request = List.of(
                new ChoiceItemRequest("IIT Bombay", "Computer Science")
        );

        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () ->
                counsellingService.saveCandidateChoices(10L, request));

        assertTrue(ex.getMessage().contains("Choice filling is currently closed"));
    }

    @Test
    void testGetCandidateChoices_ReturnsOrderedChoices() {
        when(candidateRepository.existsById(10L)).thenReturn(true);

        CandidateChoice c1 = new CandidateChoice(null, 1, "IIT Bombay", "CSE");
        c1.setId(101L);
        CandidateChoice c2 = new CandidateChoice(null, 2, "IIT Delhi", "CSE");
        c2.setId(102L);

        when(candidateChoiceRepository.findByCandidateIdOrderByPriorityOrderAsc(10L))
                .thenReturn(List.of(c1, c2));

        List<CandidateChoiceResponse> choices = counsellingService.getCandidateChoices(10L);

        assertEquals(2, choices.size());
        assertEquals(1, choices.get(0).getPriorityOrder());
        assertEquals("IIT Bombay", choices.get(0).getInstitute());
        assertEquals(2, choices.get(1).getPriorityOrder());
        assertEquals("IIT Delhi", choices.get(1).getInstitute());
    }
}
