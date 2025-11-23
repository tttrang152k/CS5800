package com.startup.trucking.web;

import com.startup.trucking.billing.PaymentMethod;
import com.startup.trucking.domain.Load;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.Payment;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import com.startup.trucking.service.NotificationService;
import com.startup.trucking.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock InvoiceService billing;
    @Mock LoadService loads;
    @Mock PaymentService payments;
    @Mock NotificationService notify;

    @Test
    void test_list_populates_rows_and_deliveredLoads_and_returns_view() {
        InvoiceController ctl = new InvoiceController(billing, loads, payments, notify);

        Invoice inv = new Invoice(); inv.setId("INV-1"); inv.setLoadId("L-1"); inv.setTotal(new BigDecimal("100.00"));
        when(billing.list()).thenReturn(List.of(inv));
        Payment p = new Payment(); p.setAmount(new BigDecimal("30.00"));
        when(payments.listForInvoice("INV-1")).thenReturn(List.of(p));

        Load lDelivered = new Load("L-2", "ACME", "Delivered", 10f, null, null, null, null, null, null, null, null);
        Load lNot = new Load("L-3", "ACME", "Dispatched", 10f, null, null, null, null, null, null, null, null);
        when(loads.listLoads()).thenReturn(List.of(lDelivered, lNot));
        when(billing.list()).thenReturn(List.of(inv));

        Model model = new ConcurrentModel();
        String view = ctl.list(model, "");
        assertEquals("invoices", view);

        assertNotNull(model.getAttribute("rows"));
        assertNotNull(model.getAttribute("deliveredLoads"));
    }

    @Test
    void test_createFromLoad_triggers_notification_when_params_present() {
        InvoiceController ctl = new InvoiceController(billing, loads, payments, notify);
        Invoice inv = new Invoice(); inv.setId("INV-9"); inv.setLoadId("L-9"); inv.setCustomerRef("ACME"); inv.setTotal(new BigDecimal("12.34"));
        when(billing.createFromLoad("L-9")).thenReturn(inv);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.createFromLoad("L-9", "EMAIL", "ops@acme.com", ra);

        assertEquals("redirect:/invoices", redirect);
        verify(notify).sendInvoiceCreated(ChannelType.EMAIL, "ops@acme.com", "ACME", "INV-9", "L-9", "12.34");
        assertTrue(((String) ra.getFlashAttributes().get("toast")).contains("notified"));
    }

    @Test
    void test_createManualOrFromLoad_uses_amount_when_given_and_no_notify_when_missing_params() {
        InvoiceController ctl = new InvoiceController(billing, loads, payments, notify);
        Invoice inv = new Invoice(); inv.setId("INV-22"); inv.setLoadId("L-22"); inv.setCustomerRef("ACME"); inv.setTotal(new BigDecimal("50.00"));
        when(billing.createManual("L-22", 25.00f)).thenReturn(inv);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.createManualOrFromLoad("L-22", new BigDecimal("25.00"), null, null, ra);

        assertEquals("redirect:/invoices", redirect);
        verify(billing).createManual("L-22", 25.00f);
        verifyNoInteractions(notify);
        assertTrue(((String) ra.getFlashAttributes().get("toast")).contains("created."));
    }

    @Test
    void test_pay_calls_service_and_optionally_notifies() {
        InvoiceController ctl = new InvoiceController(billing, loads, payments, notify);

        Payment p = new Payment(); p.setId("PAY-1");
        when(payments.pay("INV-1", PaymentMethod.CASH, new BigDecimal("10.00"), "DEMO")).thenReturn(p);

        Invoice inv = new Invoice(); inv.setId("INV-1"); inv.setCustomerRef("ACME");
        when(billing.get("INV-1")).thenReturn(inv);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.pay("INV-1", "CASH", new BigDecimal("10.00"), "EMAIL", "ops@acme.com", ra);

        assertTrue(redirect.startsWith("redirect:/invoices/INV-1/pay/confirm"));
        verify(notify).sendPaymentReceived(ChannelType.EMAIL, "ops@acme.com", "ACME", "INV-1", "10.00", "CASH");
    }

    @Test
    void test_pay_without_notify_params_skips_notification() {
        InvoiceController ctl = new InvoiceController(billing, loads, payments, notify);

        Payment p = new Payment(); p.setId("PAY-2");
        when(payments.pay("INV-2", PaymentMethod.ACH, new BigDecimal("5.00"), "DEMO")).thenReturn(p);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.pay("INV-2", "ACH", new BigDecimal("5.00"), "", "", ra);

        assertTrue(redirect.contains("paymentId=PAY-2"));
        verifyNoInteractions(notify);
    }

    @Test
    void test_payPage_populates_model_and_returns_view() {
        InvoiceController ctl = new InvoiceController(billing, loads, payments, notify);
        Invoice inv = new Invoice(); inv.setId("INV-3"); inv.setTotal(new BigDecimal("100.00"));
        when(billing.get("INV-3")).thenReturn(inv);
        Payment p = new Payment(); p.setAmount(new BigDecimal("30.00"));
        when(payments.listForInvoice("INV-3")).thenReturn(List.of(p));

        Model model = new ConcurrentModel();
        String view = ctl.payPage("INV-3", model, null);
        assertEquals("invoice-pay", view);
        assertNotNull(model.getAttribute("invoice"));
        assertNotNull(model.getAttribute("balance"));
        assertNotNull(model.getAttribute("methods"));
    }
}
