package de.adorsys.opba.api.security.generator.api;

public @interface GeneratedSigner {

    String signerClassName() default "";
    String[] openApiYamlPath();
    String signatureHeaderName() default "";
}
