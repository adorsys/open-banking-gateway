package de.adorsys.opba.fireflyexporter.repository;

import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankConsentRepository extends CrudRepository<BankConsent, Long> {

    Optional<BankConsent> findFirstByBankIdOrderByModifiedAt(String bankId);
}
