package de.adorsys.opba.protocol.hbci.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.hbci.config.ConfigConst.HBCI_PROTOCOL_CONFIG_PREFIX;

/**
 * HBCI URL protocol configuration. Note that all URLs are expanded using {@link de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil}, so
 * you can use string interpolation like this:
 * (redirect code query will be added automatically in ContextUtil.buildAndExpandQueryParameters(..)
 * http://localhost:8080/v1/consent/{sessionId}/fromAspsp/STUB_STATE/ok
 * The aforementioned URL will get interpolated using {@link de.adorsys.opba.protocol.hbci.context.HbciContext} functions to i.e.
 * http://localhost:8080/v1/consent/53659eaf-d953-48de-a644-61c3c915d2c6/fromAspsp/STUB_STATE/ok?redirectCode=0174ef07-2e33-4264-9646-3b8965d100a0
 * with data from {@link de.adorsys.opba.protocol.hbci.context.HbciContext} available at the step execution.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(HBCI_PROTOCOL_CONFIG_PREFIX + "urls")
public class HbciProtocolConfiguration {

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

    @Data
    public static class UrlSet {
        /**
         * Redirect links for UI screens - i.e. which screen to use for password input.
         */
        @NotNull
        private Redirect redirect;

        @Data
        public static class Redirect {

            /**
             * Consent related urls - Consent with IBANs input form, etc.
             */
            @NotNull
            private WebHooks webHooks;

            /**
             * Generic parameters input urls - i.e. password page.
             */
            @NotNull
            private Parameters parameters;

            /**
             * To ASPSP redirection page (for Redirect SCA).
             */
            @NotBlank
            private String toAspsp;

            @Data
            public static class WebHooks {

                /**
                 * URL that represents consent acquisition result.
                 */
                @NotBlank
                private String result;
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
    }
}
