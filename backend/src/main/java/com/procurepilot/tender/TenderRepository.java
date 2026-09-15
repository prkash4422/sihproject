package com.procurepilot.tender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TenderRepository extends JpaRepository<Tender, Long> {
    Optional<Tender> findByTenderRefNo(String tenderRefNo);

    @Query("SELECT t FROM Tender t WHERE " +
           "(:query IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.tenderRefNo) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR LOWER(t.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
           "(:department IS NULL OR LOWER(t.department) LIKE LOWER(CONCAT('%', :department, '%'))) " +
           "ORDER BY t.closingDate ASC")
    List<Tender> searchTenders(@Param("query") String query,
                               @Param("category") String category,
                               @Param("department") String department);
}
