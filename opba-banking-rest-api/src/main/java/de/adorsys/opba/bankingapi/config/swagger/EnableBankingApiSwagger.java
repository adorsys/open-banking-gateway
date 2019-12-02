package de.adorsys.opba.bankingapi.config.swagger;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(BankingApiSwaggerConfig.class)
public @interface EnableBankingApiSwagger {
}
