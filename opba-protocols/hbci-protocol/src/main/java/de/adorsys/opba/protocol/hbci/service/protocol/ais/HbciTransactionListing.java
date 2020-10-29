package de.adorsys.opba.protocol.hbci.service.protocol.ais;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankAccount;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.TransactionsResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.multibanking.domain.transaction.LoadTransactions;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.service.consent.HbciScaRequiredUtil;
import de.adorsys.opba.protocol.hbci.service.consent.authentication.HbciAuthorizationPossibleErrorHandler;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service("hbciTransactionListing")
@RequiredArgsConstructor
@Slf4j
public class HbciTransactionListing extends ValidatedExecution<TransactionListHbciContext> {

    private final OnlineBankingService onlineBankingService;
    private final HbciAuthorizationPossibleErrorHandler errorSink;

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        errorSink.handlePossibleAuthorizationError(
                () -> {

                    HbciConsent consent = context.getHbciDialogConsent();
                    TransactionRequest<LoadTransactions> request = create(new LoadTransactions(), new BankApiUser(), new BankAccess(), context.getBank(), consent);
                    BankAccount account = new BankAccount();
                    account.setIban(context.getAccountIban());
                    request.getTransaction().setPsuAccount(account);
                    TransactionsResponse response = onlineBankingService.loadTransactions(request);
                    boolean postScaRequired = HbciScaRequiredUtil.extraCheckIfScaRequired(response);

                    if (null == response.getAuthorisationCodeResponse() && !postScaRequired) {
                        ContextUtil.getAndUpdateContext(
                                execution,
                                (TransactionListHbciContext ctx) -> {
                                    ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                                    ctx.setResponse(
                                            new AisListTransactionsResult(
                                                    response.getBookings(),
                                                    response.getBalancesReport(),
                                                    Instant.now()
                                            )
                                    );
                                    ctx.setTanChallengeRequired(false);
                                }
                        );

                        return;
                    }

                    if (null != response.getAuthorisationCodeResponse()) {
                        onlineBankingService.getStrongCustomerAuthorisation().afterExecute(consent, response.getAuthorisationCodeResponse());
                    }
                    ContextUtil.getAndUpdateContext(
                            execution,
                            (HbciContext ctx) -> {
                                ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                                ctx.setTanChallengeRequired(true);
                            }
                    );
                },
                ex -> aisOnWrongCredentials(execution)
        );

    }

    private void aisOnWrongCredentials(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect credentials in HbciTransactionListsing", ctx.getRequestId(), ctx.getSagaId());
                    log.info("set wrong credentials to true");
                    ctx.setWrongAuthCredentials(true);
                }
        );
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
}
