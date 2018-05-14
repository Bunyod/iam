USE iam;

CREATE TABLE IF NOT EXISTS staged(
    username    text,
    timestamp   timestamp,
    token       tuple<text,timestamp>, PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS users(
    username    text,
    timestamp   timestamp,
    status      int,
    forename    text,
    surname     text,
    password    text,
    role        int,
    token       tuple<text,timestamp>, PRIMARY KEY (username)
);