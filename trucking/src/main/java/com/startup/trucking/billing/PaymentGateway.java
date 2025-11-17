package com.startup.trucking.billing;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface PaymentGateway {
    PaymentReceipt charge(String invoiceId, BigDecimal amount, String reference);
    String name();

    record PaymentReceipt(String provider, String authCode,
                          BigDecimal amount, OffsetDateTime at, String status) {}
}
