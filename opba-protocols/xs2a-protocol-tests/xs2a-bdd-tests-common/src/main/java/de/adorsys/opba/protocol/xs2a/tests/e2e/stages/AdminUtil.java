package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ADMIN_API;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.BASIC_AUTH;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.headersWithAuthorization;

@UtilityClass
public class AdminUtil {

     public void adminCallsDeleteBank(String bankUuid) {
        headersWithAuthorization(BASIC_AUTH)
                .when()
                .delete(ADMIN_API + "banks/" + bankUuid)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static void adminChecksThatBankIsDeleted(String bankUuid) {
        headersWithAuthorization(BASIC_AUTH)
        .when()
             .get(ADMIN_API + "banks/" + bankUuid)
        .then()
             .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
