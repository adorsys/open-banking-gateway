package de.adorsys.opba.protocol.hbci.service.protocol.ais;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.TransactionsResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.multibanking.domain.transaction.LoadTransactions;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("hbciTransactionListing")
@RequiredArgsConstructor
public class HbciTransactionListing extends ValidatedExecution<HbciContext> {

    private final ApplicationEventPublisher eventPublisher;
    private final OnlineBankingService onlineBankingService;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        TransactionRequest<LoadTransactions> request = create(new LoadTransactions(), new BankApiUser(), new BankAccess(), context.getBank(), context.getHbciDialogConsent());
        TransactionsResponse response = onlineBankingService.loadTransactions(request);

        if (null == response.getAuthorisationCodeResponse()) {
            eventPublisher.publishEvent(new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), response.getBookings()));
            return;
        }

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setTanChallengeRequired(true));
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
