package com.procurepilot.compliance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplianceItemRepository extends JpaRepository<ComplianceItem, Long> {
    List<ComplianceItem> findByTenderIdAndStartupId(Long tenderId, Long startupId);
    List<ComplianceItem> findByStartupId(Long startupId);
}
