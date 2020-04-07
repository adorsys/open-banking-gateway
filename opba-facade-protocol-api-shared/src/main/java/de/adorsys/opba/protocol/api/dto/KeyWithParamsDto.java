package de.adorsys.opba.protocol.api.dto;

import lombok.Getter;

@Getter
public class KeyWithParamsDto extends KeyDto {
    private byte[] salt;
    private String algorithm;
    private int saltLength;
    private int iterationCount;

    public KeyWithParamsDto(String id, byte[] key) {
        super(id, key);
    }

    public KeyWithParamsDto(String id, byte[] key, byte[] salt, String algorithm, int saltLength, int iterationCount) {
        super(id, key);
        this.salt = salt;
        this.algorithm = algorithm;
        this.saltLength = saltLength;
        this.iterationCount = iterationCount;
    }
}
