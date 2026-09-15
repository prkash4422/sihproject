package com.procurepilot.startup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StartupCapabilityRepository extends JpaRepository<StartupCapability, Long> {
    List<StartupCapability> findByStartupId(Long startupId);
    void deleteByStartupId(Long startupId);
}
