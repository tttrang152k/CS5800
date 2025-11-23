package com.startup.trucking.billing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AchGatewayAdapterTest {

    @Test
    void test_name_returnsAchGateway() {
        var gw = new AchGatewayAdapter();
        assertEquals("AchGateway", gw.name());
    }

    @Test
    void test_charge_returnsClearedReceipt_withAuthPrefixACH() {
        var gw = new AchGatewayAdapter();
        var before = OffsetDateTime.now().minusSeconds(2);

        var r = gw.charge("INV-99", new BigDecimal("50.00"), "ACH-REF");

        assertNotNull(r);
        assertEquals(gw.name(), r.provider());
        assertTrue(r.authCode().startsWith("ACH-"), "auth code should start with ACH-");
        assertEquals(new BigDecimal("50.00"), r.amount());
        assertEquals("Cleared", r.status());
        assertNotNull(r.at());
        assertTrue(!r.at().isBefore(before) && !r.at().isAfter(OffsetDateTime.now().plusSeconds(1)));
    }
}
