package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.AccountEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
    Iterable<AccountEntity> findByUserEntityAndBankId(UserEntity userEntity, String bankId);
    Optional<AccountEntity> findByUserEntityAndBankIdAndIban(UserEntity userEntity, String bankId, String iban);
}
