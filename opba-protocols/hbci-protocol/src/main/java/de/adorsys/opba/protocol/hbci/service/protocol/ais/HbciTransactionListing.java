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
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service("hbciTransactionListing")
@RequiredArgsConstructor
public class HbciTransactionListing extends ValidatedExecution<TransactionListHbciContext> {

    private final OnlineBankingService onlineBankingService;

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListHbciContext context) {
        HbciConsent consent = context.getHbciDialogConsent();
        TransactionRequest<LoadTransactions> request = create(new LoadTransactions(), new BankApiUser(), new BankAccess(), context.getBank(), consent);
        BankAccount account = new BankAccount();
        account.setIban(context.getAccountIban());
        request.getTransaction().setPsuAccount(account);
        TransactionsResponse response = onlineBankingService.loadTransactions(request);

        if (null == response.getAuthorisationCodeResponse()) {
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

        onlineBankingService.getStrongCustomerAuthorisation().afterExecute(consent, response.getAuthorisationCodeResponse());
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                    ctx.setTanChallengeRequired(true);
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
