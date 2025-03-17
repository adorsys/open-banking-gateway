package de.adorsys.opba.fintech.server;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.collect.ImmutableList;
import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.config.GmailOauth2Config;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import de.adorsys.opba.fintech.server.config.TestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.service.oauth2.Oauth2Const.COOKIE_OAUTH2_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class GmailOAuth2AuthenticationTest {

    private static final String FIN_TECH_INITIATE_OAUTH2 = "/v1/oauth2/{idpProvider}/login";
    private static final String FIN_TECH_BANK_OAUTH2_LOGIN = "/v1/login/oauth2?code={code}&state={state}&scope={scope}";
    private static final String SCOPE = "openid email";
    private static final String CODE_TO_BE_EXCHANGED = "code-to-be-exchanged";

    private static WireMockServer gmailOauthServer;

    @Autowired
    private GmailOauth2Config gmailOauth2Config;

    @Autowired
    protected MockMvc mvc;

    @BeforeAll
    static void startAndConfigureWiremockAndBindings() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort().usingFilesUnderClasspath("wiremock/gmail-oauth");
        gmailOauthServer = new WireMockServer(config);
        gmailOauthServer.start();
    }

    @AfterAll
    static void stopWiremock() {
        if (null != gmailOauthServer) {
            gmailOauthServer.stop();
            gmailOauthServer = null;
        }
    }

    @BeforeEach
    void updateOauthLinks() {
        gmailOauth2Config.setAuthenticationEndpoint(URI.create(gmailOauthServer.baseUrl() + "/auth"));
        gmailOauth2Config.setCodeToTokenEndpoint(URI.create(gmailOauthServer.baseUrl() + "/token"));
    }

    @Test
    @SneakyThrows
    void testOAuth2AuthenticationSuccess() {
        String stateValue = oauth2RedirectUserForAuthenticationInResourceServerAndReturnRedirectState();
        assertThat(stateValue).isNotBlank();

        this.mvc.perform(
                get(FIN_TECH_BANK_OAUTH2_LOGIN, CODE_TO_BE_EXCHANGED, stateValue, SCOPE)
                        .cookie(new Cookie(COOKIE_OAUTH2_COOKIE_NAME, stateValue))
        ).andExpect(status().isOk()).andExpect(header().exists("Set-Cookie"));
    }

    @Test
    @SneakyThrows
    void testOAuth2AuthenticationWrongEmail() {
        gmailOauth2Config.setAllowedEmailsRegex(ImmutableList.of(".+@adorsys.de"));
        String stateValue = oauth2RedirectUserForAuthenticationInResourceServerAndReturnRedirectState();
        assertThat(stateValue).isNotBlank();

        this.mvc.perform(
                get(FIN_TECH_BANK_OAUTH2_LOGIN, CODE_TO_BE_EXCHANGED, stateValue, SCOPE)
                        .cookie(new Cookie(COOKIE_OAUTH2_COOKIE_NAME, stateValue))
        ).andExpect(status().isForbidden()).andExpect(jsonPath("$.message", is("Email is not allowed: vbe@example.com")));
    }

    @Test
    @SneakyThrows
    void testOAuth2AuthenticationWrongState() {
        String stateValue = oauth2RedirectUserForAuthenticationInResourceServerAndReturnRedirectState() + "FAKE";

        this.mvc.perform(
                get(FIN_TECH_BANK_OAUTH2_LOGIN, CODE_TO_BE_EXCHANGED, stateValue, SCOPE)
                        .cookie(new Cookie(COOKIE_OAUTH2_COOKIE_NAME, stateValue))
        ).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message", is("")));
    }

    @Test
    @SneakyThrows
    void testOAuth2AuthenticationNoStateCookie() {
        String stateValue = oauth2RedirectUserForAuthenticationInResourceServerAndReturnRedirectState();
        assertThat(stateValue).isNotBlank();

        this.mvc.perform(
                get(FIN_TECH_BANK_OAUTH2_LOGIN, CODE_TO_BE_EXCHANGED, stateValue, SCOPE)
        ).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message", is("")));
    }

    @Test
    @SneakyThrows
    void testOAuth2AuthenticationNonMatchingStateCookie() {
        String stateValue = oauth2RedirectUserForAuthenticationInResourceServerAndReturnRedirectState();
        assertThat(stateValue).isNotBlank();

        this.mvc.perform(
                get(FIN_TECH_BANK_OAUTH2_LOGIN, CODE_TO_BE_EXCHANGED, stateValue, SCOPE)
                        .cookie(new Cookie(COOKIE_OAUTH2_COOKIE_NAME, stateValue + "12345"))
        ).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message", is("")));
    }

    @Test
    @SneakyThrows
    void testOAuth2AuthenticationWrongStateReuse() {
        String stateValue = oauth2RedirectUserForAuthenticationInResourceServerAndReturnRedirectState();

        this.mvc.perform(
                get(FIN_TECH_BANK_OAUTH2_LOGIN, CODE_TO_BE_EXCHANGED, stateValue, SCOPE)
                        .cookie(new Cookie(COOKIE_OAUTH2_COOKIE_NAME, stateValue))
        ).andExpect(status().isOk()).andExpect(header().exists("Set-Cookie"));

        this.mvc.perform(
                get(FIN_TECH_BANK_OAUTH2_LOGIN, CODE_TO_BE_EXCHANGED, stateValue, SCOPE)
                        .cookie(new Cookie(COOKIE_OAUTH2_COOKIE_NAME, stateValue))
        ).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message", is("")));
    }

    @SneakyThrows
    private String oauth2RedirectUserForAuthenticationInResourceServerAndReturnRedirectState() {
        MockHttpServletResponse response = this.mvc
                .perform(
                        post(FIN_TECH_INITIATE_OAUTH2, "gmail")
                                .header(Consts.HEADER_X_REQUEST_ID, UUID.randomUUID().toString())
                )
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse();

        String location = response.getHeader("Location");
        String stateCookie = response.getCookie(COOKIE_OAUTH2_COOKIE_NAME).getValue();

        assertThat(location).isNotNull();
        URI target = URI.create(location);
        assertThat(target.toASCIIString()).startsWith(gmailOauth2Config.getAuthenticationEndpoint().toASCIIString());
        assertThat(target).hasParameter("scope", SCOPE);
        assertThat(target).hasParameter("response_type", "code");
        assertThat(target).hasParameter("client_id", "test-client-id");
        assertThat(target).hasParameter("state");
        assertThat(target).hasParameter("nonce");
        assertThat(stateCookie).isNotNull();

        String state = UriComponentsBuilder.fromUri(target).build().getQueryParams().getFirst("state");
        assertThat(state).isNotNull();
        String stateDecoded = URLDecoder.decode(state, StandardCharsets.UTF_8.name());
        assertThat(stateCookie).isEqualTo(stateDecoded);
        return stateDecoded;
    }
}
