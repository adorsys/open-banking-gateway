package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankProtocolRepository extends CrudRepository<BankAction, Long> {

    @Query("FROM BankAction b WHERE b.bankProfile.bank.uuid = :uuid AND b.protocolAction = :action")
    Optional<BankAction> findByBankProfileUuidAndAction(
            @Param("uuid") String uuid,
            @Param("action") ProtocolAction action
    );
}
