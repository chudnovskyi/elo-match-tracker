CREATE USER test_user PASSWORD 'test_password';
CREATE DATABASE test_db;
GRANT CREATE ON DATABASE test_db TO test_user;
GRANT CONNECT ON DATABASE test_db TO test_user;

\c test_db

CREATE SCHEMA IF NOT EXISTS schema_v1_end2end;
GRANT ALL PRIVILEGES ON SCHEMA schema_v1_end2end TO test_user;
SET search_path = schema_v1_end2end;

-- No need to create entity tables here since we're not using views in the main migration scripts
