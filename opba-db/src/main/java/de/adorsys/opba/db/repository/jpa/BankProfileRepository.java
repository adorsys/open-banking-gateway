package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankProfileRepository extends JpaRepository<BankProfile, Long> {

    Optional<BankProfile> findByBankUuid(String bankId);
}
