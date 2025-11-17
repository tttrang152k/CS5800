-- src/main/resources/db/migration/V2__loads_data.sql
INSERT INTO loads (id, reference_no, status, rate_amount, tracking_id, rate_confirmation_ref,
                   driver_id, trailer_id, pickup_address, delivery_address, pickup_date, delivery_date)
VALUES
    ('L-1000','American Transportation','Dispatched',1500.00,'TRK-1','http://rc.pdf','EMP-42','TRL-77',
     '123 Pickup St, LA, CA','456 Delivery Ave, LV, NV','2025-10-01','2025-10-05'),
    ('L-1001','Landstar System, Inc','Delivered',2100.00,'TRK-2','http://rc2.pdf','EMP-43','TRL-78',
     '10 Port Rd, SF, CA','99 Warehouse Way, PHX, AZ','2025-10-02','2025-10-06');
