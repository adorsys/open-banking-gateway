package de.adorsys.opba.protocol.facade.config.encryption.impl;

import de.adorsys.opba.db.domain.entity.fintech.FintechConsentSpec;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import lombok.Data;

import javax.persistence.EntityManager;
import java.util.UUID;

/**
 * Fintech to AuthSession relation tuple.
 */
@Data
public class FintechUserAuthSessionTuple {

    private final long fintechUserId;
    private final UUID authSessionId;

    public FintechUserAuthSessionTuple(String id) {
        String[] segments = id.split("/");
        this.fintechUserId = Long.parseLong(segments[0]);
        this.authSessionId = UUID.fromString(segments[1]);
    }

    public FintechUserAuthSessionTuple(AuthSession session) {
        this.fintechUserId = session.getFintechUser().getId();
        this.authSessionId = session.getId();
    }

    /**
     * Computes current tuples' Datasafe storage path.
     * @return Datasafe path corresponding to current tuple
     */
    public String toDatasafePathWithoutParent() {
        return this.authSessionId.toString();
    }

    /**
     * Creates FinTech template requirements to the consent if any.
     * @param path Datasafe path
     * @param em Entity manager to persist to
     * @return FinTech consent specification
     */
    public static FintechConsentSpec buildFintechConsentSpec(String path, EntityManager em) {
        FintechUserAuthSessionTuple tuple = new FintechUserAuthSessionTuple(path);
        return FintechConsentSpec.builder()
                .id(tuple.getAuthSessionId())
                .user(em.find(FintechUser.class, tuple.getFintechUserId()))
                .build();
    }
}
