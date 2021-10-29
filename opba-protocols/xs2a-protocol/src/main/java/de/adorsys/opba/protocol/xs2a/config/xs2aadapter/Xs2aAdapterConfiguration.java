package de.adorsys.opba.protocol.xs2a.config.xs2aadapter;

import com.google.common.io.Resources;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.AspspReadOnlyRepository;
import de.adorsys.xs2a.adapter.api.DownloadService;
import de.adorsys.xs2a.adapter.api.Oauth2Service;
import de.adorsys.xs2a.adapter.api.PaymentInitiationService;
import de.adorsys.xs2a.adapter.api.Pkcs12KeyStore;
import de.adorsys.xs2a.adapter.api.http.HttpClientConfig;
import de.adorsys.xs2a.adapter.api.http.HttpClientFactory;
import de.adorsys.xs2a.adapter.api.http.HttpLogSanitizer;
import de.adorsys.xs2a.adapter.api.link.LinksRewriter;
import de.adorsys.xs2a.adapter.impl.http.ApacheHttpClientFactory;
import de.adorsys.xs2a.adapter.impl.http.BaseHttpClientConfig;
import de.adorsys.xs2a.adapter.impl.http.Xs2aHttpLogSanitizer;
import de.adorsys.xs2a.adapter.impl.link.identity.IdentityLinksRewriter;
import de.adorsys.xs2a.adapter.serviceloader.AccountInformationServiceImpl;
import de.adorsys.xs2a.adapter.serviceloader.AdapterDelegatingOauth2Service;
import de.adorsys.xs2a.adapter.serviceloader.AdapterServiceLoader;
import de.adorsys.xs2a.adapter.serviceloader.DownloadServiceImpl;
import de.adorsys.xs2a.adapter.serviceloader.EmbeddedPreAuthorisationServiceImpl;
import de.adorsys.xs2a.adapter.serviceloader.PaymentInitiationServiceImpl;
import lombok.SneakyThrows;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Paths;
import java.util.List;

import static de.adorsys.opba.protocol.xs2a.config.ConfigConst.XS2A_PROTOCOL_CONFIG_PREFIX;

/**
 * This is the embedded configuration of Adorsys-XS2A adapter (https://github.com/adorsys/xs2a-adapter) to serve requests to ASPSP.
 * Basically this adapter acts as RestClient with typed interfaces for us and other useful functionality.
 */
@Configuration
public class Xs2aAdapterConfiguration {

    @Value("${" + XS2A_PROTOCOL_CONFIG_PREFIX + "xs2a-adapter.loader.choose-first-from-multiple-aspsps:false}")
    private boolean chooseFirstFromMultipleAspsps;

    @Value("${" + XS2A_PROTOCOL_CONFIG_PREFIX + "xs2a-adapter.sanitizer.whitelist}")
    private List<String> sanitizerWhitelist;

    @Bean
    PaymentInitiationService xs2aPaymentInitiationService(AdapterServiceLoader adapterServiceLoader) {
        return new PaymentInitiationServiceImpl(adapterServiceLoader);
    }

    @Bean
    AccountInformationService xs2aaccountInformationService(AdapterServiceLoader adapterServiceLoader) {
        return new AccountInformationServiceImpl(adapterServiceLoader);
    }

    @Bean
    LinksRewriter xs2aLinksRewriter() {
        return new IdentityLinksRewriter();
    }

    @Bean
    AdapterServiceLoader xs2aadapterServiceLoader(AspspReadOnlyRepository aspspRepository,
                                                  LinksRewriter linksRewriter,
                                                  HttpClientFactory httpClientFactory) {
        return new AdapterServiceLoader(aspspRepository, httpClientFactory, linksRewriter, linksRewriter, chooseFirstFromMultipleAspsps);
    }

    @Bean
    HttpClientFactory xs2aHttpClientFactory(HttpClientBuilder httpClientBuilder, HttpClientConfig httpClientConfig) {
        return new ApacheHttpClientFactory(httpClientBuilder, httpClientConfig);
    }

    @Bean
    HttpClientConfig httpClientConfig(Pkcs12KeyStore keyStore, HttpLogSanitizer httpLogSanitizer) {
        return new BaseHttpClientConfig(httpLogSanitizer, keyStore, null);
    }

    @Bean
    @Profile("!dev")
    HttpClientBuilder xs2aHttpClientBuilder() {
        return xs2aHttpClientBuilderWithSharedConfiguration();
    }

    @Bean
    @Profile("dev")
    HttpClientBuilder xs2aHttpClientBuilderWithDisabledCompression() {
        return xs2aHttpClientBuilderWithSharedConfiguration()
                .disableContentCompression();
    }

    @Bean
    Oauth2Service xs2aOauth2Service(AdapterServiceLoader adapterServiceLoader) {
        return new AdapterDelegatingOauth2Service(adapterServiceLoader);
    }
    @Bean
    EmbeddedPreAuthorisationServiceImpl embeddedPreAuthorisationServiceImpl(AdapterServiceLoader adapterServiceLoader) {
        return new EmbeddedPreAuthorisationServiceImpl(adapterServiceLoader);
    }

    /**
     * The keystore for QWAC and QSEAL certificates.
     * @param keystorePath Location of the keystore.
     * @param keystorePassword Keystore password.
     */
    @Bean
    @SneakyThrows
    Pkcs12KeyStore xs2aPkcs12KeyStore(
            @Value("${" + XS2A_PROTOCOL_CONFIG_PREFIX + "pkcs12.keystore}") String keystorePath,
            @Value("${" + XS2A_PROTOCOL_CONFIG_PREFIX + "pkcs12.password}") char[] keystorePassword
    ) {
        if (Paths.get(keystorePath).toFile().exists()) {
            return new Pkcs12KeyStore(Paths.get(keystorePath).toAbsolutePath().toString(), keystorePassword);
        }

        try (var is = Resources.getResource(keystorePath).openStream()) {
            return new Pkcs12KeyStore(is, keystorePassword, "default_qwac", "default_qseal");
        }
    }

    /**
     * Service to download large reports (i.e. large transaction list).
     */
    @Bean
    DownloadService xs2aDownloadService(AdapterServiceLoader adapterServiceLoader) {
        return new DownloadServiceImpl(adapterServiceLoader);
    }

    private static HttpClientBuilder xs2aHttpClientBuilderWithSharedConfiguration() {
        return HttpClientBuilder.create()
                .disableDefaultUserAgent();
    }

    @Bean
    HttpLogSanitizer xs2aHttpLogSanitizer() {
        return new Xs2aHttpLogSanitizer(sanitizerWhitelist);
    }
}
