package com.startup.trucking.billing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CardGatewayAdapterTest {

    @Test
    void test_name_returnsCardGateway() {
        var gw = new CardGatewayAdapter();
        assertEquals("CardGateway", gw.name());
    }

    @Test
    void test_charge_returnsClearedReceipt_withAuthPrefixAUTH() {
        var gw = new CardGatewayAdapter();
        var before = OffsetDateTime.now().minusSeconds(2);

        var r = gw.charge("INV-1", new BigDecimal("123.45"), "REF-123");

        assertNotNull(r);
        assertEquals(gw.name(), r.provider());
        assertTrue(r.authCode().startsWith("AUTH-"), "auth code should start with AUTH-");
        assertEquals(new BigDecimal("123.45"), r.amount());
        assertEquals("Cleared", r.status());
        assertNotNull(r.at());
        assertTrue(!r.at().isBefore(before) && !r.at().isAfter(OffsetDateTime.now().plusSeconds(1)));
    }
}
