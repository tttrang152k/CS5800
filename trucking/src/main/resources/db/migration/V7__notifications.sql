CREATE TABLE IF NOT EXISTS notifications (
    id           VARCHAR(64) PRIMARY KEY,
    customer_ref VARCHAR(255),
    type         VARCHAR(64),
    channel      VARCHAR(32),
    recipient    VARCHAR(255),
    message      VARCHAR(2000),
    ref_type     VARCHAR(64),
    ref_id       VARCHAR(128),
    created_at   TIMESTAMP WITH TIME ZONE
);