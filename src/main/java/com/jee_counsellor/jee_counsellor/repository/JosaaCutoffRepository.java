package com.jee_counsellor.jee_counsellor.repository;

import com.jee_counsellor.jee_counsellor.model.JosaaCutoff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JosaaCutoffRepository extends JpaRepository<JosaaCutoff, Long> {

    /**
     * Fetches all applicable cutoff rows for the given criteria without pagination.
     */
    @Query(value = """
            SELECT * FROM josaa_cutoffs_2026 c
            WHERE c."Year" = :year
              AND c."Round" = :round
              AND c."Seat Type" = :seatType
              AND c."Gender" IN (:genders)
              AND c."Closing Rank" IS NOT NULL
              AND c."Closing Rank" != ''
              AND c."Closing Rank" NOT LIKE '%P'
              AND (
                c."Quota" = 'AI'
                OR (c."Quota" = 'HS' AND c.state = :homeState)
                OR (c."Quota" = 'OS' AND c.state != :homeState)
                OR (c."Quota" = 'GO' AND :homeState = 'Goa')
                OR (c."Quota" = 'JK' AND :homeState = 'Jammu and Kashmir')
                OR (c."Quota" = 'LA' AND :homeState = 'Ladakh')
              )
            ORDER BY CAST(REGEXP_REPLACE(c."Closing Rank", '\\.0$', '') AS BIGINT) ASC NULLS LAST
            """,
            nativeQuery = true)
    List<JosaaCutoff> findAllApplicableCutoffs(
            @Param("year") Long year,
            @Param("round") Long round,
            @Param("seatType") String seatType,
            @Param("genders") List<String> genders,
            @Param("homeState") String homeState
    );

    /**
     * Fetches applicable cutoff rows filtered at the database level with pagination.
     */
    @Query(value = """
            SELECT * FROM josaa_cutoffs_2026 c
            WHERE c."Year" = :year
              AND c."Round" = :round
              AND c."Seat Type" = :seatType
              AND c."Gender" IN (:genders)
              AND c."Closing Rank" IS NOT NULL
              AND c."Closing Rank" != ''
              AND c."Closing Rank" NOT LIKE '%P'
              AND (
                c."Quota" = 'AI'
                OR (c."Quota" = 'HS' AND c.state = :homeState)
                OR (c."Quota" = 'OS' AND c.state != :homeState)
                OR (c."Quota" = 'GO' AND :homeState = 'Goa')
                OR (c."Quota" = 'JK' AND :homeState = 'Jammu and Kashmir')
                OR (c."Quota" = 'LA' AND :homeState = 'Ladakh')
              )
            ORDER BY CAST(REGEXP_REPLACE(c."Closing Rank", '\\.0$', '') AS BIGINT) ASC NULLS LAST
            """,
            countQuery = """
            SELECT COUNT(*) FROM josaa_cutoffs_2026 c
            WHERE c."Year" = :year
              AND c."Round" = :round
              AND c."Seat Type" = :seatType
              AND c."Gender" IN (:genders)
              AND c."Closing Rank" IS NOT NULL
              AND c."Closing Rank" != ''
              AND c."Closing Rank" NOT LIKE '%P'
              AND (
                c."Quota" = 'AI'
                OR (c."Quota" = 'HS' AND c.state = :homeState)
                OR (c."Quota" = 'OS' AND c.state != :homeState)
                OR (c."Quota" = 'GO' AND :homeState = 'Goa')
                OR (c."Quota" = 'JK' AND :homeState = 'Jammu and Kashmir')
                OR (c."Quota" = 'LA' AND :homeState = 'Ladakh')
              )
            """,
            nativeQuery = true)
    Page<JosaaCutoff> findApplicableCutoffs(
            @Param("year") Long year,
            @Param("round") Long round,
            @Param("seatType") String seatType,
            @Param("genders") List<String> genders,
            @Param("homeState") String homeState,
            Pageable pageable
    );
}
