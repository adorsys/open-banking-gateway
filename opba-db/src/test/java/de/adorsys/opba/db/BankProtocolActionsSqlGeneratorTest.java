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
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.Tokenizer;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adorsys.opba.db.BankProtocolActionsSqlGeneratorTest.ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Is not actually a test. This class generates 'bank_action_data.csv', 'bank_profile_data.csv' and 'bank_sub_action_data.csv' out of 'banks.csv',
 * which data to fill into 'opb_bank', 'opb_bank_profile', 'opb_bank_action' and 'opb_bank_sub_action' tables.
 * It is disabled by default. To enable it, set 'ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION' environment variable to 'true'
 */
@SpringBootTest(classes = TestConfig.class)
@EnabledIfEnvironmentVariable(named = ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION, matches = "true")
public class BankProtocolActionsSqlGeneratorTest {
    public static final String ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION = "ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION";

    private static final String BANK_DATA_SOURCE_PATH = "migration/migrations/csv/v0/xs2a-adapter-banks.csv";

    private static final String BANK_XS2A_ACTION_DESTINATION_PATH = "src/main/resources/migration/migrations/0-xs2a_bank-action.csv";
    private static final String BANK_XS2A_SUB_ACTION_DESTINATION_PATH = "src/main/resources/migration/migrations/0-xs2a_bank-sub-action.csv";

    private static final String BANK_HBCI_ACTION_DESTINATION_PATH = "src/main/resources/migration/migrations/0-hbci_bank-action.csv";
    private static final String BANK_HBCI_SUB_ACTION_DESTINATION_PATH = "src/main/resources/migration/migrations/0-hbci_bank-sub-action.csv";

    private static final String XS2A_BANK_PROFILE_DESTINATION_PATH = "src/main/resources/migration/migrations/0-xs2a_bank-profile.csv";
    private static final String HBCI_BANK_PROFILE_DESTINATION_PATH = "src/main/resources/migration/migrations/0-hbci_bank_bank-profile.csv";
    private static final String BANK_DESTINATION_PATH = "src/main/resources/migration/migrations/0-banks.csv";

    private static final String BANK_ACTION_CSV_HEADER = "id,profile_uuid,protocol_action,protocol_bean_name,consent_supported";
    private static final String BANK_SUB_ACTION_CSV_HEADER = "id,action_id,protocol_action,sub_protocol_bean_name";

