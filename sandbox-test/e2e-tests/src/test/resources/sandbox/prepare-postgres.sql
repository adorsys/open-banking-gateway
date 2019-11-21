CREATE SCHEMA ledgers;
CREATE SCHEMA consent;

CREATE ROLE sandboxrole;
GRANT ALL ON SCHEMA consent TO sandboxrole;
GRANT ALL ON SCHEMA ledgers TO sandboxrole;

CREATE USER "test-ledgers-app" WITH ENCRYPTED PASSWORD 'test-ledgers-app' IN ROLE sandboxrole;
CREATE USER "test-consentmgmt" WITH ENCRYPTED PASSWORD 'test-consentmgmt' IN ROLE sandboxrole;
CREATE USER "test-online-banking" WITH ENCRYPTED PASSWORD 'test-online-banking' IN ROLE sandboxrole;
CREATE USER "test-tpp-rest" WITH ENCRYPTED PASSWORD 'test-tpp-rest' IN ROLE sandboxrole;

/* Necessary, as default privileges apply only from current user context  */
ALTER DEFAULT PRIVILEGES FOR USER "test-ledgers-app" IN SCHEMA consent GRANT ALL PRIVILEGES ON TABLES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER "test-ledgers-app" IN SCHEMA consent GRANT ALL PRIVILEGES ON SEQUENCES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER "test-consentmgmt" IN SCHEMA consent GRANT ALL PRIVILEGES ON TABLES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER "test-consentmgmt" IN SCHEMA consent GRANT ALL PRIVILEGES ON SEQUENCES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER "test-online-banking" IN SCHEMA consent GRANT ALL PRIVILEGES ON TABLES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER "test-online-banking" IN SCHEMA consent GRANT ALL PRIVILEGES ON SEQUENCES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER "test-tpp-rest" IN SCHEMA consent GRANT ALL PRIVILEGES ON TABLES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER "test-tpp-rest" IN SCHEMA consent GRANT ALL PRIVILEGES ON SEQUENCES TO sandboxrole;
