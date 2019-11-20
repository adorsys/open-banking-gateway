CREATE SCHEMA IF NOT EXISTS ledgers;
CREATE SCHEMA IF NOT EXISTS consent;

CREATE USER IF NOT EXISTS "test-ledgers-app" PASSWORD 'test-ledgers-app';
ALTER USER "test-ledgers-app" ADMIN TRUE; /*VERY BROAD, COARSEN IT*/
GRANT ALL ON SCHEMA LEDGERS TO "test-ledgers-app";
GRANT ALL ON SCHEMA CONSENT TO "test-ledgers-app";

CREATE USER IF NOT EXISTS "test-consentmgmt" PASSWORD 'test-consentmgmt';
ALTER USER "test-consentmgmt" ADMIN TRUE; /*VERY BROAD, COARSEN IT*/
GRANT ALL ON SCHEMA CONSENT TO "test-consentmgmt";

CREATE USER IF NOT EXISTS "test-online-banking" PASSWORD 'test-online-banking';
ALTER USER "test-online-banking" ADMIN TRUE; /*VERY BROAD, COARSEN IT*/
GRANT ALL ON SCHEMA CONSENT TO "test-online-banking";

CREATE USER IF NOT EXISTS "test-tpp-rest" PASSWORD 'test-tpp-rest';
ALTER USER "test-tpp-rest" ADMIN TRUE; /*VERY BROAD, COARSEN IT*/
GRANT ALL ON SCHEMA CONSENT TO "test-tpp-rest";