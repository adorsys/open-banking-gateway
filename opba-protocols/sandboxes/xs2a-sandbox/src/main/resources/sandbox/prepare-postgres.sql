CREATE SCHEMA ledgers;
CREATE SCHEMA consent;
CREATE SCHEMA tpp;

CREATE ROLE sandboxrole;
GRANT ALL ON SCHEMA consent TO sandboxrole;
GRANT ALL ON SCHEMA tpp TO sandboxrole;

/* Ledgers is has its own database */
CREATE USER testledgersapp WITH ENCRYPTED PASSWORD 'testledgersapp';
GRANT ALL ON SCHEMA ledgers TO testledgersapp;

/* This db is shared */
CREATE USER testconsentmgmt WITH ENCRYPTED PASSWORD 'testconsentmgmt' IN ROLE sandboxrole;
CREATE USER testonlinebanking WITH ENCRYPTED PASSWORD 'testonlinebanking' IN ROLE sandboxrole;
CREATE USER testtpprest WITH ENCRYPTED PASSWORD 'testtpprest' IN ROLE sandboxrole;

/* Necessary, as default privileges apply only from current user context  */
ALTER DEFAULT PRIVILEGES FOR USER testconsentmgmt IN SCHEMA consent GRANT ALL PRIVILEGES ON TABLES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER testconsentmgmt IN SCHEMA consent GRANT ALL PRIVILEGES ON SEQUENCES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER testonlinebanking IN SCHEMA consent GRANT ALL PRIVILEGES ON TABLES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER testonlinebanking IN SCHEMA consent GRANT ALL PRIVILEGES ON SEQUENCES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER testtpprest IN SCHEMA consent GRANT ALL PRIVILEGES ON TABLES TO sandboxrole;
ALTER DEFAULT PRIVILEGES FOR USER testtpprest IN SCHEMA consent GRANT ALL PRIVILEGES ON SEQUENCES TO sandboxrole;

/* Access to Large objects in DB (by default is off) */
ALTER DATABASE sandbox_apps SET lo_compat_privileges=on;