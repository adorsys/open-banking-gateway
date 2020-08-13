package de.adorsys.opba.fintech.impl.database.entities;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Data
@Entity
@Slf4j
public class RedirectUrlsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "redirect_urls_generator")
    @SequenceGenerator(name = "redirect_urls_generator", sequenceName = "redirect_urls_id_seq")
    private Long id;

    @Column(nullable = false)
    private String okStatePath;

    @Column(nullable = false)
    private String nokStatePath;

    @Column(nullable = false)
    private String redirectCode;

    public static String buildOkUrl(FintechUiConfig config, final String redirectCode) {
        String okUrl = getModifiedUrlWithRedirectCode(config.getRedirectUrl(), redirectCode);
        log.debug("okurl is {}", okUrl);
        return okUrl;
    }

    public static String buildNokUrl(FintechUiConfig config, final String redirectCode) {
        String notokurl = getModifiedUrlWithRedirectCode(config.getExceptionUrl(), redirectCode);
        log.debug("notokurl is {}", notokurl);
        return notokurl;
    }

    private static String getModifiedUrlWithRedirectCode(String redirectUrl, final String redirectCode) {
        return UriComponentsBuilder.fromHttpUrl(redirectUrl)
                .buildAndExpand(redirectCode)
                .toUriString();
    }
}
