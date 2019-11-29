package de.adorsys.opba.core.protocol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountInformationService {

    /**
     * Retrieves users' transactions and establishes consent under the hood if needed.
     */
    @Transactional
    public List<String> transactionList() {
        return Collections.emptyList();
    }
}
