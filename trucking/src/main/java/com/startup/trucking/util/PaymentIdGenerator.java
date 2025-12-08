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

    public String nextId() {
        return "PAY-" + UUID.randomUUID();
    }
}
