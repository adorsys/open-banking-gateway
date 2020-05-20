package de.adorsys.opba.protocol.hbci.service.protocol.ais;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.AccountInformationResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.multibanking.domain.transaction.LoadAccounts;
import de.adorsys.multibanking.hbci.HbciBanking;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Resources;
import org.flowable.engine.delegate.DelegateExecution;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIProduct;
import org.kapott.hbci.manager.HBCIUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static org.kapott.hbci.manager.HBCIVersion.HBCI_300;

@Slf4j
@Service("hbciAccountList")
public class HbciAccountList extends ValidatedExecution<AccountListHbciContext> {

    private static final String MOCK_BANK_CODE = "123456";

    private long sysIdExpirationTimeMs = 10000L;
    private long updExpirationTimeMs = 1000L;

    private final OnlineBankingService onlineBankingService;

    @SneakyThrows
    public HbciAccountList() {
        try (InputStream is = Resources.getInputStream("blz.properties")) {
            HBCIUtils.refreshBLZList(is);
        }

        // Initiate MOCK bank
        BankInfo bankInfo = new BankInfo();
        bankInfo.setBlz(MOCK_BANK_CODE);
        bankInfo.setPinTanAddress("http://localhost:8090/hbci-mock/");
        bankInfo.setPinTanVersion(HBCI_300);
        bankInfo.setBic(System.getProperty("bic"));
        onlineBankingService = new HbciBanking(new HBCIProduct("product", "300"), sysIdExpirationTimeMs, updExpirationTimeMs);
        HBCIUtils.addBankInfo(bankInfo);
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, AccountListHbciContext context) {
        Bank bank = new Bank();
        bank.setBankCode(MOCK_BANK_CODE);
        HbciConsent consent = new HbciConsent();
        consent.setHbciProduct(new HBCIProduct("product", "300"));
        consent.setCredentials(Credentials.builder()
                .userId(context.getPsuId())
                .pin(context.getPsuPin())
                .build()
        );

        TransactionRequest<LoadAccounts> request = create(new LoadAccounts(), new BankApiUser(), new BankAccess(), bank, consent);
        AccountInformationResponse response = onlineBankingService.loadBankAccounts(request);

        if (null == response.getAuthorisationCodeResponse()) {
            ContextUtil.getAndUpdateContext(
                    execution,
                    (AccountListHbciContext ctx) -> ctx.setResponse(
                            new AisListAccountsResult(response.getBankAccounts()))
            );
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
