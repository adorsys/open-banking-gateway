package de.adorsys.opba.protocol.facade.config.encryption.impl;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import lombok.Data;

import javax.persistence.EntityManager;
import java.util.UUID;

@Data
public class PairIdPsuAspspTuple {

    private final UUID pairId;
    private final long psuId;
    private final long aspspId;

    public PairIdPsuAspspTuple(String path) {
        String[] segments = path.split("/");
        this.psuId = Long.parseLong(segments[0]);
        this.pairId = UUID.fromString(segments[1]);
        this.aspspId = Long.parseLong(segments[2]);
    }

    public PairIdPsuAspspTuple(UUID pairId, AuthSession session) {
        this.pairId = pairId;
        this.psuId = session.getPsu().getId();
        this.aspspId = session.getProtocol().getBankProfile().getBank().getId();
    }

    /**
     * Key ID is
     * @return
     */
    public String toDatasafePathWithoutPsuAndId() {
        return String.valueOf(this.aspspId);
    }

    public static PsuAspspPrvKey buildPrvKey(String path, EntityManager em) {
        PairIdPsuAspspTuple tuple = new PairIdPsuAspspTuple(path);
        return PsuAspspPrvKey.builder()
                .psu(em.find(Psu.class, tuple.getPsuId()))
                .aspsp(em.find(Bank.class, tuple.getAspspId()))
                .build();
    }
}