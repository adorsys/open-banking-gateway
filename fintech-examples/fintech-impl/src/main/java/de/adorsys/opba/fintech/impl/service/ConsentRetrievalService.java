package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsentRetrievalService {
    private final AuthorizeService authorizeService;
    private final ConsentRepository consentRepository;

    public ConsentRetrievalResult get(String userid, String password) {
        ConsentRetrievalResult consentRetrievalResult = new ConsentRetrievalResult();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.password(password);
        loginRequest.username(userid);
        Optional<UserEntity> userEntity = authorizeService.loginWithPassword(loginRequest);
        if (!userEntity.isPresent()) {
            return consentRetrievalResult;
        }

        consentRetrievalResult.userid = userEntity.get().getFintechUserId();
        consentRetrievalResult.bankIdConsentIdList = new ArrayList<>();

        consentRepository.findByUserEntityAndConsentTypeAndConsentConfirmedOrderByCreationTimeDesc(
            userEntity.get(),
            ConsentType.AIS,
            true).stream().forEach(el -> {
                consentRetrievalResult.getBankIdConsentIdList().add(new BankId2ConsentId(el.getBankId(), el.getCreationTime().toString(), el.getTppServiceSessionId()));
        });

        return consentRetrievalResult;
    }

    @Data
    public static class ConsentRetrievalResult {
        private String userid;
        private List<BankId2ConsentId> bankIdConsentIdList;
    }

    @Data
    public static class BankId2ConsentId {
        private final String bankId;
        private final String date;
        private final UUID consentId;
    }


}
