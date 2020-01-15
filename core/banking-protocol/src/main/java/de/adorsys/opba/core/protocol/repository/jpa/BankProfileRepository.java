package de.adorsys.opba.core.protocol.repository.jpa;

import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankProfileRepository extends JpaRepository<BankProfile, Long> {

    Optional<BankProfile> findByBankUuid(String bankId);
}
