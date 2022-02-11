CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA extensions;

CREATE TABLE stock_keeping.user
(
    id        INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_uuid UUID UNIQUE NOT NULL DEFAULT extensions.uuid_generate_v4(),
    email     TEXT UNIQUE NOT NULL,
    password  TEXT        NOT NULL,
    role      TEXT        NOT NULL
);

CREATE TABLE stock_keeping.store
(
    id         INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    store_uuid UUID UNIQUE NOT NULL DEFAULT extensions.uuid_generate_v4(),
    name       TEXT        NOT NULL
);

CREATE TABLE stock_keeping.article
(
    id           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    article_uuid UUID UNIQUE NOT NULL DEFAULT extensions.uuid_generate_v4(),
    name         TEXT        NOT NULL
);

CREATE TABLE stock_keeping.store_article
(
    article_id      INT REFERENCES stock_keeping.article (id) ON UPDATE CASCADE ON DELETE CASCADE,
    store_id        INT REFERENCES stock_keeping.store (id) ON UPDATE CASCADE ON DELETE CASCADE,
    available_stock INTEGER NOT NULL DEFAULT 0 CHECK (available_stock >= 0),
    CONSTRAINT store_article_pkey PRIMARY KEY (article_id, store_id)
);

CREATE TABLE stock_keeping.reserved_article
(
    article_id       INT REFERENCES stock_keeping.article (id) ON UPDATE CASCADE ON DELETE CASCADE,
    store_id         INT REFERENCES stock_keeping.store (id) ON UPDATE CASCADE ON DELETE CASCADE,
    reserved_stock   INTEGER   NOT NULL DEFAULT 0 CHECK (reserved_stock >= 0),
    reservation_time TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT reserved_article_pkey PRIMARY KEY (article_id, store_id)
);