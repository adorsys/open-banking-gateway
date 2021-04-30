package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import de.adorsys.xs2a.adapter.api.model.ChallengeData;
import lombok.Data;

import java.util.List;


@Data
public class ChallengeDataLog {
        private List<String> data;
        private String imageLink;
        private Integer otpMaxLength;
        private ChallengeData.OtpFormat otpFormat;
        private String additionalInformation;
}
