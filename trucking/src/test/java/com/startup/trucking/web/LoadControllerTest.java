package com.startup.trucking.web;

import com.startup.trucking.persistence.Document;
import com.startup.trucking.domain.Load;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.service.DocumentService;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import com.startup.trucking.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadControllerTest {

    @Mock LoadService loads;
    @Mock DocumentService docs;
    @Mock InvoiceService invoices;
    @Mock NotificationService notify;

    @Test
    void test_loads_builds_rows_and_returns_view() {
        LoadController ctl = new LoadController(loads, docs, invoices, notify);

        Load l = new Load("L-1", "ACME", "Delivered", 100f, null, null, "EMP-1", null, "PU", "DEL", "D1", "D2");
        when(loads.listLoads()).thenReturn(List.of(l));

        Invoice inv = new Invoice(); inv.setId("INV-1"); inv.setLoadId("L-1");
        when(invoices.list()).thenReturn(List.of(inv));

        // Build JPA Document via setters (no-args ctor)
        Document d = new Document();
        d.setId("DOC-1");
        d.setLoadId("L-1");
        d.setType("BOL");
        d.setStatus("Uploaded");
        d.setFileRef("https://x"); // stored as String
        d.setUploadedAt(OffsetDateTime.now());

        when(docs.list("L-1")).thenReturn(List.of(d));

        Model model = new ConcurrentModel();
        String view = ctl.loads(model);

        assertEquals("loads", view);
        assertNotNull(model.getAttribute("rows"));
    }

    @Test
    void test_detail_populates_model_and_returns_view() {
        LoadController ctl = new LoadController(loads, docs, invoices, notify);
        Load l = new Load("L-2", "ACME", "Requested", 0f, null, null, null, null, null, null, null, null);
        when(loads.getLoad("L-2")).thenReturn(l);
        when(docs.list("L-2")).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String view = ctl.detail("L-2", model, "");
        assertEquals("load-detail", view);
        assertNotNull(model.getAttribute("load"));
        assertNotNull(model.getAttribute("documents"));
    }

    @Test
    void test_changeStatus_updates_and_redirects() {
        LoadController ctl = new LoadController(loads, docs, invoices, notify);
        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.changeStatus("L-5", "Delivered", ra);
        verify(loads).updateStatus("L-5", "Delivered");
        assertEquals("redirect:/loads/L-5", redirect);
    }

    @Test
    void test_notifyCustomer_skips_when_missing_params() {
        LoadController ctl = new LoadController(loads, docs, invoices, notify);
        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.notifyCustomer("L-6", "", "", ra);
        assertEquals("redirect:/loads/L-6", redirect);
        verifyNoInteractions(notify);
    }

    @Test
    void test_notifyCustomer_sends_when_present() {
        LoadController ctl = new LoadController(loads, docs, invoices, notify);
        Load l = new Load("L-7", "ACME", "Requested", 0f, null, null, null, null, null, null, null, null);
        when(loads.getLoad("L-7")).thenReturn(l);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.notifyCustomer("L-7", "EMAIL", "ops@acme.com", ra);

        assertEquals("redirect:/loads/L-7", redirect);
        verify(notify).sendLoadConfirmed(ChannelType.EMAIL, "ops@acme.com", "ACME", "L-7");
    }

    @Test
    void test_uploadDoc_calls_service_and_redirects() {
        LoadController ctl = new LoadController(loads, docs, invoices, notify);
        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.uploadDoc("L-8", "BOL", "https://x", ra);
        verify(docs).upload("L-8", "BOL", URI.create("https://x"));
        assertEquals("redirect:/loads/L-8", redirect);
    }

    @Test
    void test_create_builds_load_and_saves_and_redirects() {
        LoadController ctl = new LoadController(loads, docs, invoices, notify);
        BookingForm form = new BookingForm();
        form.setId("L-10");
        form.setReferenceNo("ACME");
        form.setRateAmount(123.45f);

        RedirectAttributes ra = new RedirectAttributesModelMap();
        String redirect = ctl.create(form, ra);

        verify(loads).putLoad(ArgumentMatchers.any());
        assertEquals("redirect:/loads", redirect);
    }
}