    private static final String BANK_CSV_HEADER = "bank_uuid,name,bic,bank_code";
    private static final String BANK_PROFILE_CSV_HEADER = "profile_uuid,bank_uuid,name,bic,url,adapter_id,bank_code,idp_url,aspsp_sca_approaches,protocol_type,is_sandbox";

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
            writeBankData(bank);
            writeXs2aBankProfileData(bank);
            writeHbciBankProfileData(bank);
        }
    }

    private List<String> skipBanksFromFileBeginning(List<String> banks) {
        return banks.subList(skipBanks, banks.size());
    }

    @SneakyThrows
    private void writeBankData(String bankRecord) {
        if ("".equals(bankRecord.trim())) {
            return;
        }

        var tokenizer = new Tokenizer(new StringReader(bankRecord), CsvPreference.EXCEL_PREFERENCE);
        var splitRecord = new ArrayList<String>();
        tokenizer.readColumns(splitRecord);
        var strWriter = new StringWriter();
        try (var writer = new CsvListWriter(strWriter, CsvPreference.EXCEL_PREFERENCE)) {
            writer.write(IntStream.of(0, 1, 2, 5).mapToObj(splitRecord::get).collect(Collectors.toList()));
        }

        writelnToFile(BANK_DESTINATION_PATH, strWriter.toString().replaceAll("\n", ""));
    }

    private void writeXs2aBankProfileData(String bankRecord) {
        var profileId = UUID.randomUUID();
        writelnToFile(XS2A_BANK_PROFILE_DESTINATION_PATH, profileId.toString() + ',' + bankRecord + ",XS2A,false");
        writeXs2aBankActionData(profileId);
    }

    private void writeXs2aBankActionData(UUID profileId) {
        int authorizationId;

        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_ACCOUNTS,xs2aListAccounts,true", bankActionId++, profileId));
        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_TRANSACTIONS,xs2aListTransactions,true", bankActionId++, profileId));
        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,AUTHORIZATION,,true", authorizationId = bankActionId++, profileId));
        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,SINGLE_PAYMENT,xs2aInitiateSinglePayment,true", bankActionId++, profileId));
        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_INFORMATION,xs2aGetPaymentInfoState,true", bankActionId++, profileId));
        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_STATUS,xs2aGetPaymentStatusState,true", bankActionId++, profileId));

        writelnToFile(BANK_XS2A_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,GET_AUTHORIZATION_STATE,xs2aGetAuthorizationState", bankSubActionId++, authorizationId));
        writelnToFile(BANK_XS2A_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,UPDATE_AUTHORIZATION,xs2aUpdateAuthorization", bankSubActionId++, authorizationId));
        writelnToFile(BANK_XS2A_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,FROM_ASPSP_REDIRECT,xs2aFromAspspRedirect", bankSubActionId++, authorizationId));
        writelnToFile(BANK_XS2A_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,DENY_AUTHORIZATION,xs2aDenyAuthorization", bankSubActionId++, authorizationId));

        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,DELETE_CONSENT,xs2aDeleteConsent,true", bankActionId++, profileId));
        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_CONSENT_STATUS,xs2aGetConsentStatus,true", bankActionId++, profileId));
    }

    private void writeHbciBankProfileData(String bankRecord) {
        var profileId = UUID.randomUUID();
        writelnToFile(HBCI_BANK_PROFILE_DESTINATION_PATH, profileId.toString() + ',' + bankRecord + ",HBCI,false");
        writeHbciBankActionData(profileId);
    }

    private void writeHbciBankActionData(UUID profileId) {
        int authorizationId;

        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_ACCOUNTS,hbciListAccounts,false", bankActionId++, profileId));
        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,LIST_TRANSACTIONS,hbciListTransactions,false", bankActionId++, profileId));
        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,AUTHORIZATION,,false", authorizationId = bankActionId++, profileId));
        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,SINGLE_PAYMENT,hbciInitiateSinglePayment,false", bankActionId++, profileId));
        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_INFORMATION,hbciGetPaymentInfoState,false", bankActionId++, profileId));
        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_PAYMENT_STATUS,hbciGetPaymentStatusState,false", bankActionId++, profileId));

        writelnToFile(BANK_HBCI_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,GET_AUTHORIZATION_STATE,hbciGetAuthorizationState", bankSubActionId++, authorizationId));
        writelnToFile(BANK_HBCI_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,UPDATE_AUTHORIZATION,hbciUpdateAuthorization", bankSubActionId++, authorizationId));
        writelnToFile(BANK_HBCI_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,FROM_ASPSP_REDIRECT,hbciFromAspspRedirect", bankSubActionId++, authorizationId));
        writelnToFile(BANK_HBCI_SUB_ACTION_DESTINATION_PATH, String.format("%d,%d,DENY_AUTHORIZATION,hbciDenyAuthorization", bankSubActionId++, authorizationId));

        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,DELETE_CONSENT,hbciDeleteConsent,false", bankActionId++, profileId));
        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, String.format("%d,%s,GET_CONSENT_STATUS,hbciGetConsentStatus,false", bankActionId++, profileId));
    }

    private void prepareDestinationFiles() {
        createOrClearFile(BANK_XS2A_ACTION_DESTINATION_PATH);
        createOrClearFile(BANK_XS2A_SUB_ACTION_DESTINATION_PATH);
        createOrClearFile(BANK_HBCI_ACTION_DESTINATION_PATH);
        createOrClearFile(BANK_HBCI_SUB_ACTION_DESTINATION_PATH);
        createOrClearFile(HBCI_BANK_PROFILE_DESTINATION_PATH);
        createOrClearFile(BANK_DESTINATION_PATH);
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
        writelnToFile(BANK_XS2A_ACTION_DESTINATION_PATH, BANK_ACTION_CSV_HEADER);
        writelnToFile(BANK_XS2A_SUB_ACTION_DESTINATION_PATH, BANK_SUB_ACTION_CSV_HEADER);
        writelnToFile(BANK_HBCI_ACTION_DESTINATION_PATH, BANK_ACTION_CSV_HEADER);
        writelnToFile(BANK_HBCI_SUB_ACTION_DESTINATION_PATH, BANK_SUB_ACTION_CSV_HEADER);
        writelnToFile(HBCI_BANK_PROFILE_DESTINATION_PATH, BANK_PROFILE_CSV_HEADER);
        writelnToFile(XS2A_BANK_PROFILE_DESTINATION_PATH, BANK_PROFILE_CSV_HEADER);
        writelnToFile(BANK_DESTINATION_PATH, BANK_CSV_HEADER);
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
