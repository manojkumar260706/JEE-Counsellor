package com.jee_counsellor.jee_counsellor.controller;

import com.jee_counsellor.jee_counsellor.dto.CollegeOptionResponse;
import com.jee_counsellor.jee_counsellor.service.CounsellingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Counselling", description = "JoSAA counselling prediction endpoints — retrieve eligible college options with difficulty predictions")
@RestController
@RequestMapping("/api/counselling")
public class CounsellingController {

    private final CounsellingService counsellingService;

    public CounsellingController(CounsellingService counsellingService) {
        this.counsellingService = counsellingService;
    }

    /**
     * Returns paginated college options for a candidate with difficulty predictions.
     * Filters cutoff data by the candidate's seat type, gender, home state (quota resolution),
     * and computes EASY / MEDIUM / REACH predictions based on their rank.
     */
    @Operation(
            summary = "Get College Options",
            description = "Returns paginated college options for the given candidate. "
                    + "Filters by seat type, gender eligibility, and quota (AI/HS/OS/GO/JK/LA). "
                    + "Each option includes an EASY/MEDIUM/REACH difficulty prediction based on "
                    + "the candidate's JEE Advanced rank (for IITs) or JEE Mains rank (for NITs/IIITs/GFTIs)."
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
}
