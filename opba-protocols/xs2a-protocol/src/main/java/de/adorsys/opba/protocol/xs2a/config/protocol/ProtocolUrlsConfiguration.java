package de.adorsys.opba.protocol.xs2a.config.protocol;

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
    private Ais ais;

    /**
     * Payment related urls .
     */
    @NotNull
    private Pis pis;

    /**
     * Generic parameters input urls - i.e. password page.
     */
    @NotNull
    private ProtocolUrlsConfiguration.CommonUrls commonUrls;

    @Data
    public static class Ais {
        /**
         * To ASPSP redirection page (for Redirect SCA).
         */
        @NotBlank
        private String toAspsp;

        /**
         * URL that represents consent acquisition result.
         */
        @NotBlank
        private String result;

        /**
         * Page where the user can provide IBAN list for dedicated consent.
         */
        @NotBlank
        private String providePsuIban;
    }

    @Data
    public static class Pis {

        /**
         * To ASPSP redirection page (for Redirect SCA).
         */
        @NotBlank
        private String toAspsp;

        /**
         * URL that represents payment acquisition result.
         */
        @NotBlank
        private String result;
    }

    @Data
    public static class CommonUrls {
        /**
         * URL that represents page saying that payment creation was OK (comes before payment result page).
         */
        @NotBlank
        private String ok;

        /**
         * URL that represents page saying that payment creation was not OK (comes before payment result page).
         */
        @NotBlank
        private String nok;

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
    }
}
