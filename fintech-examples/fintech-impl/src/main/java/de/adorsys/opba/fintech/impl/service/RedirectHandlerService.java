package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import de.adorsys.opba.fintech.impl.service.mocks.TppBankingApiTokenMock;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static java.util.Collections.singletonList;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
@ConfigurationProperties("fintech-ui")
public class RedirectHandlerService {
    private static final String LOCATION_HEADER = "Location";
    private String notOkUrl;
    private String okUrl;
    private String exceptionUrl;

    private final RedirectUrlRepository redirectUrlRepository;
    private final AuthorizeService authorizeService;

    public RedirectUrlsEntity registerRedirectUrlForSession(String xsrfToken) {
        String redirectCode = UUID.randomUUID().toString();
        log.debug("ONLY FOR DEBUG: redirectCode: {}", redirectCode);

        RedirectUrlsEntity redirectUrls = new RedirectUrlsEntity();

        redirectUrls.setRedirectCode(redirectCode);
        redirectUrls.setRedirectState(xsrfToken);
        redirectUrls.setNotOkURL(getModifiedUrlWithRedirectCode(notOkUrl, redirectCode));
        redirectUrls.setOkURL(getModifiedUrlWithRedirectCode(okUrl, redirectCode));

        return redirectUrlRepository.save(redirectUrls);
    }

    public ResponseEntity doRedirect(String redirectState, String redirectId, String redirectCode) {
        if (StringUtils.isBlank(redirectCode)) {
            log.warn("Validation redirect request was failed: redirect code is empty!");
            return prepareRedirectResponse(exceptionUrl);
        }

        RedirectUrlsEntity redirectUrls = redirectUrlRepository.findByRedirectCode(redirectCode)
                                                  .orElseThrow(() -> new IllegalStateException("Validation redirect request was failed: redirect code is wrong!"));

        if (StringUtils.isBlank(redirectState)) {
            log.warn("Validation redirect request was failed: Xsrf Token is empty!");
            return prepareRedirectResponse(redirectUrls.getNotOkURL());
        }

        if (!authorizeService.isAuthorized(redirectState, null)) {
            log.warn("Validation redirect request was failed: Xsrf Token is wrong or user are not authorized!");
            return prepareRedirectResponse(redirectUrls.getNotOkURL());
        }

        SessionEntity optionalUser = authorizeService.getByXsrfToken(redirectState);
        updateSessionByRedirectCode(optionalUser, redirectCode);

        return prepareRedirectResponse(redirectUrls.getOkURL());
    }

    private void updateSessionByRedirectCode(SessionEntity sessionEntity, String redirectCode) {
        PsuConsentSessionResponse psuConsentSessionResponse = new TppBankingApiTokenMock().getTransactionsResponse(redirectCode);
        sessionEntity.setPsuConsentSession(psuConsentSessionResponse.getPsuConsentSession().toString());
        authorizeService.updateUserSession(sessionEntity);
    }

    private ResponseEntity prepareRedirectResponse(String redirectUrl) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(LOCATION_HEADER, singletonList(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private String getModifiedUrlWithRedirectCode(String url, String redirectCode) {
        return UriComponentsBuilder.fromPath(url)
                       .buildAndExpand(redirectCode)
                       .toUriString();
    }
}
