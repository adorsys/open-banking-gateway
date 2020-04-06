package de.adorsys.opba.tppauthapi.service;

import de.adorsys.datasafe.encrypiton.api.keystore.KeyStoreService;
import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.types.api.resource.AbsoluteLocation;
import de.adorsys.datasafe.types.api.resource.PrivateResource;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.transaction.Transactional;
import java.io.OutputStream;
import java.util.Optional;

import static de.adorsys.datasafe.types.api.actions.WriteRequest.forDefaultPrivate;

@Service
@RequiredArgsConstructor
public class PsuAuthService {

    private final PsuRepository psuRepository;
    private final PsuSecureStorage psuSecureStorage;
    private final KeyStoreService keyStoreService;

    public Optional<Psu> getPsu(String psuId, String password) {
        UserIDAuth idAuth = new UserIDAuth(psuId, password::toCharArray);
        boolean userExists = psuSecureStorage.userProfile().userExists(new UserID(psuId));
        if (userExists) {
//            psuSecureStorage.userProfile().
        }
        psuSecureStorage.userProfile().listRegisteredStorageCredentials(idAuth);
        return Optional.empty();
    }

    @SneakyThrows
    public Psu generateConsentKey(String psuId, String password) {
        UserIDAuth idAuth = new UserIDAuth(psuId, password::toCharArray);
        psuSecureStorage.registerPsu(idAuth);
        AbsoluteLocation<PrivateResource> keystore = psuSecureStorage.userProfile().privateProfile(idAuth).getKeystore();


        KeyGenerator kGen = KeyGenerator.getInstance("AES");
        kGen.init(256);
        SecretKey secretKey = kGen.generateKey();

        try (OutputStream os = psuSecureStorage.privateService().write(forDefaultPrivate(idAuth, "/"))) {
            os.write(secretKey.getEncoded());
        }

        return Psu.builder()
                .build();
    }

    @Transactional
    public Psu createPsuIfNotExist(String id, String password) {
        if (psuRepository.findByUserId(id).isPresent()) {
            throw new IllegalArgumentException();
        }
        Psu psu = psuRepository.save(Psu.builder().build());
        UserIDAuth idAuth = new UserIDAuth(psu.getId().toString(), password::toCharArray);
        psuSecureStorage.registerPsu(idAuth);
        return psu;
    }
}
