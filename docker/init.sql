CREATE DATABASE elt_database;
CREATE USER elt_user WITH PASSWORD 'elt_pass';
GRANT ALL PRIVILEGES ON DATABASE "elt_database" to elt_user;
