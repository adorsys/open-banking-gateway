package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import de.adorsys.opba.fintech.impl.service.mocks.TppBankingApiTokenMock;
import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectHandlerService {

    private final RedirectUrlRepository redirectUrlRepository;
    private final AuthorizeService authorizeService;

    public String registerRedirectUrlForSession(String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK) {
        String redirectCode = UUID.randomUUID().toString();
        log.debug("ONLY FOR DEBUG: redirectCode: {}", redirectCode);

        RedirectUrlsEntity redirectUrls = new RedirectUrlsEntity();

        redirectUrls.setRedirectCode(redirectCode);
        redirectUrls.setRedirectState(xsrfToken);
        redirectUrls.setNotOkURL(fintechRedirectURLNOK);
        redirectUrls.setOkURL(fintechRedirectURLOK);

        redirectUrlRepository.save(redirectUrls);

        return redirectCode;
    }

    public ResponseEntity doRedirect(String redirectState, String redirectId, String redirectCode) {
        if (StringUtils.isBlank(redirectCode)) {
            log.warn("Validation redirect request was failed: redirect code is empty!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<RedirectUrlsEntity> redirectUrlsEntityOptional = redirectUrlRepository.findByRedirectCode(redirectCode);

        if (!redirectUrlsEntityOptional.isPresent()) {
            log.warn("Validation redirect request was failed: redirect code is wrong!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(redirectState)) {
            log.warn("Validation redirect request was failed: Xsrf Token is empty!");
            new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!authorizeService.isAuthorized(redirectState, null)) {
            log.warn("Validation redirect request was failed: Xsrf Token is wrong or user are not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SessionEntity optionalUser = authorizeService.getByXsrfToken(redirectState);
        updateSessionByRedirectCode(optionalUser, redirectCode);

        RedirectUrlsEntity redirectUrlsEntity = redirectUrlsEntityOptional.get();

        return prepareRedirectResponse(redirectUrlsEntity.getOkURL());
    }

    private void updateSessionByRedirectCode(SessionEntity sessionEntity, String redirectCode) {
        PsuConsentSessionResponse psuConsentSessionResponse = new TppBankingApiTokenMock().getTransactionsResponse(redirectCode);
        sessionEntity.setPsuConsentSession(psuConsentSessionResponse.getPsuConsentSession().toString());
        authorizeService.updateUserSession(sessionEntity);
    }

    private ResponseEntity prepareRedirectResponse(String redirectUrl) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Location", singletonList(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
