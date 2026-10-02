package com.jee_counsellor.jee_counsellor.service;

import com.jee_counsellor.jee_counsellor.dto.CollegeOptionResponse;
import com.jee_counsellor.jee_counsellor.dto.QuotaCutoffDetail;
import com.jee_counsellor.jee_counsellor.exception.ResourceNotFoundException;
import com.jee_counsellor.jee_counsellor.model.Candidate;
import com.jee_counsellor.jee_counsellor.model.Gender;
import com.jee_counsellor.jee_counsellor.model.JosaaCutoff;
import com.jee_counsellor.jee_counsellor.repository.CandidateRepository;
import com.jee_counsellor.jee_counsellor.repository.JosaaCutoffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CounsellingService {

    private static final String IIT_IDENTIFIER = "Indian Institute of Technology";

    private final CandidateRepository candidateRepository;
    private final JosaaCutoffRepository cutoffRepository;

    public CounsellingService(CandidateRepository candidateRepository,
                              JosaaCutoffRepository cutoffRepository) {
        this.candidateRepository = candidateRepository;
        this.cutoffRepository = cutoffRepository;
    }

    /**
     * Returns a paginated list of consolidated college options for the given candidate.
     * Each unique (Institute + Academic Program) is returned only once, with all matching
     * quota cutoffs attached, and an overall predicted difficulty level.
     *
     * @param candidateId the ID of the logged-in candidate
     * @param year        cutoff year (default 2026)
     * @param round       counselling round (default 1)
     * @param page        zero-based page index
     * @param size        page size
     * @return paginated CollegeOptionResponse with consolidated quota details and difficulty predictions
     */
    public Page<CollegeOptionResponse> getCollegeOptions(Long candidateId, Long year, Long round,
                                                          int page, int size) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate with ID " + candidateId + " not found."));

        // Determine applicable genders based on candidate's gender
        // Female candidates are eligible for both Gender-Neutral and Female-only seats
        // Gender-Neutral (male) candidates are only eligible for Gender-Neutral seats
        List<String> applicableGenders = resolveApplicableGenders(candidate.getGender());

        // Get candidate's seat type as stored in DB (display name format, e.g., "OBC-NCL")
        String seatType = candidate.getSeatType().getDisplayName();

        // Fetch all matching cutoff rows from DB
        List<JosaaCutoff> cutoffs = cutoffRepository.findAllApplicableCutoffs(
                year, round, seatType, applicableGenders, candidate.getHomeState());

        if (cutoffs == null || cutoffs.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }

        // Group cutoffs by (Institute + Academic Program Name)
        // LinkedHashMap preserves ranking order from SQL (sorted by closing rank ASC)
        Map<String, List<JosaaCutoff>> grouped = cutoffs.stream()
                .collect(Collectors.groupingBy(
                        c -> (c.getInstitute() != null ? c.getInstitute() : "") + "|||"
                           + (c.getAcademicProgramName() != null ? c.getAcademicProgramName() : ""),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Transform each group into a consolidated CollegeOptionResponse
        List<CollegeOptionResponse> consolidatedOptions = new ArrayList<>();
        for (List<JosaaCutoff> groupList : grouped.values()) {
            consolidatedOptions.add(buildConsolidatedOption(groupList, candidate));
        }

        // Apply pagination to consolidated list
        int totalElements = consolidatedOptions.size();
        int start = Math.min(page * size, totalElements);
        int end = Math.min(start + size, totalElements);
        List<CollegeOptionResponse> pagedList = consolidatedOptions.subList(start, end);

        return new PageImpl<>(pagedList, PageRequest.of(page, size), totalElements);
    }

    /**
     * Builds a consolidated CollegeOptionResponse from multiple cutoff rows
     * representing different quotas/genders for the same institute and program.
     */
    private CollegeOptionResponse buildConsolidatedOption(List<JosaaCutoff> cutoffs, Candidate candidate) {
        JosaaCutoff first = cutoffs.get(0);
        String institute = first.getInstitute();
        String programName = first.getAcademicProgramName();

        // Determine which rank to use:
        // IIT -> Advanced rank, everything else (NIT/IIIT/GFTI) -> Mains rank
        boolean isIIT = institute != null && institute.contains(IIT_IDENTIFIER);
        int candidateRank = isIIT ? candidate.getAdvanceRank() : candidate.getMainsRank();

        List<QuotaCutoffDetail> quotaDetails = new ArrayList<>();
        Integer bestClosingRank = null;
        String overallDifficulty = null;
        long totalSeats = 0L;

        for (JosaaCutoff cutoff : cutoffs) {
            Integer opRank = parseRank(cutoff.getOpeningRank());
            Integer clRank = parseRank(cutoff.getClosingRank());
            String difficulty = computeDifficulty(candidateRank, clRank);

            if (cutoff.getSeatsAvailable() != null) {
                totalSeats += cutoff.getSeatsAvailable();
            }

            quotaDetails.add(new QuotaCutoffDetail(
                    cutoff.getQuota(),
                    cutoff.getSeatType(),
                    cutoff.getGender(),
                    opRank,
                    clRank,
                    difficulty,
                    cutoff.getSeatsAvailable()
            ));

            // Track best closing rank (highest closing rank offers most favorable cutoff)
            if (clRank != null) {
                if (bestClosingRank == null || clRank > bestClosingRank) {
                    bestClosingRank = clRank;
                }
            }

            // Combine difficulty: EASY > MEDIUM > REACH
            overallDifficulty = combineDifficulty(overallDifficulty, difficulty);
        }

        return new CollegeOptionResponse(
                institute,
                programName,
                overallDifficulty != null ? overallDifficulty : "REACH",
                bestClosingRank,
                totalSeats,
                quotaDetails
        );
    }

    /**
     * Combines difficulty levels so the candidate sees their most favorable chance:
     * EASY > MEDIUM > REACH
     */
    private String combineDifficulty(String current, String next) {
        if (current == null) return next;
        if (next == null) return current;
        if ("EASY".equals(current) || "EASY".equals(next)) return "EASY";
        if ("MEDIUM".equals(current) || "MEDIUM".equals(next)) return "MEDIUM";
        return "REACH";
    }

    /**
     * Resolves which gender values the candidate is eligible for.
     * JoSAA rules:
     * - Female candidates -> eligible for both "Gender-Neutral" and "Female-only" seats
     * - Male/Gender-Neutral candidates -> eligible for "Gender-Neutral" seats only
     */
    private List<String> resolveApplicableGenders(Gender candidateGender) {
        if (candidateGender == Gender.FEMALE_ONLY) {
            return List.of(
                    Gender.GENDER_NEUTRAL.getDisplayName(),
                    Gender.FEMALE_ONLY.getDisplayName()
            );
        }
        return List.of(Gender.GENDER_NEUTRAL.getDisplayName());
    }

    /**
     * Computes difficulty prediction based on candidate rank vs closing rank.
     *
     * EASY   -> candidate rank <= 70% of closing rank (well within cutoff)
     * MEDIUM -> candidate rank <= closing rank (within cutoff)
     * REACH  -> candidate rank > closing rank (above cutoff, still worth trying)
     */
    private String computeDifficulty(int candidateRank, Integer closingRank) {
        if (closingRank == null || closingRank == 0) {
            return null;
        }
        if (candidateRank <= closingRank * 0.7) {
            return "EASY";
        } else if (candidateRank <= closingRank) {
            return "MEDIUM";
        } else {
            return "REACH";
        }
    }

    /**
     * Safely parses a rank string from the cutoff table.
     * Handles formats like "6859", "1173808.0".
     * Returns null for unparseable values.
     */
    private Integer parseRank(String rankStr) {
        if (rankStr == null || rankStr.isBlank()) {
            return null;
        }
        try {
            String cleaned = rankStr.strip();
            // Handle "1173808.0" format -> strip trailing ".0"
            if (cleaned.endsWith(".0")) {
                cleaned = cleaned.substring(0, cleaned.length() - 2);
            }
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
