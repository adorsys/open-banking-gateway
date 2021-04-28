package de.adorsys.opba.protocol.xs2a.util.logresolver.mapper;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aResourceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStartPaymentAuthorizationParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aTransactionParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithBalanceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.AccountAccessLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.BaseContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.PathHeadersBodyParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.PathQueryHeadersParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.ConsentPathHeadersBodyParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.ConsentPathHeadersParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.AccountReferenceLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.AddressLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.AmountLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.PisPathHeadersBodyParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.QueryHeadersParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.StartScaprocessResponseLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.TransactionListXs2aContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.Xs2aContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.Xs2aExecutionLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.PisPathHeadersParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.Xs2aPisContextLog;
import de.adorsys.xs2a.adapter.api.model.AccountAccess;
import de.adorsys.xs2a.adapter.api.model.AccountReference;
import de.adorsys.xs2a.adapter.api.model.Address;
import de.adorsys.xs2a.adapter.api.model.Amount;
import de.adorsys.xs2a.adapter.api.model.Consents;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.SelectPsuAuthenticationMethod;
import de.adorsys.xs2a.adapter.api.model.StartScaprocessResponse;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface Xs2aDtoToLogObjectsMapper {

    Xs2aExecutionLog mapFromExecutionToXs2aExecutionLog(DelegateExecution execution);

    BaseContextLog mapBaseContextDtoToBaseContextLog(BaseContext context);

    @Mapping(target = "lastRedirectTo", source = "context.lastRedirection.redirectTo")
    @Mapping(target = "lastRedirectToUiScreen", source = "context.lastRedirection.redirectToUiScreen")
    Xs2aContextLog mapFromXs2aContextDtoToXs2aContextLog(Xs2aContext context);

    StartScaprocessResponseLog mapStartScaprocessResponseDtoToStartScaprocessResponseLog(StartScaprocessResponse value);

    @Mapping(target = "lastRedirectTo", source = "context.lastRedirection.redirectTo")
    @Mapping(target = "lastRedirectToUiScreen", source = "context.lastRedirection.redirectToUiScreen")
    TransactionListXs2aContextLog mapFromTransactionListXs2aContextDtoToXs2aContextLog(TransactionListXs2aContext context);

    @Mapping(target = "lastRedirectTo", source = "context.lastRedirection.redirectTo")
    @Mapping(target = "lastRedirectToUiScreen", source = "context.lastRedirection.redirectToUiScreen")
    Xs2aPisContextLog mapFromXs2aPisContextDtoToXs2aPisContextLog(Xs2aPisContext context);

    @Mapping(target = "consentId", source = "validatedQueryHeaders.headers.consentId")
    @Mapping(target = "psuId", source = "validatedQueryHeaders.headers.psuId")
    @Mapping(target = "aspspId", source = "validatedQueryHeaders.headers.aspspId")
    @Mapping(target = "requestId", source = "validatedQueryHeaders.headers.requestId")
    QueryHeadersParametersLog mapFromQueryHeadersDtoToQueryHeadersLog(
            ValidatedQueryHeaders<Xs2aWithBalanceParameters, Xs2aWithConsentIdHeaders> validatedQueryHeaders);

    @Mapping(target = "resourceId", source = "validatedPathQueryHeaders.path.resourceId")
    @Mapping(target = "consentId", source = "validatedPathQueryHeaders.headers.consentId")
    @Mapping(target = "psuId", source = "validatedPathQueryHeaders.headers.psuId")
    @Mapping(target = "aspspId", source = "validatedPathQueryHeaders.headers.aspspId")
    @Mapping(target = "requestId", source = "validatedPathQueryHeaders.headers.requestId")
    PathQueryHeadersParametersLog mapFromPathQueryHeadersDtoToPathQueryHeadersLog(
            ValidatedPathQueryHeaders<Xs2aResourceParameters, Xs2aTransactionParameters, Xs2aWithConsentIdHeaders> validatedPathQueryHeaders);

    @Mapping(target = "psuId", source = "validatedPathHeadersBody.headers.psuId")
    @Mapping(target = "aspspId", source = "validatedPathHeadersBody.headers.aspspId")
    @Mapping(target = "requestId", source = "validatedPathHeadersBody.headers.requestId")
    @Mapping(target = "oauth2Token", source = "validatedPathHeadersBody.headers.oauth2Token")
    @Mapping(target = "psuIpAddress", source = "validatedPathHeadersBody.headers.psuIpAddress")
    @Mapping(target = "redirectUriOk", source = "validatedPathHeadersBody.headers.redirectUriOk")
    @Mapping(target = "redirectUriNok", source = "validatedPathHeadersBody.headers.redirectUriNok")
    @Mapping(target = "psuIpPort", source = "validatedPathHeadersBody.headers.psuIpPort")
    @Mapping(target = "access", source = "validatedPathHeadersBody.body.access")
    @Mapping(target = "recurringIndicator", source = "validatedPathHeadersBody.body.recurringIndicator")
    @Mapping(target = "validUntil", source = "validatedPathHeadersBody.body.validUntil")
    @Mapping(target = "frequencyPerDay", source = "validatedPathHeadersBody.body.frequencyPerDay")
    @Mapping(target = "combinedServiceIndicator", source = "validatedPathHeadersBody.body.combinedServiceIndicator")
    PathHeadersBodyParametersLog mapFromPathHeadersBodyDtoToPathHeadersBodyLog(
            ValidatedPathHeadersBody<ConsentInitiateParameters, ConsentInitiateHeaders, Consents> validatedPathHeadersBody);

    @Mapping(target = "paymentType", source = "path.paymentType")
    @Mapping(target = "paymentProduct", source = "path.paymentProduct")
    @Mapping(target = "psuId", source = "headers.psuId")
    @Mapping(target = "aspspId", source = "headers.aspspId")
    @Mapping(target = "requestId", source = "headers.requestId")
    @Mapping(target = "oauth2Token", source = "headers.oauth2Token")
    @Mapping(target = "psuIpAddress", source = "headers.psuIpAddress")
    @Mapping(target = "redirectUriOk", source = "headers.redirectUriOk")
    @Mapping(target = "redirectUriNok", source = "headers.redirectUriNok")
    @Mapping(target = "psuIpPort", source = "headers.psuIpPort")
    @Mapping(target = "endToEndIdentification", source = "body.endToEndIdentification")
    @Mapping(target = "debtorAccount", source = "body.debtorAccount")
    @Mapping(target = "instructedAmount", source = "body.instructedAmount")
    @Mapping(target = "creditorAccount", source = "body.creditorAccount")
    @Mapping(target = "creditorAgent", source = "body.creditorAgent")
    @Mapping(target = "creditorAgentName", source = "body.creditorAgentName")
    @Mapping(target = "creditorName", source = "body.creditorName")
    @Mapping(target = "creditorAddress", source = "body.creditorAddress")
    @Mapping(target = "remittanceInformationUnstructured", source = "body.remittanceInformationUnstructured")
    PisPathHeadersBodyParametersLog mapFromPathHeadersBodyDtoToPisPathHeadersBodyLog(
            Xs2aInitialPaymentParameters path, PaymentInitiateHeaders headers, PaymentInitiationJson body);

    @Mapping(target = "consentId", source = "path.consentId")
    @Mapping(target = "psuId", source = "headers.psuId")
    @Mapping(target = "aspspId", source = "headers.aspspId")
    @Mapping(target = "requestId", source = "headers.requestId")
    @Mapping(target = "oauth2Token", source = "headers.oauth2Token")
    @Mapping(target = "tppRedirectPreferred", source = "headers.tppRedirectPreferred")
    ConsentPathHeadersParametersLog mapFromPathHeadersDtoToConsentPathHeadersLog(
            Xs2aInitialConsentParameters path, Xs2aStandardHeaders headers);

    @Mapping(target = "paymentId", source = "path.paymentId")
    @Mapping(target = "paymentType", source = "path.paymentType")
    @Mapping(target = "paymentProduct", source = "path.paymentProduct")
    @Mapping(target = "psuId", source = "headers.psuId")
    @Mapping(target = "aspspId", source = "headers.aspspId")
    @Mapping(target = "requestId", source = "headers.requestId")
    @Mapping(target = "oauth2Token", source = "headers.oauth2Token")
    @Mapping(target = "tppRedirectPreferred", source = "headers.tppRedirectPreferred")
    PisPathHeadersParametersLog mapFromPathHeadersDtoToPisPathHeadersLog(
            Xs2aStartPaymentAuthorizationParameters path, Xs2aStandardHeaders headers);

    @Mapping(target = "consentId", source = "path.consentId")
    @Mapping(target = "authorizationId", source = "path.authorizationId")
    @Mapping(target = "psuId", source = "headers.psuId")
    @Mapping(target = "aspspId", source = "headers.aspspId")
    @Mapping(target = "requestId", source = "headers.requestId")
    @Mapping(target = "oauth2Token", source = "headers.oauth2Token")
    @Mapping(target = "tppRedirectPreferred", source = "headers.tppRedirectPreferred")
    @Mapping(target = "authenticationMethodId", source = "body.authenticationMethodId")
    ConsentPathHeadersBodyParametersLog mapFromPathHeadersBodyDtoToConsentPathHeadersBodyLog(
            Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, SelectPsuAuthenticationMethod body);

    AmountLog mapAmountToAmountLog(Amount amount);

    AddressLog mapAddressToAddressLog(Address address);

    AccountReferenceLog mapAccountReferenceToAccountReferenceLog(AccountReference accountReference);

    AccountAccessLog mapAccountAccessToAccountAccessLog(AccountAccess access);

}
