package de.adorsys.opba.db.domain.generators;

import de.adorsys.opba.db.domain.entity.IdAssignable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

public class AssignedUuidGenerator extends UUIDGenerator {

    public static final String ASSIGNED_ID_GENERATOR = "assigned-identity";
    public static final String ASSIGNED_ID_STRATEGY = "de.adorsys.opba.db.domain.generators.AssignedUuidGenerator";

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        if (obj instanceof IdAssignable) {
            Serializable id = ((IdAssignable<? extends Serializable>) obj).getId();
            if (id != null) {
                return id;
            }
        }

        return (Serializable) super.generate(session, obj);
    }
}
