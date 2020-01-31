package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2003;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAccountInformationApi;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.service.AccountService;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ContextInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class FinTechAccountInformationImpl implements FinTechAccountInformationApi {

    @Autowired
    AuthorizeService authorizeService;

    @Autowired
    AccountService accountService;

    @Autowired
    UserRepository userRepository;

    @Override
    public ResponseEntity<InlineResponse2003> aisAccountsGET(String bankId, UUID xRequestID, String xsrfToken) {
        if (!authorizeService.isAuthorized(xsrfToken, null)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ContextInformation contextInformation = new ContextInformation(xRequestID);
        UserEntity userEntity = userRepository.findByXsrfToken(xsrfToken).get();

        return new ResponseEntity<>(accountService.listAccounts(contextInformation, userEntity, bankId), HttpStatus.OK);

    }
}
