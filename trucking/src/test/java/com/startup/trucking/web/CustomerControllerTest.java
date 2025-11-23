package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.Notification;
import com.startup.trucking.persistence.NotificationRepository;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock LoadService loadService;
    @Mock InvoiceService invoiceService;
    @Mock NotificationRepository notifications;

    @Test
    void test_detail_filters_loads_and_invoices_and_returns_view() {
        CustomerController ctl = new CustomerController(loadService, invoiceService, notifications);

        Load l1 = new Load("L-1", "ACME", "Requested", 100f, null, null, null, null, null, null, null, null);
        Load l2 = new Load("L-2", "OTHER", "Delivered", 200f, null, null, null, null, null, null, null, null);
        when(loadService.listLoads()).thenReturn(List.of(l1, l2));

        Invoice i1 = new Invoice(); i1.setId("INV-1"); i1.setCustomerRef("ACME"); i1.setStatus("Sent"); i1.setTotal(new BigDecimal("10.00"));
        Invoice i2 = new Invoice(); i2.setId("INV-2"); i2.setCustomerRef("ACME"); i2.setStatus("Draft"); i2.setTotal(new BigDecimal("20.00"));
        Invoice i3 = new Invoice(); i3.setId("INV-3"); i3.setCustomerRef("OTHER"); i3.setStatus("Sent"); i3.setTotal(new BigDecimal("30.00"));
        when(invoiceService.list()).thenReturn(List.of(i1, i2, i3));

        when(notifications.findByCustomerRefOrderByCreatedAtDesc("ACME")).thenReturn(List.of(new Notification()));

        Model model = new ConcurrentModel();
        String view = ctl.detail("ACME", model);

        assertEquals("customer-detail", view);
        assertEquals("ACME", model.getAttribute("customerRef"));

        @SuppressWarnings("unchecked")
        List<Load> loads = (List<Load>) model.getAttribute("loads");
        assertEquals(1, loads.size());
        assertEquals("L-1", loads.get(0).getId());

        @SuppressWarnings("unchecked")
        List<Invoice> invoices = (List<Invoice>) model.getAttribute("invoices");
        assertEquals(1, invoices.size());
        assertEquals("INV-1", invoices.get(0).getId());

        verify(notifications).findByCustomerRefOrderByCreatedAtDesc("ACME");
    }
}
