-- Migration number: 0001 	 2025-09-11T15:34:40.031Z
-- Table to store user accounts
CREATE TABLE accounts (
    user_id TEXT PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);