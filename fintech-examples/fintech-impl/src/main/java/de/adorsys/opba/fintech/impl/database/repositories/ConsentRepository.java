package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository extends CrudRepository<ConsentEntity, Long> {
    Optional<ConsentEntity> findByTppAuthId(String authId);

    Optional<ConsentEntity> findFirstByUserEntityAndBankIdAndConsentTypeAndConsentConfirmedOrderByCreationTimeDesc(
        UserEntity userEntity, String bankId, ConsentType consentType, Boolean consentConfirmed);

    List<ConsentEntity> findListByUserEntityAndBankIdAndConsentTypeAndConsentConfirmed(UserEntity userEntity, String
        bankId, ConsentType consentType, Boolean consentConfirmed);

    List<ConsentEntity> findByUserEntityAndConsentTypeAndConsentConfirmedOrderByCreationTimeDesc(UserEntity userEntity, ConsentType consentType, Boolean consentConfirmed);

    @Modifying
    long deleteByUserEntityAndBankId(UserEntity entity, String bankId);
}
