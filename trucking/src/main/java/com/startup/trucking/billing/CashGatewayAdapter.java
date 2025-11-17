package com.startup.trucking.billing;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CashGatewayAdapter implements PaymentGateway {
    @Override
    public PaymentReceipt charge(String invoiceId, BigDecimal amount, String reference) {
        return new PaymentReceipt(name(), reference != null ? reference : "CASH", amount, OffsetDateTime.now(), "Cleared");
    }
    @Override public String name() { return "CashGateway"; }
}
