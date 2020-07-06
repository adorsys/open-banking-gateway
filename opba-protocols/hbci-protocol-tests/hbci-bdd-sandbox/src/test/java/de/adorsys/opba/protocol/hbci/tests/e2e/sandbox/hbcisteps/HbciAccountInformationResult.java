package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;

@JGivenStage
public class HbciAccountInformationResult<SELF extends HbciAccountInformationResult<SELF>> extends AccountInformationResult<SELF> {

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_hbci_account_data_using_consent_bound_to_service_session_bank_blz_30000003() {
        ExtractableResponse<Response> response = withAccountsHeaders(MAX_MUSTERMAN, BANK_BLZ_30000003_ID, requestSigningService, OperationType.AIS)
                .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("accounts[0].iban", equalTo("DE59300000033466865655"))
                .body("accounts[0].resourceId", instanceOf(String.class))
                .body("accounts[0].currency", equalTo("EUR"))
                .body("accounts[0].name", equalTo("Extra-Konto"))
                .body("accounts[1].iban", equalTo("DE13300000032278292697"))
                .body("accounts[1].resourceId", instanceOf(String.class))
                .body("accounts[1].currency", equalTo("EUR"))
                .body("accounts[1].name", equalTo("Extra-Konto"))
                .body("accounts", hasSize(2))
                .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_anton_brueckner_hbci_account_data_using_consent_bound_to_service_session_bank_blz_30000003() {
        ExtractableResponse<Response> response = withAccountsHeaders(ANTON_BRUECKNER, BANK_BLZ_30000003_ID, requestSigningService, OperationType.AIS)
                .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("accounts[0].iban", equalTo("DE65300000035827542519"))
                .body("accounts[0].resourceId", instanceOf(String.class))
                .body("accounts[0].currency", equalTo("EUR"))
                .body("accounts[0].name", equalTo("Extra-Konto"))
                .body("accounts[1].iban", equalTo("DE17300000039185286653"))
                .body("accounts[1].resourceId", instanceOf(String.class))
                .body("accounts[1].currency", equalTo("EUR"))
                .body("accounts[1].name", equalTo("Extra-Konto"))
                .body("accounts", hasSize(2))
                .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_bank_blz_20000002(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        return open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(resourceId, BANK_BLZ_30000003_ID, dateFrom, dateTo, bookingStatus);
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_bank_blz_30000003(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        return open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(resourceId, BANK_BLZ_30000003_ID, dateFrom, dateTo, bookingStatus);
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(
            String resourceId, String bankId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(MAX_MUSTERMAN, bankId, resourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        // TODO: Currently no IBANs as mapping is not yet completed

        assertThat(body)
                .extracting(it -> it.read("$.transactions.booked[*].transactionAmount.amount"))
                .asList()
                .extracting(it -> new BigDecimal((String) it))
                .usingElementComparator(BIG_DECIMAL_COMPARATOR)
                // Looks like returned order by Sandbox is not stable
                .containsOnly(
                        new BigDecimal("-100.00")
                );
        return self();
    }
}
