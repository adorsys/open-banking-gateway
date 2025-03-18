package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.ServiceAccountsConfig;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ServiceAccountsOper {

    private final TransactionOperations txOper;
    private final ServiceAccountsConfig serviceAccounts;
    private final AuthorizeService authorizeService;
    private final UserRepository users;

    // Unfortunately @PostConstruct can't have Transactional annotation
    @PostConstruct
    public void createOrActivateOrDeactivateServiceAccounts() {
        txOper.execute(callback -> {
            users.deactivateAllServiceAccounts();
            if (null == serviceAccounts.getAccounts()) {
                return null;
            }

            for (ServiceAccountsConfig.ServiceAccount account : serviceAccounts.getAccounts()) {
                UserEntity user = users.findById(account.getLogin())
                        .map(it -> authorizeService.updatePasswordButDontSave(it, account.getPassword()))
                        .orElseGet(() -> authorizeService.createUserEntityWithPasswordEnabledButDontSave(account.getLogin(), account.getPassword()));

                user.setActive(true);
                user.setServiceAccount(true);
                users.save(user);
            }

            return null;
        });
    }
}
