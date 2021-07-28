package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankActionRepository extends CrudRepository<BankAction, Long> {

    @Query("FROM BankAction b WHERE b.bankProfile.uuid = :uuid AND b.protocolAction = :action")
    Optional<BankAction> findByBankProfileUuidAndAction(
            @Param("uuid") UUID uuid,
            @Param("action") ProtocolAction action
    );

    List<BankAction> findByBankProfile(BankProfile profile);
}
