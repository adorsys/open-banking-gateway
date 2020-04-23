package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FintechUserRepository extends CrudRepository<FintechUser, Long> {
}
