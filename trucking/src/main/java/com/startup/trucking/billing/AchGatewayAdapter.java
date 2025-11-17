package com.startup.trucking.billing;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AchGatewayAdapter implements PaymentGateway {
    @Override
    public PaymentReceipt charge(String invoiceId, BigDecimal amount, String reference) {
        return new PaymentReceipt(name(), "ACH-" + UUID.randomUUID(), amount, OffsetDateTime.now(), "Cleared");
    }
    @Override public String name() { return "AchGateway"; }
}
