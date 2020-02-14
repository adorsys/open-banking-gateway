package de.adorsys.opba.protocol.facade.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ArrUtils {

    public boolean isEmpty(byte[] array) {
        return array == null || Array.getLength(array) == 0;
    }
}
