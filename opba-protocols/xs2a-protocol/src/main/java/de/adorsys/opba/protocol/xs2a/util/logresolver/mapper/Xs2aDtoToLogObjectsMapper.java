package de.adorsys.opba.protocol.xs2a.util.logresolver.mapper;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedPaymentParameters;
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
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Headers;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Parameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2WithCodeParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.ValidatedPathHeadersBodyLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.ValidatedPathHeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.ValidatedPathQueryHeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.ValidatedQueryHeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.Xs2aExecutionLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.PsuDataLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.SelectPsuAuthenticationMethodLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.StartScaprocessResponseLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.TransactionAuthorisationLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.UpdatePsuAuthenticationLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aOauth2HeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aOauth2ParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aOauth2WithCodeParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aResourceParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aStandardHeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aTransactionParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aWithBalanceParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common.Xs2aWithConsentIdHeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.AccountAccessLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.AccountReferenceLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.ConsentInitiateHeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.ConsentInitiateParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.ConsentsLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.Xs2aAuthorizedConsentParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent.Xs2aInitialConsentParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context.AuthenticationObjectLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context.BaseContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context.ChallengeDataLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context.ServiceContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context.TransactionListXs2aContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context.Xs2aContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context.Xs2aPisContextLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.AddressLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.AmountLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.PaymentInitiateHeadersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.PaymentInitiationJsonLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.Xs2aAuthorizedPaymentParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.Xs2aInitialPaymentParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.payment.Xs2aStartPaymentAuthorizationParametersLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.response.TokenResponseLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.response.URILog;
import de.adorsys.xs2a.adapter.api.model.AccountAccess;
import de.adorsys.xs2a.adapter.api.model.AccountReference;
import de.adorsys.xs2a.adapter.api.model.Address;
import de.adorsys.xs2a.adapter.api.model.Amount;
import de.adorsys.xs2a.adapter.api.model.AuthenticationObject;
import de.adorsys.xs2a.adapter.api.model.ChallengeData;
import de.adorsys.xs2a.adapter.api.model.Consents;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.PsuData;
import de.adorsys.xs2a.adapter.api.model.SelectPsuAuthenticationMethod;
import de.adorsys.xs2a.adapter.api.model.StartScaprocessResponse;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import de.adorsys.xs2a.adapter.api.model.TransactionAuthorisation;
import de.adorsys.xs2a.adapter.api.model.UpdatePsuAuthentication;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;

import java.net.URI;


@Mapper
public interface Xs2aDtoToLogObjectsMapper {

    //execution mapper

    Xs2aExecutionLog mapFromExecutionToXs2aExecutionLog(DelegateExecution execution);

    //context mappers

    BaseContextLog mapBaseContextDtoToBaseContextLog(BaseContext context);

    ServiceContextLog mapServiceContextDtoToServiceContextLog(ServiceContext context);

    Xs2aContextLog mapFromXs2aContextDtoToXs2aContextLog(Xs2aContext context);

    TransactionListXs2aContextLog mapFromTransactionListXs2aContextDtoToXs2aContextLog(TransactionListXs2aContext context);

    Xs2aPisContextLog mapFromXs2aPisContextDtoToXs2aPisContextLog(Xs2aPisContext context);

    StartScaprocessResponseLog mapStartScaprocessResponseDtoToStartScaprocessResponseLog(StartScaprocessResponse value);

    AuthenticationObjectLog mapAuthenticationObjectDtoToAuthenticationObjectLog(AuthenticationObject value);

    ChallengeDataLog mapChallengeDataLogDtoToChallengeDataLog(ChallengeData value);

    //parameters mappers

    ValidatedQueryHeadersLog<Xs2aWithBalanceParametersLog, Xs2aWithConsentIdHeadersLog> mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(
            Xs2aWithBalanceParameters query, Xs2aWithConsentIdHeaders headers);

    Xs2aWithBalanceParametersLog mapFromXs2aWithBalanceParametersToXs2aWithBalanceParametersLog(Xs2aWithBalanceParameters query);

    Xs2aWithConsentIdHeadersLog mapFromXs2aWithConsentIdHeadersToXs2aWithConsentIdHeadersLog(ConsentInitiateHeaders headers);


    ValidatedQueryHeadersLog<Xs2aOauth2WithCodeParametersLog, Xs2aOauth2HeadersLog> mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(
            Xs2aOauth2WithCodeParameters query, Xs2aOauth2Headers headers);

    Xs2aOauth2WithCodeParametersLog mapFromXs2aOauth2WithCodeParametersToXs2aOauth2WithCodeParametersLog(Xs2aOauth2WithCodeParameters query);

    Xs2aOauth2HeadersLog mapFromXs2aOauth2HeadersToXs2aOauth2HeadersLog(Xs2aOauth2Headers headers);


    ValidatedQueryHeadersLog<Xs2aOauth2ParametersLog, Xs2aOauth2HeadersLog> mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(
            Xs2aOauth2Parameters query, Xs2aOauth2Headers headers);

    Xs2aOauth2ParametersLog mapFromXs2aOauth2WithCodeParametersToXs2aOauth2WithCodeParametersLog(Xs2aOauth2Parameters query);


    ValidatedPathQueryHeadersLog<Xs2aResourceParametersLog, Xs2aTransactionParametersLog, Xs2aWithConsentIdHeadersLog> mapFromPathQueryHeadersToXs2aValidatedPathQueryHeadersLog(
            Xs2aResourceParameters path, Xs2aTransactionParameters query, Xs2aWithConsentIdHeaders headers);

    Xs2aResourceParametersLog mapFromXs2aResourceParametersToXs2aResourceParametersLog(Xs2aResourceParameters path);

