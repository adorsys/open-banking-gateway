package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2003;
import de.adorsys.opba.fintech.api.model.generated.InlineResponse2004;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAccountInformationApi;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.service.AccountService;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ContextInformation;
import de.adorsys.opba.fintech.impl.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
public class FinTechAccountInformationImpl implements FinTechAccountInformationApi {

    @Autowired
    AuthorizeService authorizeService;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserRepository userRepository;

    @Override
    public ResponseEntity<InlineResponse2003> aisAccountsGET(String bankId, UUID xRequestID, String xsrfToken) {
        if (!authorizeService.isAuthorized(xsrfToken, null)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ContextInformation contextInformation = new ContextInformation(xRequestID);
        SessionEntity sessionEntity = userRepository.findByXsrfToken(xsrfToken).get();

        return new ResponseEntity<>(accountService.listAccounts(contextInformation, sessionEntity, bankId), HttpStatus.OK);
    }

    public ResponseEntity<InlineResponse2004> aisTransactionsGET(String bankId, String accountId, UUID xRequestID,
                                                                 String xsrfToken, LocalDate dateFrom, LocalDate dateTo,
                                                                 String entryReferenceFrom, String bookingStatus, Boolean deltaList) {
        if (!authorizeService.isAuthorized(xsrfToken, null)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ContextInformation contextInformation = new ContextInformation(xRequestID);
        SessionEntity sessionEntity = userRepository.findByXsrfToken(xsrfToken).get();

        return new ResponseEntity<>(transactionService.listTransactions(contextInformation, sessionEntity, bankId, accountId,
                dateFrom, dateTo, entryReferenceFrom, bookingStatus, deltaList), HttpStatus.OK);
    }
}
