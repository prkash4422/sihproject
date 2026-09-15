package com.procurepilot.requirement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TenderRequirementRepository extends JpaRepository<TenderRequirement, Long> {
    List<TenderRequirement> findByTenderId(Long tenderId);
    List<TenderRequirement> findByTenderIdAndReviewStatus(Long tenderId, String reviewStatus);
    List<TenderRequirement> findByReviewStatus(String reviewStatus);
}
