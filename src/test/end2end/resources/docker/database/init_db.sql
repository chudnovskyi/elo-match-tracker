CREATE USER test_user PASSWORD 'test_password';
CREATE DATABASE test_db;
GRANT CREATE ON DATABASE test_db TO test_user;
GRANT CONNECT ON DATABASE test_db TO test_user;
