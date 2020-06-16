package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.AccountEntity;
import de.adorsys.opba.fintech.impl.database.entities.SinglePaymentEntity;
import org.springframework.data.repository.CrudRepository;

public interface SinglePaymentRepository extends CrudRepository<SinglePaymentEntity, Long> {
    Iterable<SinglePaymentEntity> findByAccountEntity(AccountEntity accountEntity);
}
