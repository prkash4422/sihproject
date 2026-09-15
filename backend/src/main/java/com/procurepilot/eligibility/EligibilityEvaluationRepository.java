package com.procurepilot.eligibility;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EligibilityEvaluationRepository extends JpaRepository<EligibilityEvaluation, Long> {
    List<EligibilityEvaluation> findByMatchId(Long matchId);
}
