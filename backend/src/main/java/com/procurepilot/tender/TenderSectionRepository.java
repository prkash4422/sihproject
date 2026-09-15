package com.procurepilot.tender;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TenderSectionRepository extends JpaRepository<TenderSection, Long> {
    List<TenderSection> findByTenderId(Long tenderId);
}
