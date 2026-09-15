package com.procurepilot.compliance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplianceEvidenceRepository extends JpaRepository<ComplianceEvidence, Long> {
    List<ComplianceEvidence> findByComplianceItemId(Long complianceItemId);
}
