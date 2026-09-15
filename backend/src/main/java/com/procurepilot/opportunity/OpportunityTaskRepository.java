package com.procurepilot.opportunity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OpportunityTaskRepository extends JpaRepository<OpportunityTask, Long> {
    List<OpportunityTask> findByOpportunityId(Long opportunityId);
}
