package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.multibanking.domain.request.UpdatePsuAuthenticationRequest;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.hbci.HbciBanking;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.Resources;
import org.flowable.engine.delegate.DelegateExecution;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIProduct;
import org.kapott.hbci.manager.HBCIUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static org.kapott.hbci.manager.HBCIVersion.HBCI_300;

@Service("hbciInitiateSendPinAndPsuId")
@RequiredArgsConstructor
public class HbciInitiateSendPinAndPsuId extends ValidatedExecution<HbciContext> {
    private static final String MOCK_BANK_CODE = "123456";

    private long sysIdExpirationTimeMs = 10000L;
    private long updExpirationTimeMs = 1000L;

    private final OnlineBankingService onlineBankingService;

    @SneakyThrows
    public HbciInitiateSendPinAndPsuId() {
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
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        Bank bank = new Bank();
        bank.setBankCode(MOCK_BANK_CODE);
        HbciConsent consent = new HbciConsent();
        consent.setHbciProduct(new HBCIProduct("product", "300"));
        consent.setCredentials(Credentials.builder()
                .userId(context.getPsuId())
                .pin(context.getPsuPin())
                .build()
        );

        UpdatePsuAuthenticationRequest request = new UpdatePsuAuthenticationRequest();
        request.setCredentials(consent.getCredentials());
        request.setBankApiConsentData(consent);
        request.setBank(bank);
        UpdateAuthResponse response =
                onlineBankingService.getStrongCustomerAuthorisation().updatePsuAuthentication(request);

        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData())
        );
    }
}
