package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.WithBasicInfo;
import de.adorsys.opba.core.protocol.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Map;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_NOK_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;

@Service("xs2aAccountListConsentInitiate")
@RequiredArgsConstructor
public class Xs2aAccountListConsentInitiate extends ValidatedExecution<Xs2aContext> {

    private static final Headers.FromCtx HEADERS = Mappers.getMapper(Headers.FromCtx.class);
    private static final ConsentsMapper CONSENTS = Mappers.getMapper(ConsentsMapper.class);

    private final AccountInformationService ais;
    private final Xs2aValidator validator;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(context, HEADERS.map(context), CONSENTS.map(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<ConsentCreationResponse> consentInit = ais.createConsent(
                HEADERS.map(context).toHeaders(),
                CONSENTS.map(context)
        );

        context.setRedirectUriOk("http://localhost:8080/v1/consents/confirm/accounts/" + execution.getProcessInstanceId() + "/");
        context.setConsentId(consentInit.getBody().getConsentId());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
    }

    @Getter
    @Setter
    public static class Headers extends WithBasicInfo {

        @NotBlank
        private String psuIpAddress;

        @NotBlank
        private String redirectUriOk;

        @NotBlank
        private String redirectUriNok;

        public RequestHeaders toHeaders() {
            Map<String, String> headers = super.asMap();
            headers.put(PSU_IP_ADDRESS, psuIpAddress);
            headers.put(TPP_REDIRECT_URI, redirectUriOk);
            headers.put(TPP_NOK_REDIRECT_URI, redirectUriNok);
            return RequestHeaders.fromMap(headers);
        }

        @Mapper
        public interface FromCtx {
            Headers map(Xs2aContext ctx);
        }
    }

    @Data
    public static class Consent {
    }

    @Mapper
    public interface ConsentsMapper {

        @SuppressWarnings("checkstyle:MagicNumber") // Hardcoded as it is POC, these should be read from context
        default Consents map(Xs2aContext ctx) {
            Consents consents = new Consents();
            AccountAccess access = new AccountAccess();
            access.setAvailableAccounts(AccountAccess.AvailableAccountsEnum.ALLACCOUNTS);
            consents.setAccess(access);
            consents.setCombinedServiceIndicator(false);
            consents.setRecurringIndicator(true);
            consents.setFrequencyPerDay(10);
            consents.setValidUntil(LocalDate.of(2021, 10, 10));

            return consents;
        }
    }
}
