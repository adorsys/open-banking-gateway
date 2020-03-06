package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedirectHandlerServiceTest {

    private final String REDIRECT_STATE = "682dbd06-75d4-4f73-a7e7-9084150a1f10";
    private final String REDIRECT_ID = "fd8a0548-6862-46cb-8d24-f4b5edc7f7cb";
    private final String REDIRECT_CODE = "7ca3f778-b0bb-4c1a-8003-d176089d1455";
    private final String OK_URL = "http://localhost:5500/fintech-callback/ok";
    private final String NOT_OK_URL = "http://localhost:5500/fintech-callback/nok";
    private final String LOCATION_HEADER = "Location";

    private final RedirectUrlsEntity REDIRECT_URLS_ENTITY = buildRedirectUrlsEntity();

    @InjectMocks
    private RedirectHandlerService redirectHandlerService;

    @Mock
    private RedirectUrlRepository redirectUrlRepository;

    @Mock
    private AuthorizeService authorizeService;

    @Mock
    private SessionEntity sessionEntity;

    @Test
    void registerRedirectUrlForSession() {
        // when
        String redirectCode = redirectHandlerService.registerRedirectUrlForSession(REDIRECT_STATE, OK_URL, NOT_OK_URL);

        //then
        verify(redirectUrlRepository, times(1)).save(any(RedirectUrlsEntity.class));
        assertThat(StringUtils.isNotBlank(redirectCode)).isTrue();
    }

    @Test
    void doRedirect_success() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));
        when(authorizeService.getByXsrfToken(REDIRECT_STATE)).thenReturn(sessionEntity);
        when(authorizeService.isAuthorized(REDIRECT_STATE, null)).thenReturn(true);

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE, REDIRECT_ID, REDIRECT_CODE);

        // then
        verify(authorizeService, times(1)).getByXsrfToken(REDIRECT_STATE);
        verify(authorizeService, times(1)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectCodeIsEmpty() {
        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE, REDIRECT_ID, "");

        // then
        verify(authorizeService, times(0)).getByXsrfToken(REDIRECT_STATE);
        verify(authorizeService, times(0)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(NOT_OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectCodeIsWrong() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE)).thenReturn(Optional.empty());

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE, REDIRECT_ID, REDIRECT_CODE);

        // then
        verify(authorizeService, times(0)).getByXsrfToken(REDIRECT_STATE);
        verify(authorizeService, times(0)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(NOT_OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectStateIsEmpty() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect("", REDIRECT_ID, REDIRECT_CODE);

        // then
        verify(authorizeService, times(0)).getByXsrfToken(REDIRECT_STATE);
        verify(authorizeService, times(0)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(NOT_OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectStateIsWrong() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));
        when(authorizeService.isAuthorized(REDIRECT_STATE, null)).thenReturn(false);

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE, REDIRECT_ID, REDIRECT_CODE);

        // then
        verify(authorizeService, times(0)).getByXsrfToken(REDIRECT_STATE);
        verify(authorizeService, times(0)).updateUserSession(sessionEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(NOT_OK_URL));
        assertThat(responseEntity.getBody()).isNull();
    }

    private RedirectUrlsEntity buildRedirectUrlsEntity() {
        RedirectUrlsEntity redirectUrlsEntity = new RedirectUrlsEntity();
        redirectUrlsEntity.setRedirectCode(REDIRECT_CODE);
        redirectUrlsEntity.setRedirectState(REDIRECT_STATE);
        redirectUrlsEntity.setOkURL(OK_URL);
        redirectUrlsEntity.setNotOkURL(NOT_OK_URL);

        return redirectUrlsEntity;
    }
}