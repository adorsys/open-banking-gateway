package de.adorsys.opba.tppauthapi.service;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import de.adorsys.opba.tppauthapi.config.TppProperties;
import de.adorsys.opba.tppauthapi.exceptions.PsuAuthenticationException;
import de.adorsys.opba.tppauthapi.exceptions.PsuAuthorizationException;
import de.adorsys.opba.tppauthapi.exceptions.PsuRegisterException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PsuAuthService {

    private final PsuRepository psuRepository;
    private final PsuSecureStorage psuSecureStorage;
    private final JWSHeader jwsHeader;
    private final RSASSASigner rsassaSigner;
    private final TppProperties tppProperties;

    @Transactional(readOnly = true)
    public Psu tryAuthenticateUser(String psuId, String password) throws PsuAuthorizationException {
        Optional<Psu> psu = psuRepository.findByUserId(psuId);
        if (!psu.isPresent()) {
            throw new PsuAuthenticationException("User not found: " + psuId);
        }
        UserIDAuth idAuth = new UserIDAuth(psu.get().getId().toString(), password::toCharArray);
        try {
            psuSecureStorage.userProfile().privateProfile(idAuth).getKeystore();
        } catch (Exception e) {
            throw new PsuAuthorizationException(e.getMessage(), e);
        }
        return psu.get();
    }

    @Transactional
    public Psu createPsuIfNotExist(String id, String password) {
        Optional<Psu> psu = psuRepository.findByUserId(id);
        if (psu.isPresent()) {
            throw new PsuRegisterException("Psu already exist");
        }
        Psu newPsu = psuRepository.save(Psu.builder().build());
        UserIDAuth idAuth = new UserIDAuth(newPsu.getId().toString(), password::toCharArray);
        psuSecureStorage.registerPsu(idAuth);
        return newPsu;
    }

    @SneakyThrows
    public String generateToken(String id) {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
        Duration duration = Duration.ofSeconds(tppProperties.getKeyValidityDays());
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .expirationTime(Date.from(currentTime.plus(duration).toInstant()))
                .issueTime(Date.from(currentTime.toInstant()))
                .subject(String.valueOf(id))
                .build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        signedJWT.sign(rsassaSigner);
        return signedJWT.serialize();
    }
}
