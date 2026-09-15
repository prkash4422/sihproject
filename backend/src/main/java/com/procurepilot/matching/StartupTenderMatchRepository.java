package com.procurepilot.matching;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface StartupTenderMatchRepository extends JpaRepository<StartupTenderMatch, Long> {
    
    @Query("SELECT m FROM StartupTenderMatch m WHERE m.startup.id = :startupId AND m.tender.id = :tenderId")
    Optional<StartupTenderMatch> findByStartupIdAndTenderId(@Param("startupId") Long startupId, @Param("tenderId") Long tenderId);

    @Query("SELECT m FROM StartupTenderMatch m WHERE m.startup.id = :startupId")
    List<StartupTenderMatch> findByStartupId(@Param("startupId") Long startupId);

    @Modifying
    @Query("DELETE FROM StartupTenderMatch m WHERE m.startup.id = :startupId AND m.tender.id = :tenderId")
    void deleteByStartupIdAndTenderId(@Param("startupId") Long startupId, @Param("tenderId") Long tenderId);
}
