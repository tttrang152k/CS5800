package com.startup.trucking.web;

import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock NotificationService notify;

    @Test
    void test_loadConfirmed_redirects_and_calls_service() {
        NotificationController ctl = new NotificationController(notify);
        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.loadConfirmed("L-1", "ACME", "EMAIL", "ops@acme.com", ra);

        assertEquals("redirect:/loads/L-1", redirect);
        verify(notify).sendLoadConfirmed(ChannelType.EMAIL, "ops@acme.com", "ACME", "L-1");
    }

    @Test
    void test_invoiceCreated_redirects_and_calls_service() {
        NotificationController ctl = new NotificationController(notify);
        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.invoiceCreated("INV-1", "L-1", "ACME", "100.00", "SMS", "15551230000", ra);

        assertEquals("redirect:/invoices", redirect);
        verify(notify).sendInvoiceCreated(ChannelType.SMS, "15551230000", "ACME", "INV-1", "L-1", "100.00");
    }

    @Test
    void test_paymentReceived_redirects_and_calls_service() {
        NotificationController ctl = new NotificationController(notify);
        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.paymentReceived("INV-1", "ACME", "25.00", "CARD", "PUSH", "token123", ra);

        assertEquals("redirect:/invoices/INV-1/pay", redirect);
        verify(notify).sendPaymentReceived(ChannelType.PUSH, "token123", "ACME", "INV-1", "25.00", "CARD");
    }
}
