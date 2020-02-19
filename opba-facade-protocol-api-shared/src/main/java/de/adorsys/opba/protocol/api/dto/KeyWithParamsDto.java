package de.adorsys.opba.protocol.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KeyWithParamsDto extends KeyDto {
    private byte[] salt;
    private String algorithm;
    private int saltLength;
    private int iterationCount;

    public KeyWithParamsDto(byte[] key) {
        super(key);
    }

    public KeyWithParamsDto(byte[] key, byte[] salt, String algorithm, int saltLength, int iterationCount) {
        super(key);
        this.salt = salt;
        this.algorithm = algorithm;
        this.saltLength = saltLength;
        this.iterationCount = iterationCount;
    }
}
