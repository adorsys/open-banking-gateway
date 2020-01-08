package de.adorsys.opba.core.protocol.repository.jpa;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankProfileRepository extends JpaRepository<BankProfile, Long> {
}
