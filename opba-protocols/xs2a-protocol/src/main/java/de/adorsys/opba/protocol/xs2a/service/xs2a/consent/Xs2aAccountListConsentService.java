package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aApiVersionSupport;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.ConsentsResponse201;
import org.flowable.engine.delegate.DelegateExecution;


public interface Xs2aAccountListConsentService extends Xs2aApiVersionSupport {

    void doValidate(DelegateExecution execution, Xs2aAisContext context);

    Response<ConsentsResponse201> doExecution(DelegateExecution execution, Xs2aAisContext context);

}
