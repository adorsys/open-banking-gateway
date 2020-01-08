package de.adorsys.opba.core.protocol.repository.jpa;

import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BankProfileRepository extends JpaRepository<BankProfile, Long> {

    @Query("FROM BankProfile bp WHERE bp.bank.id = ?1")
    BankProfile getProfileByBankId(Long bankId);
}
