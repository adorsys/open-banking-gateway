package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankProfileJpaRepository extends JpaRepository<BankProfile, Long> {

    Optional<BankProfile> findByUuid(UUID uuid);

    List<BankProfile> findByBankUuid(UUID bankUuid);

    List<BankProfile> findByBankBic(String bankBic);

    List<BankProfile> findByBankBankCode(String bankId);

    List<BankProfile> findByBankName(String bankId);

}
