package de.adorsys.opba.core.protocol.config.hardcoded;

import de.adorsys.xs2a.adapter.service.model.Aspsp;
import de.adorsys.xs2a.adapter.service.model.AspspScaApproach;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "xs2a-profile")
public class AspspHardcodedRecord {

    private String id;
    private String aspspName;
    private String bic;
    private String url;
    private String bankCode;
    private String adapterId;
    private String idpUrl;
    private List<AspspScaApproach> aspspScaApproaches;

    public Aspsp aspsp() {
        Aspsp aspsp = new Aspsp();
        aspsp.setId(id);
        aspsp.setBic(bic);
        aspsp.setUrl(url);
        aspsp.setName(aspspName);
        aspsp.setAdapterId(adapterId);
        aspsp.setBankCode(bankCode);
        aspsp.setIdpUrl(idpUrl);
        aspsp.setScaApproaches(aspspScaApproaches);
        return aspsp;
    }
}
