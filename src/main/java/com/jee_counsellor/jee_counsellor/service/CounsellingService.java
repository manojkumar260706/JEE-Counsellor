package com.jee_counsellor.jee_counsellor.service;

import com.jee_counsellor.jee_counsellor.dto.*;
import com.jee_counsellor.jee_counsellor.exception.InvalidOperationException;
import com.jee_counsellor.jee_counsellor.exception.ResourceNotFoundException;
import com.jee_counsellor.jee_counsellor.model.*;
import com.jee_counsellor.jee_counsellor.repository.CandidateChoiceRepository;
import com.jee_counsellor.jee_counsellor.repository.CandidateRepository;
import com.jee_counsellor.jee_counsellor.repository.CounsellingStateRepository;
import com.jee_counsellor.jee_counsellor.repository.JosaaCutoffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CounsellingService {

    private static final String IIT_IDENTIFIER = "Indian Institute of Technology";

    private final CandidateRepository candidateRepository;
    private final JosaaCutoffRepository cutoffRepository;
    private final CandidateChoiceRepository candidateChoiceRepository;
    private final CounsellingStateRepository counsellingStateRepository;

    @Autowired
    public CounsellingService(CandidateRepository candidateRepository,
                              JosaaCutoffRepository cutoffRepository,
                              CandidateChoiceRepository candidateChoiceRepository,
                              CounsellingStateRepository counsellingStateRepository) {
        this.candidateRepository = candidateRepository;
        this.cutoffRepository = cutoffRepository;
        this.candidateChoiceRepository = candidateChoiceRepository;
        this.counsellingStateRepository = counsellingStateRepository;
    }

    /**
     * Retrieves the current counselling state (round and phase).
     * If no state exists in database, initializes default state (Round 1, CHOICE_FILLING).
     */
    @Transactional
    public CounsellingStateResponse getCounsellingState() {
        CounsellingState state = getOrCreateCounsellingStateEntity();
        return CounsellingStateResponse.fromEntity(state);
    }

    /**
     * Updates the current counselling round (1-5) and phase.
     */
    @Transactional
    public CounsellingStateResponse updateCounsellingState(Integer round, CounsellingPhase phase) {
        if (round == null || round < 1 || round > 5) {
            throw new InvalidOperationException("Round must be between 1 and 5.");
        }
        if (phase == null) {
            throw new InvalidOperationException("Phase cannot be null.");
        }

        CounsellingState state = getOrCreateCounsellingStateEntity();
        state.setCurrentRound(round);
        state.setPhase(phase);
        state.setUpdatedAt(LocalDateTime.now());
        CounsellingState saved = counsellingStateRepository.save(state);
        return CounsellingStateResponse.fromEntity(saved);
    }

    /**
     * Returns candidate's saved choices in ascending priority order (1 = highest priority).
     */
    @Transactional(readOnly = true)
    public List<CandidateChoiceResponse> getCandidateChoices(Long candidateId) {
        validateCandidateExists(candidateId);
        List<CandidateChoice> choices = candidateChoiceRepository.findByCandidateIdOrderByPriorityOrderAsc(candidateId);
        return choices.stream().map(CandidateChoiceResponse::fromEntity).toList();
    }

    /**
     * Saves / replaces the candidate's entire choice list in bulk.
     * Priorities are automatically assigned sequentially (1, 2, 3... N) based on list order.
     */
    @Transactional
    public List<CandidateChoiceResponse> saveCandidateChoices(Long candidateId, List<ChoiceItemRequest> choiceRequests) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate with ID " + candidateId + " not found."));

        CounsellingState state = getOrCreateCounsellingStateEntity();
        if (state.getPhase() != CounsellingPhase.CHOICE_FILLING) {
            throw new InvalidOperationException("Choice filling is currently closed. Current phase: " + state.getPhase().getDisplayName());
        }

        if (choiceRequests == null || choiceRequests.isEmpty()) {
            throw new InvalidOperationException("Choices list cannot be empty.");
        }

        // Validate duplicates within the submitted list
        Set<String> seenOptions = new HashSet<>();
        for (int i = 0; i < choiceRequests.size(); i++) {
            ChoiceItemRequest item = choiceRequests.get(i);
            if (item.getInstitute() == null || item.getInstitute().isBlank()) {
                throw new InvalidOperationException("Choice at position " + (i + 1) + " must specify an institute.");
            }
            if (item.getAcademicProgramName() == null || item.getAcademicProgramName().isBlank()) {
                throw new InvalidOperationException("Choice at position " + (i + 1) + " must specify an academic program.");
            }

            String key = item.getInstitute().strip().toLowerCase() + "|||" + item.getAcademicProgramName().strip().toLowerCase();
            if (!seenOptions.add(key)) {
                throw new InvalidOperationException("Duplicate choice detected in preference list: "
                        + item.getInstitute() + " - " + item.getAcademicProgramName());
            }
        }

        // Replace existing choices atomically
        candidateChoiceRepository.deleteByCandidateId(candidateId);
        candidateChoiceRepository.flush();

        List<CandidateChoice> newChoices = new ArrayList<>();
        for (int i = 0; i < choiceRequests.size(); i++) {
            ChoiceItemRequest req = choiceRequests.get(i);
            newChoices.add(new CandidateChoice(
                    candidate,
                    i + 1, // 1-based sequential priority
                    req.getInstitute().strip(),
                    req.getAcademicProgramName().strip()
            ));
        }

        List<CandidateChoice> saved = candidateChoiceRepository.saveAll(newChoices);
        return saved.stream().map(CandidateChoiceResponse::fromEntity).toList();
    }

    private CounsellingState getOrCreateCounsellingStateEntity() {
        return counsellingStateRepository.findById(1L).orElseGet(() -> {
            CounsellingState defaultState = new CounsellingState(1L, 1, CounsellingPhase.CHOICE_FILLING, 2026L, LocalDateTime.now());
            return counsellingStateRepository.save(defaultState);
        });
    }

    private void validateCandidateExists(Long candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new ResourceNotFoundException("Candidate with ID " + candidateId + " not found.");
        }
    }

    /**
     * Returns a paginated list of consolidated college options for the given candidate.
     * Each unique (Institute + Academic Program) is returned only once, with all matching
     * quota cutoffs attached, and an overall predicted difficulty level.
     */
    public Page<CollegeOptionResponse> getCollegeOptions(Long candidateId, Long year, Long round,
                                                          int page, int size) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate with ID " + candidateId + " not found."));

        List<String> applicableGenders = resolveApplicableGenders(candidate.getGender());
        String seatType = candidate.getSeatType().getDisplayName();

        List<JosaaCutoff> cutoffs = cutoffRepository.findAllApplicableCutoffs(
                year, round, seatType, applicableGenders, candidate.getHomeState());

        if (cutoffs == null || cutoffs.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }

        Map<String, List<JosaaCutoff>> grouped = cutoffs.stream()
                .collect(Collectors.groupingBy(
                        c -> (c.getInstitute() != null ? c.getInstitute() : "") + "|||"
                           + (c.getAcademicProgramName() != null ? c.getAcademicProgramName() : ""),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<CollegeOptionResponse> consolidatedOptions = new ArrayList<>();
        for (List<JosaaCutoff> groupList : grouped.values()) {
            consolidatedOptions.add(buildConsolidatedOption(groupList, candidate));
        }

        int totalElements = consolidatedOptions.size();
        int start = Math.min(page * size, totalElements);
        int end = Math.min(start + size, totalElements);
        List<CollegeOptionResponse> pagedList = consolidatedOptions.subList(start, end);

        return new PageImpl<>(pagedList, PageRequest.of(page, size), totalElements);
    }

    private CollegeOptionResponse buildConsolidatedOption(List<JosaaCutoff> cutoffs, Candidate candidate) {
        JosaaCutoff first = cutoffs.get(0);
        String institute = first.getInstitute();
        String programName = first.getAcademicProgramName();

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

            if (clRank != null) {
                if (bestClosingRank == null || clRank > bestClosingRank) {
                    bestClosingRank = clRank;
                }
            }

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

    private String combineDifficulty(String current, String next) {
        if (current == null) return next;
        if (next == null) return current;
        if ("EASY".equals(current) || "EASY".equals(next)) return "EASY";
        if ("MEDIUM".equals(current) || "MEDIUM".equals(next)) return "MEDIUM";
        return "REACH";
    }

    private List<String> resolveApplicableGenders(Gender candidateGender) {
        if (candidateGender == Gender.FEMALE_ONLY) {
            return List.of(
                    Gender.GENDER_NEUTRAL.getDisplayName(),
                    Gender.FEMALE_ONLY.getDisplayName()
            );
        }
        return List.of(Gender.GENDER_NEUTRAL.getDisplayName());
    }

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

    private Integer parseRank(String rankStr) {
        if (rankStr == null || rankStr.isBlank()) {
            return null;
        }
        try {
            String cleaned = rankStr.strip();
            if (cleaned.endsWith(".0")) {
                cleaned = cleaned.substring(0, cleaned.length() - 2);
            }
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
