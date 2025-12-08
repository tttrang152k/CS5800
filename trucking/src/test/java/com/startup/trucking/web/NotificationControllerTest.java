package com.startup.trucking.web;

import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Controller Unit Tests")
class NotificationControllerTest {

    private static final String LOAD_ID = "L-1";
    private static final String CUSTOMER_ACME = "ACME";
    private static final String INVOICE_ID = "INV-1";

    @Mock
    NotificationService notificationService;

    @Test
    @DisplayName("notifyLoadConfirmed() - Redirects and calls service")
    void notifyLoadConfirmed_redirects_andCallsService() {
        NotificationController controller = new NotificationController(notificationService);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirect = controller.notifyLoadConfirmed(
                LOAD_ID,
                CUSTOMER_ACME,
                "EMAIL",
                "ops@acme.com",
                redirectAttributes
        );

        assertEquals("redirect:/loads/" + LOAD_ID, redirect);
        verify(notificationService).sendLoadConfirmed(
                ChannelType.EMAIL,
                "ops@acme.com",
                CUSTOMER_ACME,
                LOAD_ID
        );
    }

    @Test
    @DisplayName("notifyInvoiceCreated() - Redirects and calls service")
    void notifyInvoiceCreated_redirects_andCallsService() {
        NotificationController controller = new NotificationController(notificationService);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirect = controller.notifyInvoiceCreated(
                INVOICE_ID,
                LOAD_ID,
                CUSTOMER_ACME,
                "100.00",
                "SMS",
                "15551230000",
                redirectAttributes
        );

        assertEquals("redirect:/invoices", redirect);
        verify(notificationService).sendInvoiceCreated(
                ChannelType.SMS,
                "15551230000",
                CUSTOMER_ACME,
                INVOICE_ID,
                LOAD_ID,
                "100.00"
        );
    }

    @Test
    @DisplayName("notifyPaymentReceived() - Redirects and calls service")
    void notifyPaymentReceived_redirects_andCallsService() {
        NotificationController controller = new NotificationController(notificationService);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirect = controller.notifyPaymentReceived(
                INVOICE_ID,
                CUSTOMER_ACME,
                "25.00",
                "CARD",
                "PUSH",
                "token123",
                redirectAttributes
        );

        assertEquals("redirect:/invoices/" + INVOICE_ID + "/pay", redirect);
        verify(notificationService).sendPaymentReceived(
                ChannelType.PUSH,
                "token123",
                CUSTOMER_ACME,
                INVOICE_ID,
                "25.00",
                "CARD"
        );
    }
}
