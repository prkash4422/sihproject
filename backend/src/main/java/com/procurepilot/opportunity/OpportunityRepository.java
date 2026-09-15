package com.procurepilot.opportunity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    List<Opportunity> findByStartupIdOrderByUpdatedAtDesc(Long startupId);
    Optional<Opportunity> findByStartupIdAndTenderId(Long startupId, Long tenderId);
}
