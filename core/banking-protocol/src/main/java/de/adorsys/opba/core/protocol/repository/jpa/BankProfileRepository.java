package de.adorsys.opba.core.protocol.repository.jpa;

import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankProfileRepository extends JpaRepository<BankProfile, Long> {

    @Query("FROM BankProfile bp WHERE bp.bank.id = :bankId")
    Optional<BankProfile> getProfileByBankId(@Param("bankId") Long bankId);
}
