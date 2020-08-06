package de.adorsys.opba.protocol.api.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ChallengeData {
    private byte[] image;
    private List<String> data;
    private String imageLink;
    private Integer otpMaxLength;
    private OtpFormat otpFormat;
    private String additionalInformation;
}
