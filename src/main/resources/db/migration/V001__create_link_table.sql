-- Migration V001: Create link table
-- Description: Create the link table to store link information
-- Author: Matheus Cruz

CREATE TABLE IF NOT EXISTS link (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    destination_url VARCHAR(255) NOT NULL,
    shortened_url VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NULL
);

-- Indexes
CREATE INDEX idx_link_destination_url ON link(destination_url);
CREATE INDEX idx_link_shortened_url ON link(shortened_url);
CREATE INDEX idx_link_code ON link(code);
CREATE INDEX idx_link_active ON link(active);
CREATE INDEX idx_link_created_at ON link(created_at);
CREATE INDEX idx_link_updated_at ON link(updated_at);

-- Comments
COMMENT ON TABLE link IS 'Table to store link information for URL shortening service';
COMMENT ON COLUMN link.id IS 'Unique identifier for the link';
COMMENT ON COLUMN link.destination_url IS 'The original destination URL that the shortened URL points to';
COMMENT ON COLUMN link.shortened_url IS 'The shortened URL that redirects to the destination URL';
COMMENT ON COLUMN link.code IS 'The unique code used to generate the shortened URL';
COMMENT ON COLUMN link.active IS 'Indicates if the link is active';
COMMENT ON COLUMN link.created_at IS 'Timestamp when the link was created';
COMMENT ON COLUMN link.updated_at IS 'Timestamp when the link was updated';