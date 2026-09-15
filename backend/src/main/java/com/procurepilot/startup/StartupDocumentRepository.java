package com.procurepilot.startup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StartupDocumentRepository extends JpaRepository<StartupDocument, Long> {
    List<StartupDocument> findByStartupId(Long startupId);
}
