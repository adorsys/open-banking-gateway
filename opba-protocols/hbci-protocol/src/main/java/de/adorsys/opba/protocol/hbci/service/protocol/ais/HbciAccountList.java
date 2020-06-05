package de.adorsys.opba.protocol.hbci.service.protocol.ais;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.AccountInformationResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.multibanking.domain.transaction.LoadAccounts;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Slf4j
@Service("hbciAccountList")
@RequiredArgsConstructor
public class HbciAccountList extends ValidatedExecution<AccountListHbciContext> {

    private final OnlineBankingService onlineBankingService;

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {

        HbciConsent consent = context.getHbciDialogConsent();
        TransactionRequest<LoadAccounts> request = create(new LoadAccounts(), new BankApiUser(), new BankAccess(), context.getBank(), consent);
        AccountInformationResponse response = onlineBankingService.loadBankAccounts(request);

        if (null == response.getAuthorisationCodeResponse()) {
            ContextUtil.getAndUpdateContext(
                    execution,
                    (AccountListHbciContext ctx) -> ctx.setResponse(
                            new AisListAccountsResult(response.getBankAccounts()))
            );

            return;
        }

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
