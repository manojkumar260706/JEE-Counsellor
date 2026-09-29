package com.jee_counsellor.jee_counsellor.controller;

import com.jee_counsellor.jee_counsellor.dto.*;
import com.jee_counsellor.jee_counsellor.model.Gender;
import com.jee_counsellor.jee_counsellor.model.HomeState;
import com.jee_counsellor.jee_counsellor.model.SeatType;
import com.jee_counsellor.jee_counsellor.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Tag(name = "Candidate Management", description = "Endpoints for candidate roll check, authentication, registration, and reference metadata")
@RestController
@RequestMapping("/api/candidate")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /**
     * Initial entry point: Candidate enters roll number.
     * Returns whether candidate already exists (Case 1) or is new (Case 2).
     */
    @Operation(summary = "Check Roll Number", description = "Checks whether a candidate with the given roll number exists. Directs to login (Case 1) or registration (Case 2).")
    @PostMapping("/check")
    public ResponseEntity<CheckRollNoResponse> checkRollNo(@Valid @RequestBody CheckRollNoRequest request) {
        CheckRollNoResponse response = candidateService.checkRollNo(request.getRollNo());
        return ResponseEntity.ok(response);
    }

    /**
     * CASE 1: Existing candidate provides roll number and password to log in.
     */
    @Operation(summary = "Candidate Login (Case 1)", description = "Logs in an existing candidate using their roll number and password.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = candidateService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * CASE 2: New candidate provides roll number, name, password, ranks, gender, seat type, home state.
     * Registers the candidate and immediately logs them in.
     */
    @Operation(summary = "Register New Candidate (Case 2)", description = "Registers a new candidate with unique JEE Mains rank, Advanced rank, gender, seat type, and home state. Automatically logs them in.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterCandidateRequest request) {
        AuthResponse response = candidateService.registerAndLogin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Helper endpoint to fetch metadata for dropdowns (33 states, genders, seat types).
     */
    @Operation(summary = "Get Reference Metadata", description = "Returns the allowed 33 states/UTs, 2 genders, and 5 seat types for frontend dropdowns.")
    @GetMapping("/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata() {
        return ResponseEntity.ok(Map.of(
            "states", HomeState.VALID_STATES,
            "genders", List.of(Gender.FEMALE_ONLY.getDisplayName(), Gender.GENDER_NEUTRAL.getDisplayName()),
            "seatTypes", Arrays.stream(SeatType.values()).map(SeatType::getDisplayName).toList()
        ));
    }
}

