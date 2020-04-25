package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.tppclients.SessionCookieType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectHandlerService {
    private static final String LOCATION_HEADER = "Location";
    private final FintechUiConfig uiConfig;
    private final RedirectUrlRepository redirectUrlRepository;
    private final AuthorizeService authorizeService;
    private final RestRequestContext restRequestContext;
    private final CookieConfigProperties cookieConfigProperties;

    @Transactional
    public RedirectUrlsEntity registerRedirectStateForSession(final String finTechRedirectCode, final String okPath, final String nokPath) {
        log.debug("ONLY FOR DEBUG: finTechRedirectCode: {}", finTechRedirectCode);

        String localOkPath = okPath.replaceAll("^/", "");
        String localNokPath = nokPath.replaceAll("^/", "");

        RedirectUrlsEntity redirectUrls = new RedirectUrlsEntity();

        redirectUrls.setRedirectCode(finTechRedirectCode);
        redirectUrls.setOkStatePath(localOkPath);
        redirectUrls.setNokStatePath(localNokPath);

        return redirectUrlRepository.save(redirectUrls);
    }

    @Transactional
    public ResponseEntity doRedirect(final String uiGivenAuthId, final String redirectCode, final String okOrNotOk) {
        // vulnerable for DOS
        SessionEntity sessionEntity = authorizeService.getSession();
        if (StringUtils.isBlank(redirectCode)) {
            log.warn("Validation redirect request failed: redirect code is empty!");
            return prepareErrorRedirectResponse(sessionEntity, uiConfig.getExceptionUrl());
        }
        Optional<RedirectUrlsEntity> redirectUrls = redirectUrlRepository.findByRedirectCode(redirectCode);

        if (!redirectUrls.isPresent()) {
            log.warn("Validation redirect request failed: redirect code {} is wrong", redirectCode);
            return prepareErrorRedirectResponse(sessionEntity, uiConfig.getUnauthorizedUrl());
        }
        redirectUrlRepository.delete(redirectUrls.get());

        if (!authorizeService.isAuthorized()) {
            log.warn("Validation redirect request failed: user is not authorized!");
            return prepareErrorRedirectResponse(sessionEntity, uiConfig.getUnauthorizedUrl());
        }

        if ("ok".equalsIgnoreCase(okOrNotOk)) {
            if (StringUtils.isBlank(uiGivenAuthId)) {
                log.warn("Validation redirect request failed: authId is empty!");
                return prepareErrorRedirectResponse(sessionEntity, uiConfig.getUnauthorizedUrl());
            }

            String storedAuthId = sessionEntity.getAuthId();

            if (!uiGivenAuthId.equals(storedAuthId)) {
                log.warn("Validation redirect request failed: authid expected was {}, but authid from ui was {}", storedAuthId, uiGivenAuthId);
                return prepareErrorRedirectResponse(sessionEntity, uiConfig.getUnauthorizedUrl());
            }

            log.info("authId {}", uiGivenAuthId);
            sessionEntity.setConsentConfirmed(true);
            return prepareRedirectToReadResultResponse(sessionEntity, redirectUrls.get().getOkStatePath());
        }
        log.info("user aborted consent authorization for authid {}", uiGivenAuthId);
        return prepareRedirectToReadResultResponse(sessionEntity, redirectUrls.get().getNokStatePath());
    }

    private ResponseEntity prepareRedirectToReadResultResponse(SessionEntity sessionEntity, String redirectUrl) {
        String xsrfToken = UUID.randomUUID().toString();
        HttpHeaders authHeaders = authorizeService.modifySessionEntityAndCreateNewAuthHeader(restRequestContext.getRequestId(), sessionEntity,
                xsrfToken, cookieConfigProperties, SessionCookieType.REGULAR);
        authHeaders.put(LOCATION_HEADER, singletonList(redirectUrl));
        return new ResponseEntity<>(authHeaders, HttpStatus.ACCEPTED);
    }

    private ResponseEntity prepareErrorRedirectResponse(SessionEntity sessionEntity, String redirectUrl) {
        String xsrfToken = UUID.randomUUID().toString();
        HttpHeaders headers = authorizeService.modifySessionEntityAndCreateNewAuthHeader(restRequestContext.getRequestId(), sessionEntity,
                xsrfToken, cookieConfigProperties, SessionCookieType.REGULAR);
        headers.put(LOCATION_HEADER, singletonList(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }
}
