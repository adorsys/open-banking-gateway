package de.adorsys.opba.db;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Disabled
public class CsvConvertTest {
    private static final String BANKS_SOURCE = "migration/migrations/banks.csv";
    private static final String BANKS_ACTION_SQL = "bank_action_sql.txt";
    private static final String BANKS_SUB_ACTION_SQL = "bank_sub_action_sql.txt";

    @Test
    @SneakyThrows
    public void convertToDbCsv() {
        int bankId = 1;
        int bankActionId = 1;
        List<String> banks = readResourcLines(BANKS_SOURCE);
        banks.remove(0);

        for (String bank : banks) {
            int authorizationId;
            String[] data = bank.split(",");
            writeToFile(BANKS_ACTION_SQL, "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (" + bankId++ + ", '" + data[0] + "', 'LIST_ACCOUNTS', 'xs2aListAccounts');");
            writeToFile(BANKS_ACTION_SQL, "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (" + bankId++ + ", '" + data[0] + "', 'LIST_TRANSACTIONS', 'xs2aSandboxListTransactions');");
            writeToFile(BANKS_ACTION_SQL, "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (" + (authorizationId = bankId++) + ", '" + data[0] + "', 'AUTHORIZATION', '');");
            writeToFile(BANKS_ACTION_SQL, "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (" + bankId++ + ", '" + data[0] + "', 'SINGLE_PAYMENT', 'xs2aInitiateSinglePayment');");
            writeToFile(BANKS_ACTION_SQL, "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (" + bankId++ + ", '" + data[0] + "', 'GET_PAYMENT_INFORMATION', 'xs2aGetPaymentInfoState');");
            writeToFile(BANKS_ACTION_SQL, "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (" + bankId++ + ", '" + data[0] + "', 'GET_PAYMENT_STATUS', 'xs2aGetPaymentStatusState');");


        writeToFile(BANKS_SUB_ACTION_SQL, "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (" + bankActionId++ + ", '" + authorizationId + ", 'GET_AUTHORIZATION_STATE', 'xs2aGetAuthorizationState');");
        writeToFile(BANKS_SUB_ACTION_SQL, "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (" + bankActionId++ + ", '" + authorizationId + ", 'UPDATE_AUTHORIZATION', 'xs2aUpdateAuthorization');");
        writeToFile(BANKS_SUB_ACTION_SQL, "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (" + bankActionId++ + ", '" + authorizationId + ", 'FROM_ASPSP_REDIRECT', 'xs2aFromAspspRedirect');");
        writeToFile(BANKS_SUB_ACTION_SQL, "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (" + bankActionId++ + ", '" + authorizationId + ", 'DENY_AUTHORIZATION', 'xs2aDenyAuthorization');");
        }
    }

    @SneakyThrows
    private List<String> readResourcLines(String path) {
        return Resources.readLines(Resources.getResource(path), UTF_8);
    }

    @SneakyThrows
    private void writeToFile(String path, String data) {
        Files.asCharSink(new File(path), UTF_8).write(data);
    }
}
