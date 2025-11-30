CREATE TABLE IF NOT EXISTS documents (
                                         id           VARCHAR(64)   PRIMARY KEY,
    load_id      VARCHAR(64)   NOT NULL,
    type         VARCHAR(16)   NOT NULL,        -- BOL, POD
    status       VARCHAR(32)   NOT NULL,        -- Uploaded, Verified, Rejected
    file_ref     VARCHAR(2000) NOT NULL,        -- URI string
    uploaded_at  TIMESTAMP WITH TIME ZONE NOT NULL
                               );

CREATE INDEX IF NOT EXISTS idx_documents_load_id ON documents(load_id);
