package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.datasafe.encrypiton.impl.utils.ProviderUtils;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@RequiredArgsConstructor
public class CmsEncryptionOper {

    private final CmsEncSpec cmsEncSpec;

    public EncryptionService encryptionService(String keyId, PrivateKey privateKey) {
        return new CmsEncryption(
                keyId,
                cmsEncSpec.getCipherAlgo(),
                null,
                privateKey
        );
    }

    public EncryptionService encryptionService(String keyId, PublicKey publicKey) {
        return new CmsEncryption(
                keyId,
                cmsEncSpec.getCipherAlgo(),
                publicKey,
                null
        );
    }

    @SneakyThrows
    public KeyPair generatePublicPrivateKey() {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(cmsEncSpec.getKeyAlgo());
        keyGen.initialize(cmsEncSpec.getLen());
        return keyGen.genKeyPair();
    }

    @RequiredArgsConstructor
    public static class CmsEncryption implements EncryptionService {

        @Getter
        private final String encryptionKeyId;

        private final ASN1ObjectIdentifier algorithm;
        private final PublicKey publicKey;
        private final PrivateKey privateKey;

        @Override
        @SneakyThrows
        public byte[] encrypt(byte[] data) {
            if (null == data) {
                return new byte[0];
            }

            CMSEnvelopedDataGenerator generator = new CMSEnvelopedDataGenerator();
            generator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(encryptionKeyId.getBytes(StandardCharsets.UTF_8), publicKey));
            return generator.generate(
                    new CMSProcessableByteArray(data), new JceCMSContentEncryptorBuilder(algorithm).setProvider(ProviderUtils.bcProvider).build()
            ).getEncoded();
        }

        @Override
        @SneakyThrows
        public byte[] decrypt(byte[] data) {
            if (null == data || 0 == data.length) {
                return null;
            }

            CMSEnvelopedDataParser parser = new CMSEnvelopedDataParser(data);
            return parser.getRecipientInfos().iterator().next().getContent(new JceKeyTransEnvelopedRecipient(privateKey));
        }
    }
}
