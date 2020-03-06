package de.adorsys.opba.protocol.xs2a.config.xs2aadapter;

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
import de.adorsys.xs2a.adapter.service.provider.AdapterServiceProvider;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
        return new ThreadSafeAdapterServiceLoader(aspspRepository, keyStore, httpClientFactory, chooseFirstFromMultipleAspsps);
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

    // FIXME - remove it when https://github.com/adorsys/xs2a-adapter/issues/369 is done
    static class ThreadSafeAdapterServiceLoader extends AdapterServiceLoader {

        private final Object lock = new Object();
        private final Map<ServiceKey, Optional<?>> providers = new ConcurrentHashMap<>();

        ThreadSafeAdapterServiceLoader(
                AspspReadOnlyRepository aspspRepository,
                Pkcs12KeyStore keyStore,
                HttpClientFactory httpClientFactory,
                boolean chooseFirstFromMultipleAspsps
        ) {
            super(aspspRepository, keyStore, httpClientFactory, chooseFirstFromMultipleAspsps);
        }

        @Override
        public <T extends AdapterServiceProvider> Optional<T> getServiceProvider(Class<T> klass, String adapterId) {
            return (Optional<T>) providers.computeIfAbsent(new ServiceKey(klass, adapterId), id -> {
                synchronized (lock) {
                    return super.getServiceProvider(klass, adapterId);
                }
            });
        }

        @Getter
        @EqualsAndHashCode
        @AllArgsConstructor
        private static class ServiceKey {
            private Class clazz;
            private String adapterId;
        }
    }
}
