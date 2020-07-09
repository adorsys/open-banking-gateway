package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.net.URI;

import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationResult extends Stage<AccountInformationResult>  {

    private static final int ANTON_BRUECKNER_BOOKED_TRANSACTIONS_COUNT = 8;

    @ExpectedScenarioState
    private String redirectOkUri;

    @Getter
    @ExpectedScenarioState
    private String responseContent;

    @LocalServerPort
    private int serverPort;

    @BeforeStage
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
    }

    @SneakyThrows
    public AccountInformationResult open_banking_reads_anton_brueckner_accounts_on_redirect() {
        ExtractableResponse<Response> response = RestAssured
                .when()
                    .get(URI.create(redirectOkUri).getPath())
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("iban", contains("DE80760700240271232400"))
                    .body("iban", hasSize(1))
                    .body("currency", contains("EUR"))
                    .body("currency", hasSize(1))
                .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_reads_anton_brueckner_transactions_validated_by_iban() {
        ExtractableResponse<Response> response = RestAssured
                .when()
                    .get(URI.create(redirectOkUri).getPath())
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract();
        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].creditorAccount.iban")).asList()
                .containsOnly(
                        "DE67760700240243265400",
                        "DE23760700240234367800",
                        "DE80760700240271232400",
                        "DE84100100100568753108",
                        "DE38760700240320465700"
                );

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].debtorAccount.iban")).asList()
                .containsOnly("DE80760700240271232400", "DE38760700240320465700");

        assertThat(body)
                .extracting(it -> it.read("$.transactions.booked[*].transactionAmount.amount"))
                .asList()
                .extracting(it -> new BigDecimal((String) it))
                .usingElementComparator(BIG_DECIMAL_COMPARATOR)
                // Looks like returned order by Sandbox is not stable
                .containsExactlyInAnyOrder(
                        new BigDecimal("-150.00"),
                        new BigDecimal("-100.00"),
                        new BigDecimal("-2300.00"),
                        new BigDecimal("-250.00"),
                        new BigDecimal("2300.00"),
                        new BigDecimal("-900.00"),
                        new BigDecimal("-700.00"),
                        new BigDecimal("30000.00")
                );
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_reads_anton_brueckner_transactions_on_redirect() {
        RestAssured
                .when()
                 .get(URI.create(redirectOkUri).getPath())
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("transactions.booked.transactionId",
                        containsInAnyOrder(
                                "rnvGvu2TR2Yl99bAoM_skY",
                                "1Lag4mgPRy4kLuz1rRifJ4",
                                "xKVwpTr9TaAoW9j1Zem4Tw",
                                "GrrnMdDgTGIjM-w_kkTVSA",
                                "mfSdvTvYThwr8hocMJMsxA",
                                "Tt7Os27bTc0vC6jDk0f5lY",
                                "qlI0mwopQIknL0n-U4bD80",
                                "pG7GZlccRPsoBNudHnX25Q"
                        )
                    )
                    .body("transactions.booked", hasSize(ANTON_BRUECKNER_BOOKED_TRANSACTIONS_COUNT));
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_has_max_musterman_accounts() {
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.[*].iban")).asList().containsExactly("DE38760700240320465700");
        assertThat(body).extracting(it -> it.read("$.[*].currency")).asList().containsExactly("EUR");

        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_has_max_musterman_transactions() {
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].transactionId")).asList()
                .containsExactlyInAnyOrder(
                        "VHF5-8R1RCcskezln6CJAY",
                        "etA9KGhIT9ohX9dYXrhzc8",
                        "LjwVWzBBQtwpyQ6WBBTiwk",
                        "pkOyTAHDTb0uCF2R55HKKo",
                        "F3qVhSXlQswswIN2nk1rBo"
                );

        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_has_max_musterman_transactions_validated_by_iban() {
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].creditorAccount.iban")).asList()
                .containsOnly(
                        "DE69760700240340283600",
                        "DE80760700240271232400",
                        "DE38760700240320465700"
                );

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].debtorAccount.iban")).asList()
                .containsOnly("DE38760700240320465700", "DE80760700240271232400", "DE69760700240340283600");

        assertThat(body)
                .extracting(it -> it.read("$.transactions.booked[*].transactionAmount.amount"))
                .asList()
                .extracting(it -> new BigDecimal((String) it))
                .usingElementComparator(BIG_DECIMAL_COMPARATOR)
                .containsExactly(
                        new BigDecimal("-1280.00"),
                        new BigDecimal("-2300.00"),
                        new BigDecimal("700.00"),
                        new BigDecimal("3000.00"),
                        new BigDecimal("10000.00")
                );
        return self();
    }
}
