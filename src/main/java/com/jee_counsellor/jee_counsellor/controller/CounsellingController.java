package com.jee_counsellor.jee_counsellor.controller;

import com.jee_counsellor.jee_counsellor.dto.*;
import com.jee_counsellor.jee_counsellor.service.CounsellingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Counselling", description = "JoSAA counselling prediction, candidate option entry (choice filling), and round tracking endpoints")
@RestController
@RequestMapping("/api/counselling")
public class CounsellingController {

    private final CounsellingService counsellingService;

    public CounsellingController(CounsellingService counsellingService) {
        this.counsellingService = counsellingService;
    }

    /**
     * Returns the current counselling state (round 1-5 and active phase).
     */
    @Operation(summary = "Get Current Counselling State", description = "Retrieves the active counselling round (1 to 5) and phase (Choice Filling, Allotment Announced, etc.).")
    @GetMapping("/round")
    public ResponseEntity<CounsellingStateResponse> getCurrentRound() {
        return ResponseEntity.ok(counsellingService.getCounsellingState());
    }

    /**
     * Updates the current round and phase (admin / simulation control).
     */
    @Operation(summary = "Update Counselling Round and Phase", description = "Sets the current active round (1-5) and phase for counselling simulation.")
    @PutMapping("/round")
    public ResponseEntity<CounsellingStateResponse> updateCurrentRound(@Valid @RequestBody UpdateRoundRequest request) {
        return ResponseEntity.ok(counsellingService.updateCounsellingState(request.getRound(), request.getPhase()));
    }

    /**
     * Returns paginated college options for a candidate with difficulty predictions.
     */
    @Operation(
            summary = "Get College Options",
            description = "Returns paginated college options for the given candidate. "
                    + "Each unique (Institute, Program) is returned once with consolidated quota cutoffs "
                    + "and an overall difficulty prediction (EASY / MEDIUM / REACH)."
    )
    @GetMapping("/{candidateId}/options")
    public ResponseEntity<Page<CollegeOptionResponse>> getCollegeOptions(
            @PathVariable
            @Parameter(description = "ID of the logged-in candidate")
            Long candidateId,

            @RequestParam(defaultValue = "2026")
            @Parameter(description = "Cutoff year")
            Long year,

            @RequestParam(defaultValue = "1")
            @Parameter(description = "Counselling round (1-5)")
            Long round,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Page number (zero-based)")
            int page,

            @RequestParam(defaultValue = "20")
            @Parameter(description = "Page size")
            int size
    ) {
        Page<CollegeOptionResponse> options = counsellingService.getCollegeOptions(
                candidateId, year, round, page, size);
        return ResponseEntity.ok(options);
    }

    /**
     * Retrieves the candidate's saved choices in order of priority (1 = highest priority).
     */
    @Operation(summary = "Get Candidate Choices", description = "Retrieves the candidate's ranked preference list ordered by priority.")
    @GetMapping("/{candidateId}/choices")
    public ResponseEntity<List<CandidateChoiceResponse>> getCandidateChoices(
            @PathVariable
            @Parameter(description = "ID of the candidate")
            Long candidateId
    ) {
        return ResponseEntity.ok(counsellingService.getCandidateChoices(candidateId));
    }

    /**
     * Saves / replaces the candidate's choices list.
     * Priorities are automatically assigned sequentially (1, 2, 3...) based on list order.
     */
    @Operation(
            summary = "Save Candidate Choices",
            description = "Saves or updates the candidate's choice list in bulk. "
                    + "The list order determines priority (index 0 = Priority 1). "
                    + "Allowed only when the current phase is Choice Filling."
    )
    @PostMapping("/{candidateId}/choices")
    public ResponseEntity<List<CandidateChoiceResponse>> saveCandidateChoices(
            @PathVariable
            @Parameter(description = "ID of the candidate")
            Long candidateId,

            @Valid @RequestBody SaveChoicesRequest request
    ) {
        List<CandidateChoiceResponse> saved = counsellingService.saveCandidateChoices(candidateId, request.getChoices());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
