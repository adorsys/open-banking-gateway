package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.utils.OkOrNotOk;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
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
    private final String AUTH_ID_VALUE = "fd8a0548-6862-46cb-8d24-f4b5edc7f7cb";
    private final String REDIRECT_CODE_VALUE = "7ca3f778-b0bb-4c1a-8003-d176089d1455";
    private final String UNAUTH_URL = "http://unauthorized";

    private final String FINTECH_REDIRECT_OK = "redirect-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10";
    private final String FINTECH_REDIRECT_NOT_OK = "redirect-no-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10";


    private final String LOCATION_HEADER = "Location";
    private final RedirectUrlsEntity REDIRECT_URLS_ENTITY = buildRedirectUrlsEntity();

    @InjectMocks
    @MockBean(reset = MockReset.NONE, answer = Answers.CALLS_REAL_METHODS)
    private RestRequestContext restRequestContext;

    @Mock
    private FintechUiConfig uiConfig;

    @Mock
    private RedirectUrlRepository redirectUrlRepository;

    @Mock
    private SessionLogicService sessionLogicService;

    private RedirectHandlerService redirectHandlerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        log.info("setup RestRequestContext");
        restRequestContext.setRequestId(UUID.randomUUID().toString());
        redirectHandlerService = new RedirectHandlerService(uiConfig, redirectUrlRepository, sessionLogicService);
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
        assertThat(redirectCode.getRedirectCode()).isEqualTo(REDIRECT_CODE_VALUE);
    }

    @Test
    void doRedirect_success() {
        // given
        when(sessionLogicService.finishRedirect()).thenReturn(new HttpHeaders());
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(AUTH_ID_VALUE, REDIRECT_CODE_VALUE, OkOrNotOk.OK);

        // then
        verify(sessionLogicService, times(1)).finishRedirect();

        assertThat(responseEntity.getStatusCode()).isEqualTo(ACCEPTED);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(FINTECH_REDIRECT_OK));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectCodeIsEmpty() {
        // given
        when(uiConfig.getUnauthorizedUrl()).thenReturn(UNAUTH_URL);
        when(sessionLogicService.finishRedirect()).thenReturn(new HttpHeaders());

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(AUTH_ID_VALUE, REDIRECT_CODE_VALUE, OkOrNotOk.OK);

        // then
        verify(sessionLogicService).finishRedirect();

        assertThat(responseEntity.getStatusCode()).isEqualTo(SEE_OTHER);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(UNAUTH_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_notOk() {
        // given
        when(uiConfig.getUnauthorizedUrl()).thenReturn(UNAUTH_URL);
        when(sessionLogicService.finishRedirect()).thenReturn(new HttpHeaders());

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(null, REDIRECT_CODE_VALUE, OkOrNotOk.NOT_OK);

        // then
        verify(sessionLogicService).finishRedirect();

        assertThat(responseEntity.getStatusCode()).isEqualTo(SEE_OTHER);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        log.info("header {}", responseEntity.getHeaders().get(LOCATION_HEADER));
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(UNAUTH_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectCodeIsWrong() {
        // given
        when(uiConfig.getUnauthorizedUrl()).thenReturn(UNAUTH_URL);
        when(sessionLogicService.finishRedirect()).thenReturn(new HttpHeaders());
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.empty());

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(AUTH_ID_VALUE, REDIRECT_CODE_VALUE, OkOrNotOk.OK);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(SEE_OTHER);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(UNAUTH_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    private RedirectUrlsEntity buildRedirectUrlsEntity() {
        RedirectUrlsEntity redirectUrlsEntity = new RedirectUrlsEntity();
        redirectUrlsEntity.setRedirectCode(REDIRECT_CODE_VALUE);
        redirectUrlsEntity.setOkStatePath(FINTECH_REDIRECT_OK);
        redirectUrlsEntity.setNokStatePath(FINTECH_REDIRECT_NOT_OK);

        return redirectUrlsEntity;
    }
}
