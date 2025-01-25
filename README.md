## Run the main project
cd projects/main
clojure -M -m app.main.core

## Devops
Connect to the instance
ssh -i instance.key ubuntu@129.151.206.131

Connect to postgres container
docker exec -it prod-db /bin/bash

Create a database
psql -U user -d postgres
CREATE DATABASE "property-management";

Connect to the database
psql db -U user -d property-management;

Initial DDL for the user sign-in sign-up simple mechanism
-- Table to store user accounts
create table accounts (email text primary key, password text not null, created_at timestamp not null default current_timestamp);

-- Table required for jdbc-session-store
CREATE TABLE session_store (session_id VARCHAR(36) NOT NULL PRIMARY KEY, idle_timeout BIGINT, absolute_timeout BIGINT, value BYTEA);

Check the logs
docker logs management -f

## Feature management
Open feature flags UI
ssh -i ssh-key-2023-04-27.key -L 4000:127.0.0.1:9090 root@195.201.1.232
Open http://localhost:4000/flags