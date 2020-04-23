package de.adorsys.opba.fintech.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReplaceAuthIDTest {
    private static final String AUTH_ID_VARIABLE = "\\{auth-id}";


    @Test
    public void testOk() {
        String path = "stringcontains{auth-id}";
        Assertions.assertTrue(path.matches("(.*)"+ AUTH_ID_VARIABLE +"(.*)"));
        String newpath = path.replaceAll(AUTH_ID_VARIABLE, "authid");
        Assertions.assertEquals("stringcontainsauthid", newpath);
    }
}
