package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import de.adorsys.opba.fintech.server.config.TestConfig;
import de.adorsys.opba.fintech.server.feignmocks.TppAisClientFeignMock;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.SERVICE_SESSION_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.TPP_AUTH_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@Slf4j
public class FinTechListAccountsTest extends FinTechBankSearchApiTest {
    private static final String FIN_TECH_LIST_ACCOUNTS_URL = "/v1/ais/banks/{bank-id}/accounts";

    private static final UUID NO_CONSENT_BANK_ID = UUID.fromString("356938ab-9561-408f-ac7a-a9089c1623b7");
    private static final String USERNAME = "peter";
    private static final String PASSWORD = "1234";

    @MockBean
    protected TppAisClientFeignMock tppAisClientFeignMock;

    @SuppressWarnings("PMD.UnusedPrivateField")
    @MockBean
    protected TppBankingApiAccountInformationServiceAisApi mockedTppBankingApiAccountInformationServiceAisApi;

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @SneakyThrows
    public void testListAccountsFor200() {
        BankProfileTestResult result = getBankProfileTestResult();
        createConsent(UUID.randomUUID().toString(), UUID.randomUUID());
        List<String> accountIDs = listAccountsForOk(result);
        assertTrue(accountIDs.containsAll(Arrays.asList(new String[]{"12345", "67890"})));
    }

    @SneakyThrows
    List<String> listAccountsForOk(BankProfileTestResult result) {
        var tppAisClientFeignMockAccounts = tppAisClientFeignMock.getAccounts(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any(), any(), any(), any(), any(), any(), any(), any());
        when(tppAisClientFeignMockAccounts).thenReturn(ResponseEntity.ok(GSON.fromJson(readFile("TPP_LIST_ACCOUNTS.json"), AccountList.class)));

        MvcResult mvcResult = plainListAccounts(result.getBankUUID());
        assertEquals(OK.value(), mvcResult.getResponse().getStatus());
        log.info("GOT RESULT STRING: {}", mvcResult.getResponse().getContentAsString());
        List<String> accountIDs = new ArrayList<>();
        JSONArray accounts = new JSONObject(mvcResult.getResponse().getContentAsString())
                .getJSONArray("accounts");
        for (int i = 0; i < accounts.length(); i++) {
            accountIDs.add(accounts.getJSONObject(i).getString("resourceId"));
        }
        return accountIDs;
    }

    @Test
    @SneakyThrows
    public void testListAccountsFor303() {
        ResponseEntity accepted = ResponseEntity.accepted()
                .header(TPP_AUTH_ID, "1")
                .header(SERVICE_SESSION_ID, "682dbd06-75d4-4f73-a7e7-9084150a1f10")
                .location(new URI("affe"))
                .build();
        BankProfileTestResult result = getBankProfileTestResult();
        createConsent(null, null);
        var transactionsWithoutAccountId = tppAisClientFeignMock.getTransactionsWithoutAccountId(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any(), any(), any(), any(), any(), any(), any(), any(), any());
        when(transactionsWithoutAccountId).thenReturn(accepted);
        MvcResult mvcResult = plainListAccounts(result.getBankUUID());
        assertEquals(ACCEPTED.value(), mvcResult.getResponse().getStatus());
        verify(tppAisClientFeignMock).getTransactionsWithoutAccountId(any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(tppAisClientFeignMock, never()).getAccounts(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                                                           any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                                                           any(), any(), any());
    }

    @Test
    @SneakyThrows
    public void testListAccountsFor303NoConsentSupport() {
        when(tppBankSearchClientFeignMock.bankProfileGET(any(), eq(NO_CONSENT_BANK_ID), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(GSON.fromJson(readFile(getFilenameBankProfile(NO_CONSENT_BANK_ID)), BankProfileResponse.class)));
        when(restRequestContext.getRequestId()).thenReturn(UUID.randomUUID().toString());

        authOk(USERNAME, PASSWORD);
        ResponseEntity accepted = ResponseEntity.accepted()
                .header(TPP_AUTH_ID, "1")
                .header(SERVICE_SESSION_ID, "682dbd06-75d4-4f73-a7e7-9084150a1f10")
                .location(new URI("affe"))
                .build();
        createConsent(null, null);
        when(tppAisClientFeignMock.getAccounts(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any(), any(), any(), any())).thenReturn(accepted);

        MvcResult mvcResult = plainListAccounts(NO_CONSENT_BANK_ID);
        assertEquals(ACCEPTED.value(), mvcResult.getResponse().getStatus());
        verify(tppAisClientFeignMock, never()).getTransactions(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), // CPD-OFF
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(tppAisClientFeignMock).getAccounts(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                                                  any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()); // CPD-ON
    }


    @SneakyThrows
    MvcResult plainListAccounts(UUID bankProfileUUID) {
        log.info("bankProfileUUID {}", bankProfileUUID);
        return this.mvc
                .perform(get(FIN_TECH_LIST_ACCOUNTS_URL, bankProfileUUID)
                        .header(Consts.HEADER_X_REQUEST_ID, restRequestContext.getRequestId())
                        .header(Consts.HEADER_XSRF_TOKEN, restRequestContext.getXsrfTokenHeaderField())
                        .header("Fintech-Redirect-URL-OK", "ok")
                        .header("Fintech-Redirect-URL-NOK", "notok")
                        .header("LoARetrievalInformation", "FROM_TPP_WITH_AVAILABLE_CONSENT")
                .param("withBalance", Boolean.FALSE.toString()))
                .andDo(print())
                .andReturn();
    }

    @Override
    public void bankProfileAuthorized() {
    }

    @Override
    public void bankSearchAuthorized() {
    }

    @Override
    public void bankSearchUnAuthorized() {
    }

    @Override
    public void loginPostOk() {
    }

    @Override
    public void loginPostUnAuthorized() {
    }

    protected void createConsent(String tppAuthId, UUID serviceSessionId) {
        if (tppAuthId == null) {
            return;
        }
        final String bankId = "af062b06-ee6e-45f9-9163-b97320c6881a";
        UserEntity userEntity = userRepository.findById("peter").get();
        ConsentEntity consentEntity = new ConsentEntity(ConsentType.AIS, userEntity, bankId, null, tppAuthId, serviceSessionId);
        consentEntity.setConsentConfirmed(true);
        consentRepository.save(consentEntity);
    }
}
