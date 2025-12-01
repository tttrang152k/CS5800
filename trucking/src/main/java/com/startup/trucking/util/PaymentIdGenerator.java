package com.startup.trucking.util;

import java.util.UUID;

public class PaymentIdGenerator {
    private static volatile PaymentIdGenerator instance;

    private PaymentIdGenerator() { }

    public static PaymentIdGenerator getInstance() {
        PaymentIdGenerator local = instance;
        if (local == null) {
            synchronized (PaymentIdGenerator.class) {
                local = instance;
                if (local == null) {
                    local = new PaymentIdGenerator();
                    instance = local;
                }
            }
        }
        return local;
    }

    /** Produces IDs like PAY-550e8400-e29b-41d4-a716-446655440000 */
    public String nextId() {
        return "PAY-" + UUID.randomUUID();
    }
}
