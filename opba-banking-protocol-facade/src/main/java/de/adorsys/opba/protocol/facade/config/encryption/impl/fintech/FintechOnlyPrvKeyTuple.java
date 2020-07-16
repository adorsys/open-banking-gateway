package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import lombok.Data;

import javax.persistence.EntityManager;
import java.util.UUID;

@Data
public class FintechOnlyPrvKeyTuple {

    private final long fintechId;
    private final UUID keyId;

    public FintechOnlyPrvKeyTuple(String path) {
        String[] segments = path.split("/");
        this.fintechId = Long.parseLong(segments[0]);
        this.keyId = UUID.fromString(segments[1]);
    }

    public String toDatasafePathWithoutParent() {
        return this.fintechId + "/" + this.keyId;
    }

    public static FintechPrvKey buildFintechPrvKey(String path, EntityManager em) {
        FintechOnlyPrvKeyTuple tuple = new FintechOnlyPrvKeyTuple(path);
        return FintechPrvKey.builder()
                .id(tuple.getKeyId())
                .fintech(em.find(Fintech.class, tuple.getFintechId()))
                .build();
    }
}
