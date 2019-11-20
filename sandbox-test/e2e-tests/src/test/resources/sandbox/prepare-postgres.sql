CREATE SCHEMA ledgers;
CREATE SCHEMA consent;

CREATE USER "test-ledgers-app" WITH ENCRYPTED PASSWORD 'test-ledgers-app';
GRANT ALL ON SCHEMA ledgers TO "test-ledgers-app";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ledgers TO "test-ledgers-app";
GRANT ALL ON SCHEMA consent TO "test-ledgers-app";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA consent TO "test-ledgers-app";

CREATE USER "test-consentmgmt" WITH ENCRYPTED PASSWORD 'test-consentmgmt';
GRANT ALL ON SCHEMA consent TO "test-consentmgmt";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA consent TO "test-consentmgmt";

CREATE USER "test-online-banking" WITH ENCRYPTED PASSWORD 'test-online-banking';
GRANT ALL ON SCHEMA consent TO "test-online-banking";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA consent TO "test-online-banking";

CREATE USER "test-tpp-rest" WITH ENCRYPTED PASSWORD 'test-tpp-rest';
GRANT ALL ON SCHEMA consent TO "test-tpp-rest";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA consent TO "test-tpp-rest";