package com.startup.trucking.billing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CashGatewayAdapterTest {

    @Test
    void test_name_returnsCashGateway() {
        var gw = new CashGatewayAdapter();
        assertEquals("CashGateway", gw.name());
    }

    @Test
    void test_charge_usesProvidedReferenceAsAuthCode() {
        var gw = new CashGatewayAdapter();
        var r = gw.charge("INV-7", new BigDecimal("10.00"), "CASH-RECEIPT-123");
        assertEquals("CashGateway", r.provider());
        assertEquals("CASH-RECEIPT-123", r.authCode());
        assertEquals(new BigDecimal("10.00"), r.amount());
        assertEquals("Cleared", r.status());
        assertNotNull(r.at());
    }

    @Test
    void test_charge_defaultsAuthCodeToCASH_whenReferenceNull() {
        var gw = new CashGatewayAdapter();
        var before = OffsetDateTime.now().minusSeconds(2);

        var r = gw.charge("INV-8", new BigDecimal("0.01"), null);

        assertEquals("CashGateway", r.provider());
        assertEquals("CASH", r.authCode());
        assertEquals(new BigDecimal("0.01"), r.amount());
        assertEquals("Cleared", r.status());
        assertTrue(!r.at().isBefore(before) && !r.at().isAfter(OffsetDateTime.now().plusSeconds(1)));
    }
}
