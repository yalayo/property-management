-- Table to store user accounts
create table accounts
(
    user_id text primary key,
    email text unique,
    password text not null,
    veryfied boolean default false,
    created_at timestamp not null default current_timestamp
);

-- Table required for jdbc-session-store
create table session_store
(
    session_id varchar(36) not null primary key,
    idle_timeout bigint,
    absolute_timeout bigint,
    value bytea
);
