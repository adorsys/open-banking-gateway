package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.RequestAction;
import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.SEE_OTHER;

@ExtendWith(MockitoExtension.class)
class RedirectHandlerServiceTest {
    private final String REDIRECT_STATE_VALUE = "682dbd06-75d4-4f73-a7e7-9084150a1f10";
    private final String REDIRECT_ID_VALUE = "fd8a0548-6862-46cb-8d24-f4b5edc7f7cb";
    private final String REDIRECT_CODE_VALUE = "7ca3f778-b0bb-4c1a-8003-d176089d1455";
    private final String REDIRECT_URL = "http://localhost:4444/{redirectUri}/{redirectCode}";
    private final String EXCEPTION_URL = "http://localhost:4444/excaption-redirect";

    private final String FULL_OK_URL = "http://localhost:4444/redirect-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10/7ca3f778-b0bb-4c1a-8003-d176089d1455";
    private final String FULL_NOT_OK_URL = "http://localhost:4444/redirect-no-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10/7ca3f778-b0bb-4c1a-8003-d176089d1455";

    private final String FINTECH_REDIRECT_OK = "redirect-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10";
    private final String FINTECH_REDIRECT_NOT_OK = "redirect-no-ok/to/682dbd06-75d4-4f73-a7e7-9084150a1f10";


    private final String LOCATION_HEADER = "Location";
    private final RedirectUrlsEntity REDIRECT_URLS_ENTITY = buildRedirectUrlsEntity();
    private final String OK_URI_VALUE = "http://localhost:5500/fintech-callback/redirect?fintechRedirectUriOk=" + REDIRECT_CODE_VALUE;

    @Mock
    private RedirectUrlRepository redirectUrlRepository;

    @Mock
    private AuthorizeService authorizeService;

    @Mock
    private SessionEntity sessionEntity;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private RequestInfoService requestInfoService;

    @Mock
    private RequestInfoEntity requestInfoEntity;

    @InjectMocks
    private RedirectHandlerService redirectHandlerService;

    @BeforeEach
    void setup() {
        redirectHandlerService = new RedirectHandlerService(redirectUrlRepository, authorizeService, accountService, transactionService, requestInfoService);
        redirectHandlerService.setRedirectUrl(REDIRECT_URL);
        redirectHandlerService.setExceptionUrl(EXCEPTION_URL);
    }

    @Test
    void registerRedirectUrlForSession() {
        //given
        when(redirectUrlRepository.save(any(RedirectUrlsEntity.class))).thenReturn(buildRedirectUrlsEntity());

        // when
        RedirectUrlsEntity redirectCode = redirectHandlerService.registerRedirectStateForSession(REDIRECT_STATE_VALUE, FINTECH_REDIRECT_OK, FINTECH_REDIRECT_NOT_OK);

        //then
        verify(redirectUrlRepository, times(1)).save(any(RedirectUrlsEntity.class));
        assertThat(redirectCode.getNotOkUrl()).isEqualTo(FULL_NOT_OK_URL);
        assertThat(redirectCode.getOkUrl()).isEqualTo(FULL_OK_URL);
        assertThat(redirectCode.getRedirectState()).isEqualTo(REDIRECT_STATE_VALUE);
        assertThat(redirectCode.getRedirectCode()).isEqualTo(REDIRECT_CODE_VALUE);
    }

    @Test
    void doRedirect_success() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));
        when(authorizeService.getByXsrfToken(REDIRECT_STATE_VALUE)).thenReturn(sessionEntity);
        when(authorizeService.isAuthorized(REDIRECT_STATE_VALUE, null)).thenReturn(true);
        when(requestInfoEntity.getRequestAction()).thenReturn(RequestAction.LIST_ACCOUNTS);
        when(requestInfoService.getRequestInfoByXsrfToken(REDIRECT_STATE_VALUE)).thenReturn(requestInfoEntity);
        when(accountService.listAccounts(any(ContextInformation.class), eq(sessionEntity), eq(REDIRECT_URLS_ENTITY), eq(requestInfoEntity))).thenReturn(buildResponseEntity());

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE);

        // then
        verify(authorizeService, times(1)).getByXsrfToken(REDIRECT_STATE_VALUE);

        assertThat(responseEntity.getStatusCode()).isEqualTo(ACCEPTED);
        assertThat(responseEntity.getHeaders().size()).isEqualTo(1);
        assertThat(responseEntity.getHeaders().get(LOCATION_HEADER)).isEqualTo(singletonList(OK_URI_VALUE));
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void doRedirect_redirectCodeIsEmpty() {
        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, "");

        // then
        verify(authorizeService, times(0)).getByXsrfToken(REDIRECT_STATE_VALUE);
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
        assertThrows(IllegalStateException.class, () -> redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE));
    }

    @Test
    void doRedirect_redirectStateIsEmpty() {
        // given
        when(redirectUrlRepository.findByRedirectCode(REDIRECT_CODE_VALUE)).thenReturn(Optional.of(REDIRECT_URLS_ENTITY));

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect("", REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE);

        // then
        verify(authorizeService, times(0)).getByXsrfToken(REDIRECT_STATE_VALUE);
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
        when(authorizeService.isAuthorized(REDIRECT_STATE_VALUE, null)).thenReturn(false);

        // when
        ResponseEntity responseEntity = redirectHandlerService.doRedirect(REDIRECT_STATE_VALUE, REDIRECT_ID_VALUE, REDIRECT_CODE_VALUE);

        // then
        verify(authorizeService, times(0)).getByXsrfToken(REDIRECT_STATE_VALUE);
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
        redirectUrlsEntity.setOkUrl(FULL_OK_URL);
        redirectUrlsEntity.setNotOkUrl(FULL_NOT_OK_URL);

        return redirectUrlsEntity;
    }

    private ResponseEntity buildResponseEntity() {
        return ResponseEntity.status(ACCEPTED)
                       .location(URI.create(OK_URI_VALUE))
                       .build();
    }
}