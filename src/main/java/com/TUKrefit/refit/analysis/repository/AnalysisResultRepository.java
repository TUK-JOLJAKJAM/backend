package com.TUKrefit.refit.analysis.repository;

import com.TUKrefit.refit.analysis.entity.AnalysisResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, String> {
    Optional<AnalysisResult> findFirstByHistoryIdAndUserIdOrderByAnalyzedAtMsDesc(
            String historyId,
            String userId
    );

    Page<AnalysisResult> findByHistoryIdAndUserIdOrderByAnalyzedAtMsDesc(
            String historyId,
            String userId,
            Pageable pageable
    );

    Page<AnalysisResult> findByUserIdOrderByAnalyzedAtMsDesc(String userId, Pageable pageable);
}
