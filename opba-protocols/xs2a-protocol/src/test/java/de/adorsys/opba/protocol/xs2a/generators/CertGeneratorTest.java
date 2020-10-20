package de.adorsys.opba.protocol.xs2a.generators;

import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.testcontainers.shaded.org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * This is not a test, but pkcs12 keystore generator for xs2a-adapter (with qwac and qseal keys).
 * See https://github.com/adorsys/XS2A-Sandbox @ certificate-generator for details.
 * It reads pem/pub pairs and generates keystore from it.
 * PEM/PUB (i.e. qseal.pub, qseal.pem) are generated with
 * `https://github.com/adorsys/XS2A-Sandbox @ certificate-generator` REST application.
 */
@Slf4j
@EnabledIfEnvironmentVariable(named = "GENERATE_QWAC_DUMMY", matches = "true")
class CertGeneratorTest {

    private static final char[] PASSWORD = "password".toCharArray();

    /**
     * Generates P12 keystore from dummy qwac.pem/pub and qseal.pem/pub files (in resources) that can be used as
     * keystore for XS2A-adapter. Keystore and key is protected by password `password`.
     * Resulting KeyStore is placed in current directory with name `sample-qwac.keystore`
     */
    @Test
    @SneakyThrows
    void generateQwacKeystore() {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore qwacKeyStore = KeyStore.getInstance("PKCS12");
        qwacKeyStore.load(null, PASSWORD);
        setKeyStoreEntry("dummykeys/qseal/qseal.pem", "dummykeys/qseal/qseal.pub", "default_qseal", qwacKeyStore);
        setKeyStoreEntry("dummykeys/qwac/qwac.pem", "dummykeys/qwac/qwac.pub", "default_qwac", qwacKeyStore);

        Path qwacPath = Paths.get("sample-qwac.keystore");
        try (OutputStream os = Files.newOutputStream(qwacPath)) {
            qwacKeyStore.store(os, PASSWORD);
            log.info("Saved qwac keystore to {}", qwacPath.toAbsolutePath());
        }
    }

    @SneakyThrows
    private void setKeyStoreEntry(String pemResourcePath, String cerResourcePath, String keyStoreAlias,
                                  KeyStore keyStore) {
        PEMKeyPair pair = read(pemResourcePath);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
        KeyPair kp = converter.getKeyPair(pair);
        PrivateKey privateKey = kp.getPrivate();
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(readRaw(cerResourcePath));
        keyStore.setKeyEntry(keyStoreAlias, privateKey, PASSWORD, new Certificate[]{cert});
    }

    @SneakyThrows
    private PEMKeyPair read(String resourceName) {
        try (PEMParser pemReader = new PEMParser(
                new InputStreamReader(
                        new ByteArrayInputStream(Resources.toByteArray(Resources.getResource(resourceName)))
                )
        )) {
            return (PEMKeyPair) pemReader.readObject();
        }
    }

    @SneakyThrows
    private InputStream readRaw(String resourceName) {
        return new ByteArrayInputStream(Resources.toByteArray(Resources.getResource(resourceName)));
    }
}
