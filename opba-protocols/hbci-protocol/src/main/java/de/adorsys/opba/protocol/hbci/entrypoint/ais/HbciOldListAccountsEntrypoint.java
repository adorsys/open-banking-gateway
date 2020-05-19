package de.adorsys.opba.protocol.hbci.entrypoint.ais;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.AbstractResponse;
import de.adorsys.multibanking.domain.response.AccountInformationResponse;
import de.adorsys.multibanking.domain.response.TransactionAuthorisationResponse;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.multibanking.domain.transaction.BulkPayment;
import de.adorsys.multibanking.domain.transaction.ForeignPayment;
import de.adorsys.multibanking.domain.transaction.FutureBulkPayment;
import de.adorsys.multibanking.domain.transaction.FutureSinglePayment;
import de.adorsys.multibanking.domain.transaction.LoadAccounts;
import de.adorsys.multibanking.domain.transaction.LoadBalances;
import de.adorsys.multibanking.domain.transaction.LoadStandingOrders;
import de.adorsys.multibanking.domain.transaction.LoadTransactions;
import de.adorsys.multibanking.domain.transaction.PaymentStatusReqest;
import de.adorsys.multibanking.domain.transaction.RawSepaPayment;
import de.adorsys.multibanking.domain.transaction.SinglePayment;
import de.adorsys.multibanking.domain.transaction.StandingOrderRequest;
import de.adorsys.multibanking.domain.transaction.TanRequest;
import de.adorsys.multibanking.domain.transaction.TransactionAuthorisation;
import de.adorsys.multibanking.hbci.HbciBpdUpdCallback;
import de.adorsys.multibanking.hbci.job.AccountInformationJob;
import de.adorsys.multibanking.hbci.job.BulkPaymentJob;
import de.adorsys.multibanking.hbci.job.DeleteFutureBulkPaymentJob;
import de.adorsys.multibanking.hbci.job.DeleteFutureSinglePaymentJob;
import de.adorsys.multibanking.hbci.job.DeleteStandingOrderJob;
import de.adorsys.multibanking.hbci.job.ForeignPaymentJob;
import de.adorsys.multibanking.hbci.job.InstantPaymentStatusJob;
import de.adorsys.multibanking.hbci.job.LoadBalancesJob;
import de.adorsys.multibanking.hbci.job.LoadStandingOrdersJob;
import de.adorsys.multibanking.hbci.job.LoadTransactionsJob;
import de.adorsys.multibanking.hbci.job.NewStandingOrderJob;
import de.adorsys.multibanking.hbci.job.RawSepaJob;
import de.adorsys.multibanking.hbci.job.ScaAwareJob;
import de.adorsys.multibanking.hbci.job.SinglePaymentJob;
import de.adorsys.multibanking.hbci.job.TanRequestJob;
import de.adorsys.multibanking.hbci.job.TransactionAuthorisationJob;
import de.adorsys.multibanking.hbci.job.TransferJob;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Resources;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIProduct;
import org.kapott.hbci.manager.HBCIUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.multibanking.domain.ScaStatus.FINALISED;
import static de.adorsys.opba.protocol.hbci.entrypoint.ais.HbciCacheHandler.createCallback;
import static org.kapott.hbci.manager.HBCIVersion.HBCI_300;

/**
 * Entry point that handles ListAccounts request from the FinTech.
 */
@Slf4j
@Service("hbciOldListAccounts")
public class HbciOldListAccountsEntrypoint implements ListAccounts {

    private static final String MOCK_BANK_CODE = "123456";

    private long sysIdExpirationTimeMs = 10000L;
    private long updExpirationTimeMs = 1000L;

    @SneakyThrows
    public HbciOldListAccountsEntrypoint() {
        try (InputStream is = Resources.getInputStream("blz.properties")) {
            HBCIUtils.refreshBLZList(is);
        }

        // Initiate MOCK bank
        BankInfo bankInfo = new BankInfo();
        bankInfo.setBlz(MOCK_BANK_CODE);
        bankInfo.setPinTanAddress("http://localhost:8090/hbci-mock/");
        bankInfo.setPinTanVersion(HBCI_300);
        bankInfo.setBic(System.getProperty("bic"));
        HBCIUtils.addBankInfo(bankInfo);
    }

    @Override
    public CompletableFuture<Result<AccountListBody>> execute(ServiceContext<ListAccountsRequest> serviceContext) {
        Bank bank = new Bank();
        bank.setBankCode(MOCK_BANK_CODE);
        HbciConsent consent = new HbciConsent();
        consent.setHbciProduct(new HBCIProduct("product", "300"));
        consent.setCredentials(Credentials.builder()
                .customerId("foo-bar")
                .userId("user-id")
                .pin("12456")
                .build()
        );

        TransactionRequest<LoadAccounts> request = create(new LoadAccounts(), new BankApiUser(), new BankAccess(), bank, consent);

        HbciConsent hbciConsent = (HbciConsent) request.getBankApiConsentData();
        hbciConsent.checkUpdSysIdCache(sysIdExpirationTimeMs, updExpirationTimeMs);

        try {
            if (hbciConsent.getHbciTanSubmit() == null || hbciConsent.getStatus() == FINALISED) {
                HbciBpdUpdCallback hbciCallback = createCallback(request);

                AccountInformationJob accountInformationJob = new AccountInformationJob(request);
                AccountInformationResponse response = accountInformationJob.execute(hbciCallback);
                response.setBankApiConsentData(hbciCallback.updateConsentUpd(hbciConsent));
                return CompletableFuture.completedFuture(new SuccessResult<>(AccountListBody.builder().build()));
            } else {
                TransactionAuthorisationResponse<? extends AbstractResponse> transactionAuthorisationResponse =
                        transactionAuthorisation(new TransactionAuthorisation<>(request));

                hbciConsent.afterTransactionAuthorisation(transactionAuthorisationResponse.getScaStatus());

                return CompletableFuture.completedFuture(new AuthorizationRequiredResult<>(URI.create("http://example.com"), null));
            }
        } catch (HBCI_Exception e) {
            log.error("Failed HBCI account list", e);
            throw e;
        }
    }