    Xs2aTransactionParametersLog mapFromXs2aTransactionParametersToXs2aTransactionParametersLog(Xs2aTransactionParameters query);


    ValidatedPathHeadersBodyLog<ConsentInitiateParametersLog, ConsentInitiateHeadersLog, ConsentsLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            ConsentInitiateParameters path, ConsentInitiateHeaders headers, Consents body);

    ConsentInitiateParametersLog mapFromConsentInitiateParametersToConsentInitiateParametersLog(ConsentInitiateParameters path);

    ConsentInitiateHeadersLog mapFromConsentInitiateHeadersToConsentInitiateHeadersLog(ConsentInitiateHeaders headers);

    ConsentsLog mapFromConsentsToConsentsLog(Consents body);


    ValidatedPathHeadersBodyLog<Xs2aInitialPaymentParametersLog, PaymentInitiateHeadersLog, PaymentInitiationJsonLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            Xs2aInitialPaymentParameters path, PaymentInitiateHeaders headers, PaymentInitiationJson body);

    Xs2aInitialPaymentParametersLog mapFromConsentInitiateParametersToConsentInitiateParametersLog(Xs2aInitialPaymentParameters path);

    PaymentInitiateHeadersLog mapFromConsentInitiateHeadersToConsentInitiateHeadersLog(PaymentInitiateHeaders headers);

    PaymentInitiationJsonLog mapFromConsentsToConsentsLog(PaymentInitiationJson body);


    ValidatedPathHeadersBodyLog<Xs2aAuthorizedConsentParametersLog, Xs2aStandardHeadersLog, TransactionAuthorisationLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, TransactionAuthorisation body);

    Xs2aAuthorizedConsentParametersLog mapFromXs2aAuthorizedConsentParametersToXs2aAuthorizedConsentParametersLog(Xs2aAuthorizedConsentParameters path);

    TransactionAuthorisationLog mapFromTransactionAuthorisationToTransactionAuthorisationLog(TransactionAuthorisation body);


    ValidatedPathHeadersBodyLog<Xs2aAuthorizedPaymentParametersLog, Xs2aStandardHeadersLog, TransactionAuthorisationLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            Xs2aAuthorizedPaymentParameters path, Xs2aStandardHeaders headers, TransactionAuthorisation body);

    Xs2aAuthorizedPaymentParametersLog mapFromXs2aAuthorizedPaymentParametersToXs2aAuthorizedPaymentParametersLog(Xs2aAuthorizedPaymentParameters path);


    ValidatedPathHeadersBodyLog<Xs2aAuthorizedPaymentParametersLog, Xs2aStandardHeadersLog, UpdatePsuAuthenticationLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            Xs2aAuthorizedPaymentParameters path, Xs2aStandardHeaders headers, UpdatePsuAuthentication body);


    ValidatedPathHeadersBodyLog<Xs2aAuthorizedPaymentParametersLog, Xs2aStandardHeadersLog, SelectPsuAuthenticationMethodLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            Xs2aAuthorizedPaymentParameters path, Xs2aStandardHeaders headers, SelectPsuAuthenticationMethod body);


    ValidatedPathHeadersBodyLog<Xs2aAuthorizedConsentParametersLog, Xs2aStandardHeadersLog, UpdatePsuAuthenticationLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, UpdatePsuAuthentication body);

    UpdatePsuAuthenticationLog mapFromUpdatePsuAuthenticationToUpdatePsuAuthenticationLog(UpdatePsuAuthentication body);

    PsuDataLog mapFromPsuDataToPsuDataLog(PsuData psuData);


    ValidatedPathHeadersLog<Xs2aInitialConsentParametersLog, Xs2aStandardHeadersLog> mapFromPathHeadersToXs2aValidatedPathHeadersLog(
            Xs2aInitialConsentParameters path, Xs2aStandardHeaders headers);

    Xs2aInitialConsentParametersLog mapFromXs2aInitialConsentParametersToXs2aInitialConsentParametersLog(Xs2aInitialConsentParameters path);

    Xs2aStandardHeadersLog mapFromXs2aStandardHeadersToXs2aStandardHeadersLog(Xs2aStandardHeaders headers);


    ValidatedPathHeadersLog<Xs2aStartPaymentAuthorizationParametersLog, Xs2aStandardHeadersLog> mapFromPathHeadersToXs2aValidatedPathHeadersLog(
            Xs2aStartPaymentAuthorizationParameters path, Xs2aStandardHeaders headers);

    Xs2aStartPaymentAuthorizationParametersLog mapFromXs2aStartPaymentAuthorizationParametersToXs2aStartPaymentAuthorizationParametersLog(Xs2aStartPaymentAuthorizationParameters path);


    ValidatedPathHeadersBodyLog<Xs2aAuthorizedConsentParametersLog, Xs2aStandardHeadersLog, SelectPsuAuthenticationMethodLog> mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(
            Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, SelectPsuAuthenticationMethod body);

    SelectPsuAuthenticationMethodLog mapFromSelectPsuAuthenticationMethodToSelectPsuAuthenticationMethodLog(SelectPsuAuthenticationMethod body);


    AmountLog mapAmountToAmountLog(Amount amount);

    AddressLog mapAddressToAddressLog(Address address);


    AccountReferenceLog mapAccountReferenceToAccountReferenceLog(AccountReference accountReference);

    AccountAccessLog mapAccountAccessToAccountAccessLog(AccountAccess access);

    //responses mappers

    TokenResponseLog mapFromTokenResponseToTokenResponseLog(TokenResponse response);

    URILog mapFromURIToURILog(URI response);

}
