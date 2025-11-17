CREATE TABLE loads (
                       id                    VARCHAR(64) PRIMARY KEY,
                       reference_no          VARCHAR(128),
                       status                VARCHAR(32),
                       rate_amount           NUMERIC(12,2),
                       tracking_id           VARCHAR(64),
                       rate_confirmation_ref VARCHAR(512),
                       driver_id             VARCHAR(64),
                       trailer_id            VARCHAR(64),
                       pickup_address        VARCHAR(512),
                       delivery_address      VARCHAR(512),
                       pickup_date           VARCHAR(32),
                       delivery_date         VARCHAR(32)
);

CREATE INDEX idx_loads_status ON loads(status);
CREATE INDEX idx_loads_driver  ON loads(driver_id);
