package com.startup.trucking.service;

import com.startup.trucking.billing.PaymentGateway;
import com.startup.trucking.billing.PaymentGateway.PaymentReceipt;
import com.startup.trucking.billing.PaymentGatewayRegistry;
import com.startup.trucking.billing.PaymentMethod;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.InvoiceRepository;
import com.startup.trucking.persistence.Payment;
import com.startup.trucking.persistence.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock PaymentRepository payments;
    @Mock InvoiceRepository invoices;
    @Mock PaymentGatewayRegistry gateways;
    @Mock PaymentGateway gateway;

    PaymentService service;

    @BeforeEach
    void init() {
        service = new PaymentService(payments, invoices, gateways);
    }

    @Test
    void test_listForInvoice_delegates_to_repository() {
        when(payments.findByInvoiceId("INV-1")).thenReturn(List.of(new Payment()));
        assertEquals(1, service.listForInvoice("INV-1").size());
        verify(payments).findByInvoiceId("INV-1");
    }

    @Test
    void test_get_returns_when_found() {
        Payment p = new Payment();
        p.setId("PAY-1");
        when(payments.findById("PAY-1")).thenReturn(Optional.of(p));
        assertEquals(p, service.get("PAY-1"));
    }

    @Test
    void test_get_throws_when_missing() {
        when(payments.findById("PAY-X")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.get("PAY-X"));
        assertTrue(ex.getMessage().contains("Payment not found"));
    }

    @Test
    void test_pay_success_partial_then_paid_status_updates() {
        Invoice inv = new Invoice();
        inv.setId("INV-100");
        inv.setTotal(new BigDecimal("100.00"));
        inv.setStatus("Sent");
        when(invoices.findById("INV-100")).thenReturn(Optional.of(inv));

        Payment prior = new Payment();
        prior.setAmount(new BigDecimal("30.00"));
        when(payments.findByInvoiceId("INV-100")).thenReturn(List.of(prior));

        when(gateways.resolve(PaymentMethod.CARD)).thenReturn(gateway);
        PaymentReceipt receipt = new PaymentReceipt("CardGateway", "AUTH-123", new BigDecimal("70.00"),
                OffsetDateTime.now(), "Cleared");
        when(gateway.charge(eq("INV-100"), eq(new BigDecimal("70.00")), eq("DEMO"))).thenReturn(receipt);

        ArgumentCaptor<Payment> savedCap = ArgumentCaptor.forClass(Payment.class);
        when(payments.save(savedCap.capture())).thenAnswer(a -> a.getArgument(0));

        ArgumentCaptor<Invoice> invCap = ArgumentCaptor.forClass(Invoice.class);
        when(invoices.save(invCap.capture())).thenAnswer(a -> a.getArgument(0));

        Payment out = service.pay("INV-100", PaymentMethod.CARD, new BigDecimal("70.0"), "DEMO");

        // payment persisted with scale 2
        Payment saved = savedCap.getValue();
        assertEquals("INV-100", saved.getInvoiceId());
        assertEquals(new BigDecimal("70.00"), saved.getAmount());
        assertEquals("CARD", saved.getMethod());
        assertEquals("Cleared", saved.getStatus());
        assertEquals("CardGateway", saved.getProvider());
        assertEquals("AUTH-123", saved.getAuthCode());
        assertNotNull(saved.getPaidAt());
        assertEquals(out.getId(), saved.getId());

        Invoice savedInv = invCap.getValue();
        assertEquals("Paid", savedInv.getStatus());
    }

    @Test
    void test_pay_success_results_in_partially_paid_when_balance_remaining() {
        Invoice inv = new Invoice();
        inv.setId("INV-200");
        inv.setTotal(new BigDecimal("100.00"));
        inv.setStatus("Sent");
        when(invoices.findById("INV-200")).thenReturn(Optional.of(inv));

        when(payments.findByInvoiceId("INV-200")).thenReturn(List.of());

        when(gateways.resolve(PaymentMethod.ACH)).thenReturn(gateway);
        PaymentReceipt receipt = new PaymentReceipt("AchGateway", "ACH-XYZ", new BigDecimal("20.00"),
                OffsetDateTime.now(), "Cleared");
        when(gateway.charge(eq("INV-200"), eq(new BigDecimal("20.00")), any())).thenReturn(receipt);

        service.pay("INV-200", PaymentMethod.ACH, new BigDecimal("20"), "REF");

        ArgumentCaptor<Invoice> invCap = ArgumentCaptor.forClass(Invoice.class);
        verify(invoices).save(invCap.capture());
        assertEquals("Partially Paid", invCap.getValue().getStatus());
    }

    @Test
    void test_pay_rejects_non_positive_amount() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> service.pay("INV-X", PaymentMethod.CASH, new BigDecimal("0"), "R"));
        assertTrue(ex1.getMessage().contains("positive"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> service.pay("INV-X", PaymentMethod.CASH, new BigDecimal("-1"), "R"));
        assertTrue(ex2.getMessage().contains("positive"));
    }

    @Test
    void test_pay_throws_when_invoice_missing() {
        when(invoices.findById("INV-MISS")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.pay("INV-MISS", PaymentMethod.CASH, new BigDecimal("1.00"), "R"));
        assertTrue(ex.getMessage().contains("Invoice not found"));
    }

    @Test
    void test_pay_throws_when_invoice_already_paid() {
        Invoice inv = new Invoice();
        inv.setId("INV-P");
        inv.setTotal(new BigDecimal("10.00"));
        inv.setStatus("Paid");
        when(invoices.findById("INV-P")).thenReturn(Optional.of(inv));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.pay("INV-P", PaymentMethod.CASH, new BigDecimal("1.00"), "R"));
        assertTrue(ex.getMessage().contains("already Paid"));
    }

    @Test
    void test_pay_throws_when_amount_exceeds_remaining() {
        Invoice inv = new Invoice();
        inv.setId("INV-EX");
        inv.setTotal(new BigDecimal("50.00"));
        inv.setStatus("Sent");
        when(invoices.findById("INV-EX")).thenReturn(Optional.of(inv));

        Payment prior = new Payment();
        prior.setAmount(new BigDecimal("40.00"));
        when(payments.findByInvoiceId("INV-EX")).thenReturn(List.of(prior));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.pay("INV-EX", PaymentMethod.CASH, new BigDecimal("20.00"), "R"));
        assertTrue(ex.getMessage().contains("exceeds remaining"));
    }
}
