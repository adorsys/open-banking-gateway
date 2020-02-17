package de.adorsys.opba.protocol.facade.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class KeyAndSaltDto extends KeyDto {
    private byte[] salt;

    public KeyAndSaltDto(byte[] key, byte[] salt) {
        super(key);
        this.salt = salt;
    }
}
