package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankProfileJpaRepository extends JpaRepository<BankProfile, Long> {

    Optional<BankProfile> findByUuid(UUID uuid);

    void deleteByBank(Bank bank);

    List<BankProfile> findByBankUuid(UUID bankUuid);

    List<BankProfile> findByBankIdIn(Collection<Long> bankIds);

    List<BankProfile> findByBankId(Long bankId);

    List<BankProfile> findByBankBic(String bankBic);

    List<BankProfile> findByBankBankCode(String bankCode);

    @SuppressWarnings("checkstyle:MethodName") // ambiguity due to getBankName()
    List<BankProfile> findByBank_Name(String bankName);
}
