package de.adorsys.opba.protocol.facade.config.encryption.impl;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKeyInbox;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import lombok.Data;

import javax.persistence.EntityManager;

@Data
public class FintechPsuAspspTuple {

    private final long fintechId;
    private final long psuId;
    private final long aspspId;

    public FintechPsuAspspTuple(String path) {
        String[] segments = path.split("/");
        this.fintechId = Long.parseLong(segments[0]);
        this.psuId = Long.parseLong(segments[1]);
        this.aspspId = Long.parseLong(segments[2]);
    }

    public FintechPsuAspspTuple(AuthSession session) {
        this.fintechId = session.getFintechUser().getFintech().getId();
        this.psuId = session.getPsu().getId();
        this.aspspId = session.getProtocol().getBankProfile().getBank().getId();
    }

    public FintechPsuAspspTuple(ServiceSession session) {
        this.fintechId = session.getAuthSession().getFintechUser().getFintech().getId();
        this.psuId = session.getAuthSession().getPsu().getId();
        this.aspspId = session.getProtocol().getBankProfile().getBank().getId();
    }

    public String toDatasafePathWithoutParent() {
        return this.psuId + "/" + this.aspspId;
    }

    public static FintechPsuAspspPrvKey buildFintechPrvKey(String path, EntityManager em) {
        FintechPsuAspspTuple tuple = new FintechPsuAspspTuple(path);
        return FintechPsuAspspPrvKey.builder()
                .fintech(em.find(Fintech.class, tuple.getFintechId()))
                .psu(em.find(Psu.class, tuple.getPsuId()))
                .aspsp(em.find(Bank.class, tuple.getAspspId()))
                .build();
    }

    public static FintechPsuAspspPrvKeyInbox buildFintechInboxPrvKey(String path, EntityManager em) {
        FintechPsuAspspTuple tuple = new FintechPsuAspspTuple(path);
        return FintechPsuAspspPrvKeyInbox.builder()
                .fintech(em.find(Fintech.class, tuple.getFintechId()))
                .psu(em.find(Psu.class, tuple.getPsuId()))
                .aspsp(em.find(Bank.class, tuple.getAspspId()))
                .build();
    }
}
