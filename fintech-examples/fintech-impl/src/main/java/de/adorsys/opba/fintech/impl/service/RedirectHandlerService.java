package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class RedirectHandlerService {
    private static final String LOCATION_HEADER = "Location";

    private final FintechUiConfig uiConfig;
    private final RedirectUrlRepository redirectUrlRepository;
    private final AuthorizeService authorizeService;

    @Transactional
    public RedirectUrlsEntity registerRedirectStateForSession(String xsrfToken, String okPath, String nokPath) {
        String redirectCode = UUID.randomUUID().toString();
        log.debug("ONLY FOR DEBUG: redirectCode: {}", redirectCode);

        String localOkPath = okPath.replaceAll("^/", "");
        String localNokPath = nokPath.replaceAll("^/", "");

        RedirectUrlsEntity redirectUrls = new RedirectUrlsEntity();

        redirectUrls.setRedirectCode(redirectCode);
        redirectUrls.setRedirectState(xsrfToken);
        redirectUrls.setOkStatePath(localOkPath);
        redirectUrls.setNokStatePath(localNokPath);

        return redirectUrlRepository.save(redirectUrls);
    }

    @Transactional
    public ResponseEntity doRedirect(String redirectState, String authId, String redirectCode) {
        if (StringUtils.isBlank(redirectCode)) {
            log.warn("Validation redirect request was failed: redirect code is empty!");
            return prepareErrorRedirectResponse(uiConfig.getExceptionUrl());
        }

        Optional<RedirectUrlsEntity> redirectUrls = redirectUrlRepository.findByRedirectCode(redirectCode);

        if (!redirectUrls.isPresent()) {
            log.warn("Validation redirect request was failed: redirect code is wrong");
            return prepareErrorRedirectResponse(uiConfig.getUnauthorizedUrl());
        }

        if (StringUtils.isBlank(authId)) {
            log.warn("Validation redirect request was failed: authId is empty!");
            return prepareErrorRedirectResponse(uiConfig.getUnauthorizedUrl());
        }

        if (StringUtils.isBlank(redirectState)) {
            log.warn("Validation redirect request was failed: Xsrf Token is empty!");
            return prepareErrorRedirectResponse(uiConfig.getUnauthorizedUrl());
        }

        if (!authorizeService.isAuthorized()) {
            log.warn("Validation redirect request was failed: Xsrf Token is wrong or user are not authorized!");
            return prepareErrorRedirectResponse(uiConfig.getUnauthorizedUrl());
        }

        ContextInformation contextInformation = new ContextInformation();
        SessionEntity sessionEntity = authorizeService.getByXsrfToken(redirectState);
        redirectUrlRepository.delete(redirectUrls.get());

        return prepareRedirectToReadResultResponse(contextInformation, sessionEntity, redirectUrls.get());
    }

    private ResponseEntity prepareRedirectToReadResultResponse(
            ContextInformation contextInformation, SessionEntity sessionEntity, RedirectUrlsEntity redirectUrls
    ) {
        HttpHeaders authHeaders = authorizeService.fillWithAuthorizationHeaders(contextInformation, sessionEntity);
        authHeaders.put(LOCATION_HEADER, singletonList(redirectUrls.getOkStatePath()));
        return new ResponseEntity<>(authHeaders, HttpStatus.ACCEPTED);
    }

    private ResponseEntity prepareErrorRedirectResponse(String redirectUrl) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(LOCATION_HEADER, singletonList(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }
}
