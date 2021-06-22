import java.util.stream.IntStream

file = new File('./out.txt')

def generateXs2aStatements(id, bankId, note, authId, authActionIdDelta) {
    def authActionId = id + authActionIdDelta

    def baseStatements =
            """
<!--  ${note}  -->
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'LIST_ACCOUNTS', 'xs2aListAccounts');
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'LIST_TRANSACTIONS', 'xs2aSandboxListTransactions');
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'AUTHORIZATION', '');
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'SINGLE_PAYMENT', 'xs2aInitiateSinglePayment');
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'GET_PAYMENT_INFORMATION', 'xs2aGetPaymentInfoState');
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'GET_PAYMENT_STATUS', 'xs2aGetPaymentStatusState');
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'DELETE_CONSENT', 'xs2aDeleteConsent');
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'GET_CONSENT_STATUS', 'xs2aGetConsentStatus');

insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'GET_AUTHORIZATION_STATE', 'xs2aGetAuthorizationState');
insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'UPDATE_AUTHORIZATION', 'xs2aUpdateAuthorization');
insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'FROM_ASPSP_REDIRECT', 'xs2aFromAspspRedirect');
insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'DENY_AUTHORIZATION', 'xs2aDenyAuthorization');
"""

    def generatedStatements = baseStatements

    IntStream.rangeClosed(1, 20).forEach {
        def pos = String.format('%02d', it)
        generatedStatements += "insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name) values (${id++}, '${bankId}', 'XS2A_STUB${pos}', 'xs2aStub${pos}');\n"
    }
    generatedStatements += '\n'
    IntStream.rangeClosed(1, 20).forEach {
        def pos = String.format('%02d', it)
        generatedStatements += "insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'XS2A_STUB${pos}', 'xs2aStub${pos}');\n"
    }

    file << generatedStatements
    return [id, authId]
}

def generateHbciStatements(id, bankId, note, authId, authActionIdDelta) {
    def authActionId = id + authActionIdDelta

    def baseStatements =
            """
<!--  ${note}  -->
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (${id++}, '${bankId}', 'LIST_ACCOUNTS', 'hbciListAccounts', false);
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (${id++}, '${bankId}', 'LIST_TRANSACTIONS', 'hbciListTransactions', false);
insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (${id++}, '${bankId}', 'AUTHORIZATION', '', false);

insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'GET_AUTHORIZATION_STATE', 'hbciGetAuthorizationState');
insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'UPDATE_AUTHORIZATION', 'hbciUpdateAuthorization');
insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'FROM_ASPSP_REDIRECT', 'hbciFromAspspRedirect');
insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'DENY_AUTHORIZATION', 'hbciDenyAuthorization');
"""

    def generatedStatements = baseStatements

    IntStream.rangeClosed(1, 20).forEach {
        def pos = String.format('%02d', it)
        generatedStatements += "insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (${id++}, '${bankId}', 'HBCI_STUB${pos}', 'hbciStub${pos}', false);\n";
    }
    generatedStatements += '\n'
    IntStream.rangeClosed(1, 20).forEach {
        def pos = String.format('%02d', it)
        generatedStatements += "insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'HBCI_STUB${pos}', 'hbciStub${pos}');\n"
    }

    file << generatedStatements
    return [id, authId]
}

def generateGeneric(id, bankId, note, authId, authActionIdDelta) {
    def authActionId = id + authActionIdDelta
    def generatedStatements = "\n<!--  ${note}  -->\n"

    IntStream.rangeClosed(1, 20).forEach {
        def pos = String.format('%02d', it)
        generatedStatements += "insert into \${table-prefix}bank_action (id, bank_uuid, protocol_action, protocol_bean_name, consent_supported) values (${id++}, '${bankId}', 'HBCI_STUB${pos}', 'hbciStub${pos}', false);\n";
    }
    generatedStatements += '\n'
    IntStream.rangeClosed(1, 20).forEach {
        def pos = String.format('%02d', it)
        generatedStatements += "insert into \${table-prefix}bank_sub_action (id, action_id, protocol_action, sub_protocol_bean_name) values (${authId++}, ${authActionId}, 'HBCI_STUB${pos}', 'hbciStub${pos}');\n"
    }

    file << generatedStatements
    return [id, authId]
}

// id, bankId, note, authId, authActionIdDelta
def xs2aData = [
        ['53c47f54-b9a4-465a-8f77-bc6cd5f0cf46', 'ADORSYS ASPSP', 2],
        ['2d8b3e75-9e3e-4fd2-b79c-063556ad9ecc', 'ADORSYS REDIRECT ASPSP', 2],
        ['aa750320-2958-455e-9926-e9fca5ddfa92', 'ADORSYS EMBEDDED ASPSP', 2],
        ['45e6cda0-69a7-4731-90fc-2b13a42fd3bd', 'ADORSYS OAUTH-PRE-STEP ASPSP', 2],
        ['867a53d8-4cca-4365-a393-7febb0bbd38e', 'ADORSYS OAUTH-INTEGRATED ASPSP', 2]
]

def hbciData = [
        ['918d80fa-f7fd-4c9f-a6bd-7a9e12aeee76', 'HBCI BLZ 10000001', 2],
        ['0a1a6417-4913-4238-ab56-058025c3b2b8', 'HBCI BLZ 20000002', 2],
        ['125ef2c6-f414-4a10-a865-e3cdddf9753d', 'HBCI BLZ 30000003', 2]
]

def id = 1
def authId = 1
file.write("\n\n<!-- XS2A MOCK banks -->\n\n")
xs2aData.forEach {
    def res = generateXs2aStatements(id, it[0], it[1], authId, it[2])
    id = res[0]
    authId = res[1]
}
file << ("\n\n<!-- HBCI MOCK banks -->\n\n")
hbciData.forEach {
    def res = generateHbciStatements(id, it[0], it[1], authId, it[2])
    id = res[0]
    authId = res[1]
}

/*
file << ("\n\n<!-- STUB MOCK banks -->\n\n")
IntStream.rangeClosed(1, 50).forEach{
    def res = generateGeneric(id, UUID.randomUUID().toString(), "STUB BANK #${it}", authId, 2)
    id = res[0]
    authId = res[1]
}
 */