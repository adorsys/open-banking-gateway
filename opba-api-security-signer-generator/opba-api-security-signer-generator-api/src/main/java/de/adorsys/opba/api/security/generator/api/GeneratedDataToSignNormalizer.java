package de.adorsys.opba.api.security.generator.api;

public @interface GeneratedDataToSignNormalizer {

    String signerClassName() default "";
    String[] openApiYamlPath();
    String signatureHeaderName() default "";
}
