package com.procurepilot.ai;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QaSessionRepository extends JpaRepository<QaSession, Long> {
    Optional<QaSession> findByTenderIdAndStartupId(Long tenderId, Long startupId);
}
