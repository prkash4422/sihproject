package com.procurepilot.startup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StartupCertificationRepository extends JpaRepository<StartupCertification, Long> {
    List<StartupCertification> findByStartupId(Long startupId);
    void deleteByStartupId(Long startupId);
}
