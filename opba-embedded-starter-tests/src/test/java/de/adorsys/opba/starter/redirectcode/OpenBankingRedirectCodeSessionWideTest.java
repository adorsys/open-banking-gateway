package de.adorsys.opba.starter.redirectcode;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "facade.urls.redirect.session-wide-redirect-code=true")
public class OpenBankingRedirectCodeSessionWideTest extends OpenBankingRedirectCodeTest {

    @Test
    void testRedirectCodeIsDistinctOnPropertyNotSet() {
        var listResp = xs2aAccountList(HttpStatus.ACCEPTED);
        var sessionId = listResp.header(SERVICE_SESSION_ID);
        var loginResp = xs2aLoginToSession(getRedirectCode(listResp), sessionId);
        var authCookie = loginResp.cookie(AUTHORIZATION_SESSION_KEY);
        var provideMoreFirst = xs2aUpdateConsentAuthorization(sessionId, "{}", authCookie, getRedirectCode(loginResp));
        var provideMoreSecond = xs2aUpdateConsentAuthorization(sessionId, "{}", authCookie, getRedirectCode(provideMoreFirst));

        assertThat(getRedirectCode(loginResp)).isEqualTo(provideMoreFirst.header(REDIRECT_CODE));
        assertThat(provideMoreFirst.header(REDIRECT_CODE)).isEqualTo(provideMoreSecond.header(REDIRECT_CODE));
    }
}
