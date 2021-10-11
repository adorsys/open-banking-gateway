package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ais.UpdateExternalAisSession;
import de.adorsys.opba.protocol.api.dto.request.accounts.UpdateExternalAisSessionRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateExternalAisSessionBody;
import de.adorsys.opba.protocol.facade.services.FacadeOptionalService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.UPDATE_EXTERNAL_AIS_SESSION;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

@Service
public class UpdateExternalAisSessionService extends FacadeOptionalService<UpdateExternalAisSessionRequest, UpdateExternalAisSessionBody, UpdateExternalAisSession> {

    public UpdateExternalAisSessionService(
            Map<String, ? extends UpdateExternalAisSession> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate
    ) {
        super(UPDATE_EXTERNAL_AIS_SESSION, actionProviders, selector, provider, handler, txTemplate);
    }
}
