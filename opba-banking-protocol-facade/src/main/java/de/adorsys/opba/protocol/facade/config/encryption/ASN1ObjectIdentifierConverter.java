package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.collect.ImmutableMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationPropertiesBinding
public class ASN1ObjectIdentifierConverter implements Converter<String, ASN1ObjectIdentifier> {

    private static final Map<String, ASN1ObjectIdentifier> MAPPINGS =
            ImmutableMap.<String, ASN1ObjectIdentifier>builder()
                    .put("AES128_CBC", NISTObjectIdentifiers.id_aes128_CBC)
                    .put("AES192_CBC", NISTObjectIdentifiers.id_aes192_CBC)
                    .put("AES256_CBC", NISTObjectIdentifiers.id_aes256_CBC)
                    .put("AES128_CCM", NISTObjectIdentifiers.id_aes128_CCM)
                    .put("AES192_CCM", NISTObjectIdentifiers.id_aes192_CCM)
                    .put("AES256_CCM", NISTObjectIdentifiers.id_aes256_CCM)
                    .put("AES128_GCM", NISTObjectIdentifiers.id_aes128_GCM)
                    .put("AES192_GCM", NISTObjectIdentifiers.id_aes192_GCM)
                    .put("AES256_GCM", NISTObjectIdentifiers.id_aes256_GCM)
                    .put("AES128_WRAP", NISTObjectIdentifiers.id_aes128_wrap)
                    .put("AES192_WRAP", NISTObjectIdentifiers.id_aes192_wrap)
                    .put("AES256_WRAP", NISTObjectIdentifiers.id_aes256_wrap)
                    .put("CHACHA20_POLY1305", PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305) // with CMS should be used data size 64 bytes or more
                    .build();

    @Override
    public ASN1ObjectIdentifier convert(String source) {
        ASN1ObjectIdentifier identifier = MAPPINGS.get(source);
        if (null == identifier) {
            throw new IllegalArgumentException("Unknown mapping: " + source);
        }

        return identifier;
    }
}
