package de.adorsys.opba.tppbanking.impl.repository.jpa;

import de.adorsys.opba.tppbanking.impl.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankProfileRepository extends JpaRepository<BankProfile, Long> {

    Optional<BankProfile> findByBankUuid(String bankId);
}
