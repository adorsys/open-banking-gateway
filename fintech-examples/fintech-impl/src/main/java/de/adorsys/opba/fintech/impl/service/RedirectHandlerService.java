package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.utils.OkOrNotOk;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Collections.singletonList;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectHandlerService {
    private static final String LOCATION_HEADER = "Location";
    private final FintechUiConfig uiConfig;
    private final RedirectUrlRepository redirectUrlRepository;
    private final SessionLogicService sessionLogicService;

    @Transactional
    public RedirectUrlsEntity registerRedirectStateForSession(final String finTechRedirectCode, final String okPath, final String nokPath) {
        String localOkPath = okPath.replaceAll("^/", "");
        String localNokPath = nokPath.replaceAll("^/", "");

        RedirectUrlsEntity redirectUrls = new RedirectUrlsEntity();

        redirectUrls.setRedirectCode(finTechRedirectCode);
        redirectUrls.setOkStatePath(localOkPath);
        redirectUrls.setNokStatePath(localNokPath);

        return redirectUrlRepository.save(redirectUrls);
    }

    @Transactional
    public ResponseEntity doRedirect(final String authId, final String redirectCode, final OkOrNotOk okOrNotOk) {
        if (StringUtils.isBlank(redirectCode)) {
            log.warn("Validation redirect request failed: redirect code is empty!");
            return prepareErrorRedirectResponse(uiConfig.getExceptionUrl());
        }
        Optional<RedirectUrlsEntity> redirectUrls = redirectUrlRepository.findByRedirectCode(redirectCode);

        if (!redirectUrls.isPresent()) {
            log.warn("Validation redirect request failed: redirect code {} is wrong", redirectCode);
            return prepareErrorRedirectResponse(uiConfig.getUnauthorizedUrl());
        }
        redirectUrlRepository.delete(redirectUrls.get());
        // if (!sessionLogicService.isRedirectAuthorized()) has been tested before

        if (okOrNotOk.equals(OkOrNotOk.OK)) {
            if (StringUtils.isBlank(authId)) {
                log.warn("Validation redirect request failed: authId is empty!");
                return prepareErrorRedirectResponse(uiConfig.getUnauthorizedUrl());
            }

            return prepareRedirectToReadResultResponse(redirectUrls.get().getOkStatePath());
        }
        log.info("user aborted consent authorization for authid {}", authId);
        return prepareRedirectToReadResultResponse(redirectUrls.get().getNokStatePath());
    }

    private ResponseEntity prepareRedirectToReadResultResponse(String redirectUrl) {
        HttpHeaders headers = sessionLogicService.finishRedirect();
        headers.put(LOCATION_HEADER, singletonList(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }

    private ResponseEntity prepareErrorRedirectResponse(String redirectUrl) {
        HttpHeaders headers = sessionLogicService.finishRedirect();
        headers.put(LOCATION_HEADER, singletonList(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }
}
