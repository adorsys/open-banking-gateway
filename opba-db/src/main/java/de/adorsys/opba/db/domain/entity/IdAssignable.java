package de.adorsys.opba.db.domain.entity;

import java.io.Serializable;

public interface IdAssignable<I extends Serializable> {

    I getId();

    void setId(I id);
}
