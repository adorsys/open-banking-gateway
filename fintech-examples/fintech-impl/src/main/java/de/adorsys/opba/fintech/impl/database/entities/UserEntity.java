package de.adorsys.opba.fintech.impl.database.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Contains the "static data" of the user
 * Will later contain list of IBANs and other information
 * that is not bound to one particular session
 */
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UserEntity {
    @Id
    private String loginUserName;
    private String password;
    private String fintechUserId;

    private boolean serviceAccount;
    private boolean active;

    /**
     * For Oauth2-created users we disable password login (because these users can't set password when created).
     */
    private boolean enablePasswordLogin;
}
