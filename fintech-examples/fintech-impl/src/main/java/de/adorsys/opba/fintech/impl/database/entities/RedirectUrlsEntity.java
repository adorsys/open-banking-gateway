package de.adorsys.opba.fintech.impl.database.entities;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import lombok.Data;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Data
@Entity
public class RedirectUrlsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "redirect_urls_generator")
    @SequenceGenerator(name = "redirect_urls_generator", sequenceName = "redirect_urls_id_seq")
    private Long id;

    @Column(nullable = false)
    private String redirectState;

    @Column(nullable = false)
    private String okStatePath;

    @Column(nullable = false)
    private String nokStatePath;

    @Column(nullable = false)
    private String redirectCode;

    public String buildOkUrl(FintechUiConfig config) {
        return getModifiedUrlWithRedirectCode(config.getRedirectUrl(), redirectCode);
    }

    public String buildNokUrl(FintechUiConfig config) {
        return getModifiedUrlWithRedirectCode(config.getExceptionUrl(), redirectCode);
    }


    private static String getModifiedUrlWithRedirectCode(String redirectUrl, String... params) {
        return UriComponentsBuilder.fromHttpUrl(redirectUrl)
                .buildAndExpand(params)
                .toUriString();
    }
}
