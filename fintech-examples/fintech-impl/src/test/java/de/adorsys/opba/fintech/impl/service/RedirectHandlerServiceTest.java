package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.SEE_OTHER;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RedirectHandlerServiceTest {
    private final String REDIRECT_STATE_VALUE = "682dbd06-75d4-4f73-a7e7-9084150a1f10";
    private final String REDIRECT_ID_VALUE = "fd8a0548-6862-46cb-8d24-f4b5edc7f7cb";
    private final String REDIRECT_CODE_VALUE = "7ca3f778-b0bb-4c1a-8003-d176089d1455";
    private final String REDIRECT_URL = "http://localhost:4444/{redirectUri}/{redirectCode}";
    private final String EXCEPTION_URL = "http://localhost:4444/excaption-redirect";
    private final String FULL_NOT_OK_URL = "http://localhost:4444/excaption-redirect";

    private final String FINTECH_REDIRECT_OK = "redirect-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10";
    private final String FINTECH_REDIRECT_NOT_OK = "redirect-no-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10";


    private final String LOCATION_HEADER = "Location";
    private final RedirectUrlsEntity REDIRECT_URLS_ENTITY = buildRedirectUrlsEntity();

    private FintechUiConfig uiConfig = new FintechUiConfig(REDIRECT_URL, EXCEPTION_URL, EXCEPTION_URL);

    @InjectMocks
    @MockBean(reset = MockReset.NONE, answer = Answers.CALLS_REAL_METHODS)
    private RestRequestContext restRequestContext;

    @Mock
    private RedirectUrlRepository redirectUrlRepository;

    @Mock
    private AuthorizeService authorizeService;

    @Mock
    private SessionEntity sessionEntity;

    @InjectMocks
    private RedirectHandlerService redirectHandlerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        log.info("setup RestRequestContext");
        restRequestContext.setRequestId(UUID.randomUUID().toString());
        redirectHandlerService = new RedirectHandlerService(uiConfig, redirectUrlRepository, authorizeService, restRequestContext);
    }

    @Test
    void registerRedirectUrlForSession() {
        //given
        when(redirectUrlRepository.save(any(RedirectUrlsEntity.class))).thenReturn(buildRedirectUrlsEntity());

        // when
        RedirectUrlsEntity redirectCode = redirectHandlerService.registerRedirectStateForSession(REDIRECT_STATE_VALUE, FINTECH_REDIRECT_OK, FINTECH_REDIRECT_NOT_OK);

        //then
        verify(redirectUrlRepository, times(1)).save(any(RedirectUrlsEntity.class));
        assertThat(redirectCode.getNokStatePath()).isEqualTo(FINTECH_REDIRECT_NOT_OK);
        assertThat(redirectCode.getOkStatePath()).isEqualTo(FINTECH_REDIRECT_OK);
        assertThat(redirectCode.getRedirectState()).isEqualTo(REDIRECT_STATE_VALUE);
        assertThat(redirectCode.getRedirectCode()).isEqualTo(REDIRECT_CODE_VALUE);
    }

    @Test
    void doRedirect_success() {
        // given
        when(authorizeService.fillWithAuthorizationHeaders(sessionEntity, restRequestContext.getXsrfTokenHeaderField()))
                .thenReturn(new HttpHeaders());
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));
        when(authorizeService.getSession()).thenReturn(sessionEntity);
        when(authorizeService.isAuthorized()).thenReturn(true);

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE);

        // then
        verify(authorizeService, times(1)).getSession();

        assertThat(responseEntity.getStatusCode()).isEqualTo(ACCEPTED);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(FINTECH_REDIRECT_OK));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectCodeIsEmpty() {
        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, "");

        // then
        verify(authorizeService, times(0)).getSession();
        verify(authorizeService, times(0)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(SEE_OTHER);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(EXCEPTION_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectCodeIsWrong() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.empty());

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(SEE_OTHER);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(FULL_NOT_OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectStateIsEmpty() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect("", REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE);

        // then
        verify(authorizeService, times(0)).getSession();
        verify(authorizeService, times(0)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(SEE_OTHER);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(FULL_NOT_OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectStateIsWrong() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));
        // when(authorizeService.isAuthorized(REDIRECT_STATE_VALUE, null)).thenReturn(false);

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE);

        // then
        verify(authorizeService, times(0)).getSession();
        verify(authorizeService, times(0)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(SEE_OTHER);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(FULL_NOT_OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    private RedirectUrlsEntity buildRedirectUrlsEntity() {
        RedirectUrlsEntity redirectUrlsEntity = new RedirectUrlsEntity();
        redirectUrlsEntity.setRedirectCode(REDIRECT_CODE_VALUE);
        redirectUrlsEntity.setRedirectState(REDIRECT_STATE_VALUE);
        redirectUrlsEntity.setOkStatePath(FINTECH_REDIRECT_OK);
        redirectUrlsEntity.setNokStatePath(FINTECH_REDIRECT_NOT_OK);

        return redirectUrlsEntity;
    }
}
