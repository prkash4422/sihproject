package com.procurepilot.eligibility;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EligibilityRuleRepository extends JpaRepository<EligibilityRule, Long> {
    Optional<EligibilityRule> findByRuleCode(String ruleCode);
}
