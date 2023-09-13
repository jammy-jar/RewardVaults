CREATE TABLE IF NOT EXISTS user_data (
    id          INTEGER PRIMARY KEY     NOT NULL,
    uuid        CHAR(36)                NOT NULL,
    items       BLOB                    NOT NULL,
    UNIQUE(uuid)
);

