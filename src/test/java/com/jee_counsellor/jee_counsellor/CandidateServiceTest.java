package com.jee_counsellor.jee_counsellor;

import com.jee_counsellor.jee_counsellor.dto.*;
import com.jee_counsellor.jee_counsellor.exception.InvalidOperationException;
import com.jee_counsellor.jee_counsellor.exception.ResourceNotFoundException;
import com.jee_counsellor.jee_counsellor.model.Candidate;
import com.jee_counsellor.jee_counsellor.model.Gender;
import com.jee_counsellor.jee_counsellor.model.SeatType;
import com.jee_counsellor.jee_counsellor.repository.CandidateRepository;
import com.jee_counsellor.jee_counsellor.service.CandidateService;
import com.jee_counsellor.jee_counsellor.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CandidateServiceTest {

    private CandidateRepository candidateRepository;
    private CandidateService candidateService;

    @BeforeEach
    void setUp() {
        candidateRepository = mock(CandidateRepository.class);
        candidateService = new CandidateService(candidateRepository);
    }

    @Test
    void testCheckRollNo_WhenNew() {
        when(candidateRepository.findByRollNo("240101")).thenReturn(Optional.empty());

        CheckRollNoResponse response = candidateService.checkRollNo("240101");
        assertFalse(response.isExists());
        assertEquals("240101", response.getRollNo());
        assertTrue(response.getMessage().contains("Please provide registration details"));
    }

    @Test
    void testCheckRollNo_WhenExists() {
        Candidate candidate = new Candidate();
        candidate.setRollNo("240101");
        candidate.setName("Rahul Sharma");
        when(candidateRepository.findByRollNo("240101")).thenReturn(Optional.of(candidate));

        CheckRollNoResponse response = candidateService.checkRollNo("240101");
        assertTrue(response.isExists());
        assertEquals("240101", response.getRollNo());
        assertEquals("Rahul Sharma", response.getName());
        assertTrue(response.getMessage().contains("password"));
    }

    @Test
    void testRegisterAndLogin_Success() {
        RegisterCandidateRequest request = new RegisterCandidateRequest(
                "240101", "Priya Singh", "securePass123",
                1250, 450, "Female-only (including Supernumerary)", "OPEN", "Rajasthan"
        );

        when(candidateRepository.existsByRollNo("240101")).thenReturn(false);
        when(candidateRepository.existsByMainsRank(1250)).thenReturn(false);
        when(candidateRepository.existsByAdvanceRank(450)).thenReturn(false);
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = candidateService.registerAndLogin(request);
        assertTrue(response.isSuccess());
        assertNotNull(response.getCandidate());
        assertEquals("240101", response.getCandidate().getRollNo());
        assertEquals(Gender.FEMALE_ONLY, response.getCandidate().getGender());
        assertEquals(SeatType.OPEN, response.getCandidate().getSeatType());
        assertEquals("Rajasthan", response.getCandidate().getHomeState());
    }

    @Test
    void testRegister_DuplicateMainsRank_Throws() {
        RegisterCandidateRequest request = new RegisterCandidateRequest(
                "240102", "Amit", "pass", 100, 200, "Gender-Neutral", "OBC-NCL", "Delhi"
        );
        when(candidateRepository.existsByRollNo("240102")).thenReturn(false);
        when(candidateRepository.existsByMainsRank(100)).thenReturn(true);

        assertThrows(InvalidOperationException.class, () -> candidateService.registerAndLogin(request));
    }

    @Test
    void testRegister_InvalidState_Throws() {
        RegisterCandidateRequest request = new RegisterCandidateRequest(
                "240102", "Amit", "pass", 100, 200, "Gender-Neutral", "OBC-NCL", "NonExistentState"
        );
        when(candidateRepository.existsByRollNo("240102")).thenReturn(false);
        when(candidateRepository.existsByMainsRank(100)).thenReturn(false);
        when(candidateRepository.existsByAdvanceRank(200)).thenReturn(false);

        assertThrows(InvalidOperationException.class, () -> candidateService.registerAndLogin(request));
    }

    @Test
    void testLogin_Success() {
        Candidate candidate = new Candidate();
        candidate.setRollNo("240101");
        candidate.setName("Rahul");
        candidate.setPasswordHash(PasswordUtil.hashPassword("secret123"));

        when(candidateRepository.findByRollNo("240101")).thenReturn(Optional.of(candidate));

        AuthResponse response = candidateService.login(new LoginRequest("240101", "secret123"));
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("Login successful"));
    }

    @Test
    void testLogin_WrongPassword_Throws() {
        Candidate candidate = new Candidate();
        candidate.setRollNo("240101");
        candidate.setPasswordHash(PasswordUtil.hashPassword("secret123"));

        when(candidateRepository.findByRollNo("240101")).thenReturn(Optional.of(candidate));

        assertThrows(InvalidOperationException.class, () ->
                candidateService.login(new LoginRequest("240101", "wrongpassword")));
    }
}
