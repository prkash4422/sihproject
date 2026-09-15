package com.procurepilot.ai;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QaMessageRepository extends JpaRepository<QaMessage, Long> {
    List<QaMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
