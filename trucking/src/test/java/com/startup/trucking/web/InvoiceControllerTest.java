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
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Invoice Controller Unit Tests")
class InvoiceControllerTest {

    @Mock
    InvoiceService invoiceService;

    @Mock
    LoadService loadService;

    @Mock
    PaymentService paymentService;

    @Mock
    NotificationService notificationService;

    // ---------------------------------------------------------------------
    // list
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("list() - Populates rows and delivered loads and returns view")
    void test_list_populates_rows_and_deliveredLoads_and_returns_view() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        Invoice invoice = new Invoice();
        invoice.setId("INV-1");
        invoice.setLoadId("L-1");
        invoice.setTotal(new BigDecimal("100.00"));
        when(invoiceService.list()).thenReturn(List.of(invoice));

        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("30.00"));
        when(paymentService.listForInvoice("INV-1")).thenReturn(List.of(payment));

        Load delivered = new Load(
                "L-2", "ACME", "Delivered", 10f,
                null, null, null, null,
                null, null, null, null
        );
        Load notDelivered = new Load(
                "L-3", "ACME", "Dispatched", 10f,
                null, null, null, null,
                null, null, null, null
        );
        when(loadService.listLoads()).thenReturn(List.of(delivered, notDelivered));

        Model model = new ConcurrentModel();
        String view = controller.list(model, "");

        assertEquals("invoices", view);
        assertNotNull(model.getAttribute("rows"));
        assertNotNull(model.getAttribute("deliveredLoads"));
    }

    // ---------------------------------------------------------------------
    // createFromLoad
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createFromLoad() - Creates invoice and optionally notifies customer")
    void test_createFromLoad_triggers_notification_when_params_present() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        Invoice invoice = new Invoice();
        invoice.setId("INV-9");
        invoice.setLoadId("L-9");
        invoice.setCustomerRef("ACME");
        invoice.setTotal(new BigDecimal("12.34"));

        when(invoiceService.createFromLoad("L-9")).thenReturn(invoice);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.createFromLoad("L-9", "EMAIL", "ops@acme.com", redirectAttributes);

        assertEquals("redirect:/invoices", redirect);
        verify(notificationService).sendInvoiceCreated(
                ChannelType.EMAIL,
                "ops@acme.com",
                "ACME",
                "INV-9",
                "L-9",
                "12.34"
        );
        assertTrue(((String) redirectAttributes.getFlashAttributes().get("toast")).contains("notified"));
    }

    // ---------------------------------------------------------------------
    // createManualOrFromLoad
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createManualOrFromLoad() - Uses amount when provided and skips notification when params missing")
    void test_createManualOrFromLoad_uses_amount_when_given_and_no_notify_when_missing_params() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        Invoice invoice = new Invoice();
        invoice.setId("INV-22");
        invoice.setLoadId("L-22");
        invoice.setCustomerRef("ACME");
        invoice.setTotal(new BigDecimal("50.00"));

        when(invoiceService.createManual("L-22", 25.00f)).thenReturn(invoice);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.createManualOrFromLoad(
                "L-22",
                new BigDecimal("25.00"),
                null,
                null,
                redirectAttributes
        );

        assertEquals("redirect:/invoices", redirect);
        verify(invoiceService).createManual("L-22", 25.00f);
        verifyNoInteractions(notificationService);
        assertTrue(((String) redirectAttributes.getFlashAttributes().get("toast")).contains("created."));
    }

    // ---------------------------------------------------------------------
    // send
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("send() - Marks invoice sent and redirects to /invoices")
    void test_send_marks_invoice_and_redirects() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.send("INV-10", redirectAttributes);

        assertEquals("redirect:/invoices", redirect);
        verify(invoiceService).markSent("INV-10");
        assertTrue(((String) redirectAttributes.getFlashAttributes().get("toast")).contains("INV-10"));
    }

    // ---------------------------------------------------------------------
    // payPage
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("payPage() - Populates payment model and returns view")
    void test_payPage_populates_model_and_returns_view() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        Invoice invoice = new Invoice();
        invoice.setId("INV-3");
        invoice.setTotal(new BigDecimal("100.00"));
        when(invoiceService.get("INV-3")).thenReturn(invoice);

        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("30.00"));
        when(paymentService.listForInvoice("INV-3")).thenReturn(List.of(payment));

        Model model = new ConcurrentModel();
        String view = controller.payPage("INV-3", model, null);

        assertEquals("invoice-pay", view);
        assertNotNull(model.getAttribute("invoice"));
        assertNotNull(model.getAttribute("balance"));
        assertNotNull(model.getAttribute("methods"));
    }

    // ---------------------------------------------------------------------
    // pay
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("pay() - Records payment and optionally notifies customer")
    void test_pay_calls_service_and_optionally_notifies() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        Payment payment = new Payment();
        payment.setId("PAY-1");
        when(paymentService.pay("INV-1", PaymentMethod.CASH, new BigDecimal("10.00"), "DEMO"))
                .thenReturn(payment);

        Invoice invoice = new Invoice();
        invoice.setId("INV-1");
        invoice.setCustomerRef("ACME");
        when(invoiceService.get("INV-1")).thenReturn(invoice);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.pay(
                "INV-1",
                "CASH",
                new BigDecimal("10.00"),
                "EMAIL",
                "ops@acme.com",
                redirectAttributes
        );

        assertTrue(redirect.startsWith("redirect:/invoices/INV-1/pay/confirm"));
        verify(notificationService).sendPaymentReceived(
                ChannelType.EMAIL,
                "ops@acme.com",
                "ACME",
                "INV-1",
                "10.00",
                "CASH"
        );
    }

    @Test
    @DisplayName("pay() - Records payment and skips notification when params missing")
    void test_pay_without_notify_params_skips_notification() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        Payment payment = new Payment();
        payment.setId("PAY-2");
        when(paymentService.pay("INV-2", PaymentMethod.ACH, new BigDecimal("5.00"), "DEMO"))
                .thenReturn(payment);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.pay(
                "INV-2",
                "ACH",
                new BigDecimal("5.00"),
                "",
                "",
                redirectAttributes
        );

        assertTrue(redirect.contains("paymentId=PAY-2"));
        verifyNoInteractions(notificationService);
        assertTrue(((String) redirectAttributes.getFlashAttributes().get("toast"))
                .contains("Payment recorded."));
    }

    // ---------------------------------------------------------------------
    // confirm
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("confirm() - Populates confirmation model and returns view")
    void test_confirm_populates_model_and_returns_view() {
        InvoiceController controller =
                new InvoiceController(invoiceService, loadService, paymentService, notificationService);

        Invoice invoice = new Invoice();
        invoice.setId("INV-5");
        invoice.setTotal(new BigDecimal("100.00"));

        Payment payment = new Payment();
        payment.setId("PAY-5");
        payment.setAmount(new BigDecimal("40.00"));

        when(invoiceService.get("INV-5")).thenReturn(invoice);
        when(paymentService.get("PAY-5")).thenReturn(payment);
        when(paymentService.listForInvoice("INV-5")).thenReturn(List.of(payment));

        Model model = new ConcurrentModel();
        String view = controller.confirm("INV-5", "PAY-5", model);

        assertEquals("payment-confirmation", view);
        assertSame(invoice, model.getAttribute("invoice"));
        assertSame(payment, model.getAttribute("payment"));
        assertEquals(new BigDecimal("40.00"), model.getAttribute("paid"));
        assertEquals(new BigDecimal("60.00"), model.getAttribute("balance"));
    }
}
