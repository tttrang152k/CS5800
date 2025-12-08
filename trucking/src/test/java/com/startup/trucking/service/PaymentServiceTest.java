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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Unit Tests")
class PaymentServiceTest {

    private static final String INVOICE_ID = "INV-1";

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    PaymentGatewayRegistry gatewayRegistry;

    @Mock
    PaymentGateway paymentGateway;

    @Mock
    PaymentReceipt paymentReceipt;

    PaymentService service;

    @BeforeEach
    void setUp() {
        service = new PaymentService(paymentRepository, invoiceRepository, gatewayRegistry);
    }

    // ---------------------------------------------------------------------
    // listForInvoice
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listForInvoice() - Returns all payments for given invoice")
    void test_listForInvoice_returns_payments() {
        Payment p1 = new Payment();
        p1.setId("PAY-1");
        Payment p2 = new Payment();
        p2.setId("PAY-2");

        when(paymentRepository.findByInvoiceId(INVOICE_ID)).thenReturn(List.of(p1, p2));

        List<Payment> result = service.listForInvoice(INVOICE_ID);

        assertEquals(2, result.size());
        assertEquals("PAY-1", result.get(0).getId());
        assertEquals("PAY-2", result.get(1).getId());
        verify(paymentRepository).findByInvoiceId(INVOICE_ID);
    }

    // ---------------------------------------------------------------------
    // get
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("get() - Returns existing payment")
    void test_get_returns_when_found() {
        Payment payment = new Payment();
        payment.setId("PAY-1");
        when(paymentRepository.findById("PAY-1")).thenReturn(Optional.of(payment));

        Payment result = service.get("PAY-1");

        assertSame(payment, result);
        verify(paymentRepository).findById("PAY-1");
    }

    @Test
    @DisplayName("get() - Missing payment throws exception")
    void test_get_throws_when_missing() {
        when(paymentRepository.findById("PAY-X")).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.get("PAY-X"));

        assertTrue(ex.getMessage().contains("Payment not found"));
        verify(paymentRepository).findById("PAY-X");
    }

    // ---------------------------------------------------------------------
    // pay - validation
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("pay() - Throws when amount is null")
    void test_pay_throws_when_amount_null() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.pay(INVOICE_ID, PaymentMethod.CARD, null, "R"));

        assertTrue(ex.getMessage().contains("Amount must be positive"));
        verifyNoInteractions(invoiceRepository, paymentRepository, gatewayRegistry);
    }

    @Test
    @DisplayName("pay() - Throws when amount is non-positive")
    void test_pay_throws_when_amount_non_positive() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.pay(INVOICE_ID, PaymentMethod.CARD, BigDecimal.ZERO, "R"));

        assertTrue(ex.getMessage().contains("Amount must be positive"));
        verifyNoInteractions(invoiceRepository, paymentRepository, gatewayRegistry);
    }

    @Test
    @DisplayName("pay() - Missing invoice throws exception")
    void test_pay_throws_when_invoice_not_found() {
        when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.pay(INVOICE_ID, PaymentMethod.CARD, new BigDecimal("10.00"), "R"));

        assertTrue(ex.getMessage().contains("Invoice not found"));
    }

    @Test
    @DisplayName("pay() - Already paid invoice is rejected")
    void test_pay_throws_when_invoice_already_paid() {
        Invoice invoice = new Invoice();
        invoice.setId(INVOICE_ID);
        invoice.setStatus("Paid");
        when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        IllegalStateException ex =
                assertThrows(IllegalStateException.class,
                        () -> service.pay(INVOICE_ID, PaymentMethod.CARD, new BigDecimal("10.00"), "R"));

        assertTrue(ex.getMessage().contains("already Paid"));
        verify(paymentRepository, never()).findByInvoiceId(any());
    }

    @Test
    @DisplayName("pay() - Amount exceeding remaining balance is rejected")
    void test_pay_throws_when_amount_exceeds_remaining() {
        Invoice invoice = new Invoice();
        invoice.setId(INVOICE_ID);
        invoice.setStatus("Draft");
        invoice.setTotal(new BigDecimal("100.00"));
        when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        Payment prior = new Payment();
        prior.setAmount(new BigDecimal("90.00"));
        when(paymentRepository.findByInvoiceId(INVOICE_ID)).thenReturn(List.of(prior));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.pay(INVOICE_ID, PaymentMethod.CARD, new BigDecimal("20.00"), "R"));

        assertTrue(ex.getMessage().contains("exceeds remaining"));
        verifyNoInteractions(gatewayRegistry);
    }

    // ---------------------------------------------------------------------
    // pay - success paths
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("pay() - Marks invoice Partially Paid when not fully paid")
    void test_pay_marks_invoice_partially_paid_when_not_fully_paid() {
        Invoice invoice = new Invoice();
        invoice.setId(INVOICE_ID);
        invoice.setStatus("Draft");
        invoice.setTotal(new BigDecimal("100.00"));
        when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        Payment prior = new Payment();
        prior.setAmount(new BigDecimal("40.00"));
        when(paymentRepository.findByInvoiceId(INVOICE_ID)).thenReturn(List.of(prior));

        when(gatewayRegistry.resolve(PaymentMethod.CASH)).thenReturn(paymentGateway);
        when(paymentGateway.charge(eq(INVOICE_ID), eq(new BigDecimal("20.00")), eq("R")))
                .thenReturn(paymentReceipt);

        when(paymentReceipt.at()).thenReturn(OffsetDateTime.now());
        when(paymentReceipt.authCode()).thenReturn("AUTH-2");
        when(paymentReceipt.status()).thenReturn("OK");
        when(paymentReceipt.provider()).thenReturn("Stripe");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.pay(INVOICE_ID, PaymentMethod.CASH, new BigDecimal("20.00"), "R");

        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());
        Invoice updated = invoiceCaptor.getValue();
        assertEquals("Partially Paid", updated.getStatus());
    }
}
