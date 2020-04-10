package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FintechUserRepository extends CrudRepository<FintechUser, Long> {

    Optional<FintechUser> findByPsuFintechIdAndFintech(String psuFintechId, Fintech fintech);
}
