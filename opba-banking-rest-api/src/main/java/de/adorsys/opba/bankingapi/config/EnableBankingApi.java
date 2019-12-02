package de.adorsys.opba.bankingapi.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(BankingApiConfig.class)
public @interface EnableBankingApi {
}
