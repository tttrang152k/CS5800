package com.startup.trucking.billing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentGatewayRegistryTest {

    @Test
    void test_resolve_returnsCardGateway_forCARD() {
        var reg = new PaymentGatewayRegistry();
        var gw = reg.resolve(PaymentMethod.CARD);
        assertNotNull(gw);
        assertEquals("CardGateway", gw.name());
        assertTrue(gw instanceof CardGatewayAdapter);
    }

    @Test
    void test_resolve_returnsAchGateway_forACH() {
        var reg = new PaymentGatewayRegistry();
        var gw = reg.resolve(PaymentMethod.ACH);
        assertNotNull(gw);
        assertEquals("AchGateway", gw.name());
        assertTrue(gw instanceof AchGatewayAdapter);
    }

    @Test
    void test_resolve_returnsCashGateway_forCASH() {
        var reg = new PaymentGatewayRegistry();
        var gw = reg.resolve(PaymentMethod.CASH);
        assertNotNull(gw);
        assertEquals("CashGateway", gw.name());
        assertTrue(gw instanceof CashGatewayAdapter);
    }

    @Test
    void test_resolve_treatsCHECK_asCashGateway() {
        var reg = new PaymentGatewayRegistry();
        var gw = reg.resolve(PaymentMethod.CHECK);
        assertNotNull(gw);
        assertEquals("CashGateway", gw.name());
        assertTrue(gw instanceof CashGatewayAdapter);
    }

    @Test
    void test_resolve_throwsForNull() {
        var reg = new PaymentGatewayRegistry();
        var ex = assertThrows(IllegalArgumentException.class, () -> reg.resolve(null));
        assertTrue(ex.getMessage().contains("Unsupported method"));
    }
}
