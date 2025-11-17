CREATE TABLE IF NOT EXISTS payments (
                                        id          VARCHAR(64) PRIMARY KEY,
    invoice_id  VARCHAR(64) NOT NULL,
    amount      NUMERIC(12,2) NOT NULL,
    method      VARCHAR(32) NOT NULL,
    paid_at     TIMESTAMP NOT NULL,
    reference   VARCHAR(128),
    status      VARCHAR(32) NOT NULL,
    provider    VARCHAR(64),
    auth_code   VARCHAR(64)
    );

CREATE INDEX IF NOT EXISTS idx_payments_invoice ON payments(invoice_id);

ALTER TABLE payments
    ADD CONSTRAINT IF NOT EXISTS fk_payments_invoice
    FOREIGN KEY (invoice_id) REFERENCES invoices(id);
