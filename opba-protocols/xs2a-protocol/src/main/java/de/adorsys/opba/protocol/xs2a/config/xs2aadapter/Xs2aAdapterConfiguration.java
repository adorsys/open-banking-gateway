package de.adorsys.opba.protocol.xs2a.config.xs2aadapter;

import com.google.common.io.Resources;
import de.adorsys.xs2a.adapter.adapter.link.identity.IdentityLinksRewriter;
import de.adorsys.xs2a.adapter.http.ApacheHttpClientFactory;
import de.adorsys.xs2a.adapter.http.HttpClientFactory;
import de.adorsys.xs2a.adapter.mapper.PaymentInitiationScaStatusResponseMapper;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.AspspReadOnlyRepository;
import de.adorsys.xs2a.adapter.service.DownloadService;
import de.adorsys.xs2a.adapter.service.Oauth2Service;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.Pkcs12KeyStore;
import de.adorsys.xs2a.adapter.service.impl.AccountInformationServiceImpl;
import de.adorsys.xs2a.adapter.service.impl.DownloadServiceImpl;
import de.adorsys.xs2a.adapter.service.impl.PaymentInitiationServiceImpl;
import de.adorsys.xs2a.adapter.service.link.LinksRewriter;
import de.adorsys.xs2a.adapter.service.loader.AdapterDelegatingOauth2Service;
import de.adorsys.xs2a.adapter.service.loader.AdapterServiceLoader;
import de.adorsys.xs2a.adapter.service.loader.Psd2AdapterServiceLoader;
import lombok.SneakyThrows;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Paths;

/**
 * This is the embedded configuration of Adorsys-XS2A adapter (https://github.com/adorsys/xs2a-adapter) to serve requests to ASPSP.
 * Basically this adapter acts as RestClient with typed interfaces for us and other useful functionality.
 */
@Configuration
public class Xs2aAdapterConfiguration {

    @Value("${xs2a-adapter.loader.choose-first-from-multiple-aspsps:false}")
    private boolean chooseFirstFromMultipleAspsps;

    @Bean
    PaymentInitiationService xs2aPaymentInitiationService(AdapterServiceLoader adapterServiceLoader) {
        return new PaymentInitiationServiceImpl(adapterServiceLoader);
    }

    @Bean
    PaymentInitiationScaStatusResponseMapper xs2aPaymentInitiationScaStatusResponseMapper() {
        return new PaymentInitiationScaStatusResponseMapper();
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
                                                  LinksRewriter linksRewriter, Pkcs12KeyStore keyStore,
                                                  HttpClientFactory httpClientFactory) {
        return new Psd2AdapterServiceLoader(aspspRepository, keyStore, httpClientFactory, linksRewriter, linksRewriter, chooseFirstFromMultipleAspsps);
    }

    @Bean
    HttpClientFactory xs2aHttpClientFactory(HttpClientBuilder httpClientBuilder, Pkcs12KeyStore pkcs12KeyStore) {
        return new ApacheHttpClientFactory(httpClientBuilder, pkcs12KeyStore);
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

    /**
     * The keystore for QWAC and QSEAL certificates.
     * @param keystorePath Location of the keystore.
     * @param keystorePassword Keystore password.
     */
    @Bean
    @SneakyThrows
    Pkcs12KeyStore xs2aPkcs12KeyStore(
            @Value("${pkcs12.keyStore}") String keystorePath,
            @Value("${pkcs12.password}") char[] keystorePassword
    ) {
        return new Pkcs12KeyStore(
                Paths.get(keystorePath).toFile().exists()
                        ? Paths.get(keystorePath).toAbsolutePath().toString()
                        : Paths.get(Resources.getResource(keystorePath).toURI()).toAbsolutePath().toString(),
                keystorePassword
        );
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
}
