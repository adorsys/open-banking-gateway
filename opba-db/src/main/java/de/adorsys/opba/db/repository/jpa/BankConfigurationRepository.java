package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.BankConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankConfigurationRepository extends JpaRepository<BankConfiguration, Long> {
}
