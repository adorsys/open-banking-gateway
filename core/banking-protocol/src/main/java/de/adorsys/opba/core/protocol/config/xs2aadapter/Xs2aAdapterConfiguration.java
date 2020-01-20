package de.adorsys.opba.core.protocol.config.xs2aadapter;

import com.google.common.io.Resources;
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
import de.adorsys.xs2a.adapter.service.loader.AdapterDelegatingOauth2Service;
import de.adorsys.xs2a.adapter.service.loader.AdapterServiceLoader;
import lombok.SneakyThrows;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Paths;

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
    AdapterServiceLoader xs2aadapterServiceLoader(AspspReadOnlyRepository aspspRepository,
                                                  Pkcs12KeyStore keyStore, HttpClientFactory httpClientFactory) {
        return new AdapterServiceLoader(aspspRepository, keyStore, httpClientFactory, chooseFirstFromMultipleAspsps);
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

    @Bean
    DownloadService xs2aDownloadService(AdapterServiceLoader adapterServiceLoader) {
        return new DownloadServiceImpl(adapterServiceLoader);
    }

    private static HttpClientBuilder xs2aHttpClientBuilderWithSharedConfiguration() {
        return HttpClientBuilder.create()
                .disableDefaultUserAgent();
    }
}
