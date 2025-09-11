-- Migration number: 0001 	 2025-09-11T15:34:40.031Z
-- Table to store user accounts
CREATE TABLE accounts (
    email TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);