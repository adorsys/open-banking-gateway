package de.adorsys.opba.protocol.facade.config.encryption.impl;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import lombok.Data;

import javax.persistence.EntityManager;
import java.util.UUID;

/**
 * Key ID -> Psu + Aspsp relation tuple.
 */
@Data
public class PairIdPsuAspspTuple {

    private final UUID pairId;
    private final long psuId;
    private final long aspspId;

    @SuppressWarnings("checkstyle:MagicNumber") // Magic segment count
    public PairIdPsuAspspTuple(String path) {
        String[] segments = path.split("/");
        if (segments.length == 3) {
            this.psuId = Long.parseLong(segments[0]);
            this.pairId = UUID.fromString(segments[1]);
            this.aspspId = Long.parseLong(segments[2]);
            return;
        }

        this.pairId = null;
        this.psuId = Long.parseLong(segments[0]);
        this.aspspId = Long.parseLong(segments[1]);
    }

    public PairIdPsuAspspTuple(UUID pairId, AuthSession session) {
        this.pairId = pairId;
        this.psuId = session.getPsu().getId();
        this.aspspId = session.getAction().getBankProfile().getBank().getId();
    }

    public PairIdPsuAspspTuple(AuthSession session) {
        this.pairId = null;
        this.psuId = session.getPsu().getId();
        this.aspspId = session.getAction().getBankProfile().getBank().getId();
    }

    public String toDatasafePathWithoutPsuAndId() {
        if (null != pairId) {
            throw new IllegalArgumentException("Unexpected pair id");
        }
        return String.valueOf(this.aspspId);
    }

    /**
     * Computes current tuples' Datasafe storage path.
     * @return Datasafe path corresponding to current tuple
     */
    public String toDatasafePathWithoutPsu() {
        return pairId.toString() + "/" + this.aspspId;
    }

    /**
     * Creates PSU - ASPSP private key pair entity.
     * @param path Datasafe path
     * @param em Entity manager to persist to
     * @return KeyPair template
     */
    public static PsuAspspPrvKey buildPrvKey(String path, EntityManager em) {
        PairIdPsuAspspTuple tuple = new PairIdPsuAspspTuple(path);
        if (null == tuple.getPairId()) {
            throw new IllegalArgumentException("Pair id missing");
        }

        return PsuAspspPrvKey.builder()
                .id(tuple.getPairId())
                .psu(em.find(Psu.class, tuple.getPsuId()))
                .aspsp(em.find(Bank.class, tuple.getAspspId()))
                .build();
    }
}
