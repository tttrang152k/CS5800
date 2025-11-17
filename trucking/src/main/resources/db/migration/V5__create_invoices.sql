CREATE TABLE IF NOT EXISTS invoices (
                                        id         VARCHAR(64) PRIMARY KEY,
    load_id    VARCHAR(64) NOT NULL UNIQUE,
    issued_at  TIMESTAMP,
    status     VARCHAR(32),
    subtotal   NUMERIC(12,2),
    tax        NUMERIC(12,2),
    total      NUMERIC(12,2),
    customer_ref VARCHAR(128)
    );

CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(status);
