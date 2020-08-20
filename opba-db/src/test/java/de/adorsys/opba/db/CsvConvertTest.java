package de.adorsys.opba.db;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.adorsys.opba.db.config.TestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

import static de.adorsys.opba.db.CsvConvertTest.ENABLE_HEAVY_TESTS;
import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootTest(classes = TestConfig.class)
@EnabledIfEnvironmentVariable(named = ENABLE_HEAVY_TESTS, matches = "true")
public class CsvConvertTest {
    public static final String ENABLE_HEAVY_TESTS = "ENABLE_CSV_CONVERSION";

    private static final String BANKS_SOURCE = "migration/migrations/banks.csv";
    private static final String BANKS_TARGET = "src/main/resources/migration/migrations/bank_action_data.sql";

    @Value("${test.bank-action-id}")
    private Integer bankActionId;

    @Value("${test.bank-sub-action-id}")
    private Integer bankSubActionId;

    @Test
    @SneakyThrows
    public void convertToDbCsv() {
        List<String> banks = readResourceLines(BANKS_SOURCE);
        banks.remove(0);
        createTargetFileIfNotExists(BANKS_TARGET);

        for (String bank : banks) {
            writeXs2aData(bank);
            writeHbciData(bank);
        }
    }

    private void writeXs2aData(String bankRecord) { ;
        String[] data = bankRecord.split(",");
        int authorizationId;

        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (%d, '%s', 'LIST_ACCOUNTS', 'xs2aListAccounts');",
                bankActionId++, data[0]));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (%d, '%s', 'LIST_TRANSACTIONS', 'xs2aSandboxListTransactions');",
                bankActionId++, data[0]));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (%d, '%s', 'AUTHORIZATION', '');",
                authorizationId = bankActionId++, data[0]));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (%d, '%s', 'SINGLE_PAYMENT', 'xs2aInitiateSinglePayment');",
                bankActionId++, data[0]));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (%d, '%s', 'GET_PAYMENT_INFORMATION', 'xs2aGetPaymentInfoState');",
                bankActionId++, data[0]));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (%d, '%s', 'GET_PAYMENT_STATUS', 'xs2aGetPaymentStatusState');",
                bankActionId++, data[0]));

        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'GET_AUTHORIZATION_STATE', 'xs2aGetAuthorizationState');",
                bankSubActionId++, authorizationId));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'UPDATE_AUTHORIZATION', 'xs2aUpdateAuthorization');",
                bankSubActionId++, authorizationId));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'FROM_ASPSP_REDIRECT', 'xs2aFromAspspRedirect');",
                bankSubActionId++, authorizationId));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'DENY_AUTHORIZATION', 'xs2aDenyAuthorization');",
                bankSubActionId++, authorizationId));
    }

    private void writeHbciData(String bankRecord) {
        String[] data = bankRecord.split(",");
        int authorizationId;

        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (%d, '%s', 'LIST_ACCOUNTS', 'hbciListAccounts', %s);",
                bankActionId++, data[0], false));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (%d, '%s', 'LIST_TRANSACTIONS', 'hbciListTransactions', %s);",
                bankActionId++, data[0], false));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (%d, '%s', 'AUTHORIZATION', '', %s);",
                authorizationId = bankActionId++, data[0], false));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (%d, '%s', 'SINGLE_PAYMENT', 'hbciInitiateSinglePayment', %s);",
                bankActionId++, data[0], false));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (%d, '%s', 'GET_PAYMENT_INFORMATION', 'hbciGetPaymentStatusState', %s);",
                bankActionId++, data[0], false));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (%d, '%s', 'GET_PAYMENT_STATUS', 'hbciGetPaymentInfoState', %s);",
                bankActionId++, data[0], false));

        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'GET_AUTHORIZATION_STATE', 'hbciGetAuthorizationState');",
                bankSubActionId++, authorizationId));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'UPDATE_AUTHORIZATION', 'hbciUpdateAuthorization');",
                bankSubActionId++, authorizationId));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'FROM_ASPSP_REDIRECT', 'hbciFromAspspRedirect');",
                bankSubActionId++, authorizationId));
        writeToFile(BANKS_TARGET, String.format(
                "insert into ${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (%d, '%d, 'DENY_AUTHORIZATION', 'hbciDenyAuthorization');",
                bankSubActionId++, authorizationId));
    }

    @SneakyThrows
    private List<String> readResourceLines(String path) {
        return Resources.readLines(Resources.getResource(path), UTF_8);
    }

    @SneakyThrows
    private void createTargetFileIfNotExists(String filePath) {
        boolean exists = new File(filePath).exists();

        if (!exists){
            Files.touch(new File(filePath));
        }
    }

    @SneakyThrows
    private void writeToFile(String path, String data) {
        Files.asCharSink(new File(path), UTF_8, FileWriteMode.APPEND).write(data);
    }
}
