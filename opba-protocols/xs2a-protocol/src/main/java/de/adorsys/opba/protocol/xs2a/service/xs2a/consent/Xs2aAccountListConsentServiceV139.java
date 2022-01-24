package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.vdurmont.semver4j.Semver;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateV139Headers;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.Consents;
import de.adorsys.xs2a.adapter.api.model.ConsentsResponse201;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class Xs2aAccountListConsentServiceV139 implements Xs2aAccountListConsentService {

    private static final Semver XS2A_API_VERSION = new Semver("1.3.9");

    private final Xs2aValidator validator;
    private final AccountInformationService ais;
    private final CreateConsentOrPaymentPossibleErrorHandler handler;
    private final AisConsentInitiateV139Extractor extractor;
    private final CreateAisConsentService createAisConsentService;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    public void doValidate(DelegateExecution execution, Xs2aAisContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    public Response<ConsentsResponse201> doExecution(DelegateExecution execution, Xs2aAisContext context) {

        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ValidatedPathHeadersBody<ConsentInitiateParameters, ConsentInitiateV139Headers, Consents> params = extractor.forExecution(context);
        return handler.tryCreateAndHandleErrors(execution, () ->
                createAisConsentService.createConsent(ais, context, params.getPath(), params.getHeaders(), params.getBody()));

    }

    @Override
    public boolean isXs2aApiVersionSupported(String apiVersion) {
        return Strings.isNotBlank(apiVersion) && !VERSION_DIFFS.contains(XS2A_API_VERSION.diff(apiVersion));
    }
}
