package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.DatasafeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasafeConfigRepository extends JpaRepository<DatasafeConfig, Long> {
}
