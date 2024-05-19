CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS cargo_item_type (
    id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    shape TEXT,
    legend CHAR(1)
);

CREATE TABLE IF NOT EXISTS cargo_van_type (
    id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    width integer,
    height integer,
    CHECK (width > 0 AND height > 0)
);