    private <T extends AbstractTransaction, R extends AbstractResponse> TransactionAuthorisationResponse<R> transactionAuthorisation(TransactionAuthorisation<T> transactionAuthorisation) {
        createCallback(transactionAuthorisation.getOriginTransactionRequest());
        try {
            ScaAwareJob<T, R> scaJob = createScaJob(transactionAuthorisation.getOriginTransactionRequest());

            TransactionAuthorisationJob<T, R> transactionAuthorisationJob = new TransactionAuthorisationJob<>(scaJob, transactionAuthorisation);
            TransactionAuthorisationResponse<R> response = transactionAuthorisationJob.execute();

            HbciConsent hbciConsent = ((HbciConsent) transactionAuthorisation.getOriginTransactionRequest().getBankApiConsentData());
            hbciConsent.afterTransactionAuthorisation(response.getScaStatus());

            return response;
        } catch (HBCI_Exception e) {
            log.error("Failed HBCI account list", e);
            throw e;
        }
    }

    public static <T extends AbstractTransaction> TransactionRequest<T> create(T transaction,
                                                                               BankApiUser bankApiUser,
                                                                               BankAccess bankAccess,
                                                                               Bank bank,
                                                                               Object bankApiConsentData) {
        TransactionRequest<T> transactionRequest = new TransactionRequest<>(transaction);
        transactionRequest.setBankApiUser(bankApiUser);
        transactionRequest.setBankAccess(bankAccess);
        transactionRequest.setBankApiConsentData(bankApiConsentData);
        transactionRequest.setBank(bank);

        return transactionRequest;
    }

    private <T extends AbstractTransaction, R extends AbstractResponse> ScaAwareJob<T, R> createScaJob(TransactionRequest<T> transactionRequest) {
        switch (transactionRequest.getTransaction().getTransactionType()) {
            case SINGLE_PAYMENT:
            case FUTURE_SINGLE_PAYMENT:
            case INSTANT_PAYMENT:
                return (ScaAwareJob<T, R>) new SinglePaymentJob((TransactionRequest<SinglePayment>) transactionRequest);
            case TRANSFER_PAYMENT:
                return (ScaAwareJob<T, R>) new TransferJob((TransactionRequest<SinglePayment>) transactionRequest);
            case FOREIGN_PAYMENT:
                return (ScaAwareJob<T, R>) new ForeignPaymentJob((TransactionRequest<ForeignPayment>) transactionRequest);
            case BULK_PAYMENT:
            case FUTURE_BULK_PAYMENT:
                return (ScaAwareJob<T, R>) new BulkPaymentJob((TransactionRequest<BulkPayment>) transactionRequest);
            case STANDING_ORDER:
                return (ScaAwareJob<T, R>) new NewStandingOrderJob((TransactionRequest<StandingOrderRequest>) transactionRequest);
            case RAW_SEPA:
                return (ScaAwareJob<T, R>) new RawSepaJob((TransactionRequest<RawSepaPayment>) transactionRequest);
            case FUTURE_SINGLE_PAYMENT_DELETE:
                return (ScaAwareJob<T, R>) new DeleteFutureSinglePaymentJob((TransactionRequest<FutureSinglePayment>) transactionRequest);
            case FUTURE_BULK_PAYMENT_DELETE:
                return (ScaAwareJob<T, R>) new DeleteFutureBulkPaymentJob((TransactionRequest<FutureBulkPayment>) transactionRequest);
            case STANDING_ORDER_DELETE:
                return (ScaAwareJob<T, R>) new DeleteStandingOrderJob((TransactionRequest<StandingOrderRequest>) transactionRequest);
            case TAN_REQUEST:
                return (ScaAwareJob<T, R>) new TanRequestJob((TransactionRequest<TanRequest>) transactionRequest);
            case LOAD_BANKACCOUNTS:
                return (ScaAwareJob<T, R>) new AccountInformationJob((TransactionRequest<LoadAccounts>) transactionRequest);
            case LOAD_BALANCES:
                return (ScaAwareJob<T, R>) new LoadBalancesJob((TransactionRequest<LoadBalances>) transactionRequest);
            case LOAD_TRANSACTIONS:
                return (ScaAwareJob<T, R>) new LoadTransactionsJob((TransactionRequest<LoadTransactions>) transactionRequest);
            case LOAD_STANDING_ORDERS:
                return (ScaAwareJob<T, R>) new LoadStandingOrdersJob((TransactionRequest<LoadStandingOrders>) transactionRequest);
            case GET_PAYMENT_STATUS:
                return (ScaAwareJob<T, R>) new InstantPaymentStatusJob((TransactionRequest<PaymentStatusReqest>) transactionRequest);
            default:
                throw new IllegalArgumentException("invalid transaction type " + transactionRequest.getTransaction().getTransactionType());
        }
    }

}
