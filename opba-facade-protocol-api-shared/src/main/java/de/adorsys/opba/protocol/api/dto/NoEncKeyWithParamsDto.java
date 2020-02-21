package de.adorsys.opba.protocol.api.dto;

public class NoEncKeyWithParamsDto extends KeyWithParamsDto {

    public static final String NOOP = "NOOP";

    public NoEncKeyWithParamsDto() {
        super(null, NOOP.getBytes(), NOOP, 0, 0);
    }
}
