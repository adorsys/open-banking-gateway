CREATE SCHEMA IF NOT EXISTS banking_protocol;
CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA banking_protocol;
ALTER DATABASE open_banking SET pg_trgm.word_similarity_threshold = 0.2;
