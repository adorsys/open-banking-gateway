package de.adorsys.opba.protocol.xs2a.config.protocol;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.xs2a.config.ConfigConst.XS2A_PROTOCOL_CONFIG_PREFIX;

/**
 * XS2A URL protocol configuration. Note that all URLs are expanded using {@link de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil}, so
 * you can use string interpolation like this:
 * http://localhost:8080/v1/consent/#{context.getAuthorizationSessionIdIfOpened()}/fromAspsp/STUB_STATE/ok?redirectCode=#{context.getAspspRedirectCode()}
 * The aforementioned URL will get interpolated using {@link de.adorsys.opba.protocol.xs2a.context.Xs2aContext} functions to i.e.
 * http://localhost:8080/v1/consent/53659eaf-d953-48de-a644-61c3c915d2c6/fromAspsp/STUB_STATE/ok?redirectCode=0174ef07-2e33-4264-9646-3b8965d100a0
 * with data from {@link de.adorsys.opba.protocol.xs2a.context.Xs2aContext} available at the step execution.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(XS2A_PROTOCOL_CONFIG_PREFIX + "urls")
public class ProtocolUrlsConfiguration {
    /**
     * Account related urls .
     */
    @NotNull
    private UrlSet ais;

    /**
     * Payment related urls .
     */
    @NotNull
    private UrlSet pis;

    public UrlSet getUrlAisOrPisSetBasedOnContext(Xs2aContext context) {
        return ProtocolAction.SINGLE_PAYMENT.equals(context.getAction()) ? getPis() : getAis();
    }

    @Data
    public static class UrlSet {
        /**
         * To ASPSP redirection page (for Redirect SCA).
         */
        @NotBlank
        private String toAspsp;

        /**
         * Return from ASPSP urls
         */
        @NotNull
        private WebHooks webHooks;

        /**
         * Generic parameters input urls - i.e. password page.
         */
        @NotNull
        private Parameters parameters;
    }

    @Data
    public static class WebHooks {
        /**
         * URL that represents page saying that consent creation was OK (comes before consent result page).
         */
        @NotBlank
        private String ok;

        /**
         * URL that represents page saying that consent creation was not OK (comes before consent result page).
         */
        @NotBlank
        private String nok;

        /**
         * URL that represents consent acquisition result.
         */
        @NotBlank
        private String result;

        /**
         * Returning from ASPSP-OAuth2 (base URL, ASPSP IDP should add code to it) to exchange code to token.
         */
        @NotNull
        private String fromOauth2WithCode;
    }

    @Data
    public static class Parameters {

        /**
         * Page with generic consent input form and other parameters like PSU ID, can be thought as landing page
         * for consent data input.
         */
        @NotBlank
        private String provideMore;

        /**
         * Page where the user provides his PIN or ASPSP password.
         */
        @NotBlank
        private String providePsuPassword;

        /**
         * Page where the user can select multiple SCA methods (SMS, email) for 2FA or multifactor authorization.
         */
        @NotBlank
        private String selectScaMethod;

        /**
         * Page where user reports SCA challenge result (i.e. SMS secret code from ASPSP)
         */
        @NotBlank
        private String reportScaResult;

        /**
         * Page where the user can provide IBAN list for dedicated consent.
         */
        @NotBlank
        private String providePsuIban;
    }
}
