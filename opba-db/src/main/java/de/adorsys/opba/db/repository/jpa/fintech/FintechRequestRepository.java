package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.FintechRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FintechRequestRepository extends CrudRepository<FintechRequest, UUID> {
    Optional<FintechRequest> findByXRequestId(String xRequestId);
}
