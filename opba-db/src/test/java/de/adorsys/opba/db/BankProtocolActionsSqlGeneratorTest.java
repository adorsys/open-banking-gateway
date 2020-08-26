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
import java.io.FileWriter;
import java.util.List;
import java.util.UUID;

import static de.adorsys.opba.db.BankProtocolActionsSqlGeneratorTest.ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Is not actually a test. This class generates 'bank_action_data.csv', 'bank_profile_data.csv' and 'bank_sub_action_data.csv' out of 'banks.csv',
 * which data to fill into 'opb_bank', 'opb_bank_profile', 'opb_bank_action' and 'opb_bank_sub_action' tables.
 * It is disable by default. To enable it, set 'ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION' environment variable to 'true'
 */
@SpringBootTest(classes = TestConfig.class)
@EnabledIfEnvironmentVariable(named = ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION, matches = "true")
public class BankProtocolActionsSqlGeneratorTest {
    public static final String ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION = "ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION";

    private static final String BANK_DATA_SOURCE_PATH = "migration/migrations/banks.csv";

    private static final String BANK_ACTION_DESTINATION_PATH = "src/main/resources/migration/migrations/bank_action_data.csv";
    private static final String BANK_SUB_ACTION_DESTINATION_PATH = "src/main/resources/migration/migrations/bank_sub_action_data.csv";
    private static final String BANK_PROFILE_DESTINATION_PATH = "src/main/resources/migration/migrations/bank_profile_data.csv";

    private static final String BANK_ACTION_CSV_HEADER = "id,bank_uuid,protocol_action,protocol_bean_name,consent_supported";
    private static final String BANK_SUB_ACTION_CSV_HEADER = "id,action_id,protocol_action,sub_protocol_bean_name";
    private static final String BANK_PROFILE_CSV_HEADER = "uuid,name,bic,url,adapter_id,bank_code,idp_url,aspsp_sca_approaches";

    @Value("${bank-action-generator.action.start-id}")
    private Integer bankActionId;

    @Value("${bank-action-generator.sub-action.start-id}")
    private Integer bankSubActionId;

    @Value("${bank-action-generator.skip-banks}")
    private Integer skipBanks;

    @Test
    @SneakyThrows
    public void convertToDbSql() {
        List<String> banks = skipBanksFromFileBeginning(readResourceLines(BANK_DATA_SOURCE_PATH));
        prepareDestinationFiles();

        for (String bank : banks) {
            writeXs2aBankActionData(bank);
            writeHbciBankData(bank);
        }
    }

    private List<String> skipBanksFromFileBeginning(List<String> banks) {
        return banks.subList(skipBanks, banks.size());
    }

    private void writeXs2aBankActionData(String bankRecord) {
        String bankUUID = bankRecord.substring(0, bankRecord.indexOf(','));
        int authorizationId;

        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_ACCOUNTS,xs2aListAccounts,true", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_TRANSACTIONS,xs2aSandboxListTransactions,true", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,AUTHORIZATION,,true", authorizationId = bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,SINGLE_PAYMENT,xs2aInitiateSinglePayment,true", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_INFORMATION,xs2aGetPaymentInfoState,true", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_STATUS,xs2aGetPaymentStatusState,true", bankActionId++, bankUUID));

        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,GET_AUTHORIZATION_STATE,xs2aGetAuthorizationState", bankSubActionId++, authorizationId));
        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,UPDATE_AUTHORIZATION,xs2aUpdateAuthorization", bankSubActionId++, authorizationId));
        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,FROM_ASPSP_REDIRECT,xs2aFromAspspRedirect", bankSubActionId++, authorizationId));
        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,DENY_AUTHORIZATION,xs2aDenyAuthorization", bankSubActionId++, authorizationId));
    }

    private void writeHbciBankData(String bankRecord) {
        String recordWithNewUUID = replaceWithRandomUUID(bankRecord);
        writelnToFile(BANK_PROFILE_DESTINATION_PATH, recordWithNewUUID);
        writeHbciBankActionData(recordWithNewUUID);
    }

    private void writeHbciBankActionData(String bankRecord) {
        String bankUUID = bankRecord.substring(0, bankRecord.indexOf(','));
        int authorizationId;

        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_ACCOUNTS,hbciListAccounts,false", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_TRANSACTIONS,hbciListTransactions,false", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,AUTHORIZATION,,false", authorizationId = bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,SINGLE_PAYMENT,hbciInitiateSinglePayment,false", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_INFORMATION,hbciGetPaymentInfoState,false", bankActionId++, bankUUID));
        writelnToFile(BANK_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_STATUS,hbciGetPaymentStatusState,false", bankActionId++, bankUUID));

        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,GET_AUTHORIZATION_STATE,hbciGetAuthorizationState", bankSubActionId++, authorizationId));
        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,UPDATE_AUTHORIZATION,hbciUpdateAuthorization", bankSubActionId++, authorizationId));
        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,FROM_ASPSP_REDIRECT,hbciFromAspspRedirect", bankSubActionId++, authorizationId));
        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,DENY_AUTHORIZATION,hbciDenyAuthorization", bankSubActionId++, authorizationId));
    }

    private void prepareDestinationFiles() {
        createOrClearFile(BANK_ACTION_DESTINATION_PATH);
        createOrClearFile(BANK_SUB_ACTION_DESTINATION_PATH);
        createOrClearFile(BANK_PROFILE_DESTINATION_PATH);
        writeCsvHeaders();
    }

    private void createOrClearFile(String path) {
        boolean exists = new File(path).exists();

        if (!exists){
            createFile(path);
            return;
        }

        clearFile(path);
    }

    private void writeCsvHeaders() {
        writelnToFile(BANK_ACTION_DESTINATION_PATH, BANK_ACTION_CSV_HEADER);
        writelnToFile(BANK_SUB_ACTION_DESTINATION_PATH, BANK_SUB_ACTION_CSV_HEADER);
        writelnToFile(BANK_PROFILE_DESTINATION_PATH, BANK_PROFILE_CSV_HEADER);
    }

    private String replaceWithRandomUUID(String bankRecord) {
        return String.format("%s%s", UUID.randomUUID().toString(), bankRecord.substring(bankRecord.indexOf(',')));
    }

    @SneakyThrows
    private void clearFile(String path) {
        new FileWriter(path, false).close();
    }

    @SneakyThrows
    private void createFile(String path) {
        Files.touch(new File(path));
    }

    @SneakyThrows
    private List<String> readResourceLines(String path) {
        return Resources.readLines(Resources.getResource(path), UTF_8);
    }

    @SneakyThrows
    private void writelnToFile(String path, String data) {
        Files.asCharSink(new File(path), UTF_8, FileWriteMode.APPEND).write(data + "\n");
    }
}
