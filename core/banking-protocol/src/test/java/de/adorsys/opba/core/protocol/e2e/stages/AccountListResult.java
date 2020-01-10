package de.adorsys.opba.core.protocol.e2e.stages;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@JGivenStage
public class AccountListResult extends Stage<AccountListResult>  {

    @ExpectedScenarioState
    private String redirectOkUri;

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
    public AccountListResult open_banking_reads_anton_brueckner_accounts_on_redirect() {
        RestAssured
                .when()
                    .get(URI.create(redirectOkUri).getPath())
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("iban", contains("DE80760700240271232400"))
                    .body("iban", hasSize(1))
                    .body("currency", contains("EUR"))
                    .body("currency", hasSize(1));
        return self();
    }

    @SneakyThrows
    public AccountListResult open_banking_reads_anton_brueckner_transactions_on_redirect() {
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
                    .body("transactions.booked", hasSize(8));
        return self();
    }

    @SneakyThrows
    public AccountListResult open_banking_has_max_musterman_accounts() {
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.[*].iban")).asList().containsExactly("DE38760700240320465700");
        assertThat(body).extracting(it -> it.read("$.[*].currency")).asList().containsExactly("EUR");

        return self();
    }

    @SneakyThrows
    public AccountListResult open_banking_has_max_musterman_transactions() {
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
}
