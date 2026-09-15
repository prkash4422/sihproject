package com.procurepilot.startup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StartupRepository extends JpaRepository<Startup, Long> {
    Optional<Startup> findByUserId(Long userId);
    boolean existsByDpiitNumber(String dpiitNumber);
}
