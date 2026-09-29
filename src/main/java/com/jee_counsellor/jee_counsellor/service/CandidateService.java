package com.jee_counsellor.jee_counsellor.service;

import com.jee_counsellor.jee_counsellor.dto.*;
import com.jee_counsellor.jee_counsellor.exception.InvalidOperationException;
import com.jee_counsellor.jee_counsellor.exception.ResourceNotFoundException;
import com.jee_counsellor.jee_counsellor.model.Candidate;
import com.jee_counsellor.jee_counsellor.model.Gender;
import com.jee_counsellor.jee_counsellor.model.HomeState;
import com.jee_counsellor.jee_counsellor.model.SeatType;
import com.jee_counsellor.jee_counsellor.repository.CandidateRepository;
import com.jee_counsellor.jee_counsellor.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public CheckRollNoResponse checkRollNo(String rollNo) {
        if (rollNo == null || rollNo.trim().isEmpty()) {
            throw new InvalidOperationException("Roll number cannot be empty");
        }
        String cleanedRollNo = rollNo.trim();
        return candidateRepository.findByRollNo(cleanedRollNo)
                .map(c -> CheckRollNoResponse.existing(c.getRollNo(), c.getName()))
                .orElseGet(() -> CheckRollNoResponse.newCandidate(cleanedRollNo));
    }

    public AuthResponse login(LoginRequest request) {
        String rollNo = request.getRollNo().trim();
        Candidate candidate = candidateRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate with roll number '" + rollNo + "' not found."));

        boolean isMatch = PasswordUtil.verifyPassword(request.getPassword(), candidate.getPasswordHash());
        if (!isMatch) {
            throw new InvalidOperationException("Invalid password. Please check your credentials.");
        }

        return AuthResponse.success("Login successful. Welcome back, " + candidate.getName() + "!",
                CandidateResponse.fromEntity(candidate));
    }

    @Transactional
    public AuthResponse registerAndLogin(RegisterCandidateRequest request) {
        String rollNo = request.getRollNo().trim();

        if (candidateRepository.existsByRollNo(rollNo)) {
            throw new InvalidOperationException("Candidate with roll number '" + rollNo + "' is already registered. Please log in.");
        }

        if (candidateRepository.existsByMainsRank(request.getMainsRank())) {
            throw new InvalidOperationException("A candidate with JEE Mains rank " + request.getMainsRank() + " is already registered. JEE Mains rank must be unique.");
        }

        if (candidateRepository.existsByAdvanceRank(request.getAdvanceRank())) {
            throw new InvalidOperationException("A candidate with JEE Advanced rank " + request.getAdvanceRank() + " is already registered. JEE Advanced rank must be unique.");
        }

        Gender gender;
        try {
            gender = Gender.fromString(request.getGender());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException(e.getMessage());
        }

        SeatType seatType;
        try {
            seatType = SeatType.fromString(request.getSeatType());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException(e.getMessage());
        }

        String homeState;
        try {
            homeState = HomeState.normalizeAndValidate(request.getHomeState());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException(e.getMessage());
        }

        String passwordHash = PasswordUtil.hashPassword(request.getPassword());

        Candidate candidate = new Candidate();
        candidate.setRollNo(rollNo);
        candidate.setName(request.getName().trim());
        candidate.setPasswordHash(passwordHash);
        candidate.setMainsRank(request.getMainsRank());
        candidate.setAdvanceRank(request.getAdvanceRank());
        candidate.setGender(gender);
        candidate.setSeatType(seatType);
        candidate.setHomeState(homeState);

        Candidate saved = candidateRepository.save(candidate);

        return AuthResponse.success("Registration successful. You are now logged in!",
                CandidateResponse.fromEntity(saved));
    }
}
