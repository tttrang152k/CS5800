package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.persistence.Document;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.service.DocumentService;
import com.startup.trucking.service.InvoiceService;
import com.startup.trucking.service.LoadService;
import com.startup.trucking.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Load Controller Unit Tests")
class LoadControllerTest {

    private static final String LOAD_ID = "L-1";
    private static final String CUSTOMER_ACME = "ACME";

    @Mock
    LoadService loadService;

    @Mock
    DocumentService documentService;

    @Mock
    InvoiceService invoiceService;

    @Mock
    NotificationService notificationService;

    @Test
    @DisplayName("listLoads() - Builds rows and returns view")
    void listLoads_buildsRows_andReturnsView() {
        LoadController controller =
                new LoadController(loadService, documentService, invoiceService, notificationService);

        Load load = new Load(
                LOAD_ID,
                CUSTOMER_ACME,
                "Delivered",
                100f,
                null,
                null,
                "EMP-1",
                null,
                "PU",
                "DEL",
                "2025-01-01",
                "2025-01-02"
        );
        when(loadService.listLoadsSorted("CustomerNameAsc")).thenReturn(List.of(load));

        Invoice invoice = new Invoice();
        invoice.setId("INV-1");
        invoice.setLoadId(LOAD_ID);
        when(invoiceService.list()).thenReturn(List.of(invoice));

        Document document = new Document();
        document.setId("DOC-1");
        document.setLoadId(LOAD_ID);
        document.setType("BOL");
        document.setStatus("Uploaded");
        document.setFileRef("https://x");
        document.setUploadedAt(OffsetDateTime.now());
        when(documentService.list(LOAD_ID)).thenReturn(List.of(document));

        Model model = new ConcurrentModel();
        String viewName = controller.listLoads("CustomerNameAsc", model);

        assertEquals("loads", viewName);
        assertNotNull(model.getAttribute("rows"));
        assertEquals("CustomerNameAsc", model.getAttribute("currentSort"));
        verify(loadService).listLoadsSorted("CustomerNameAsc");
    }

    @Test
    @DisplayName("loadDetail() - Populates model and returns view")
    void loadDetail_populatesModel_andReturnsView() {
        LoadController controller =
                new LoadController(loadService, documentService, invoiceService, notificationService);

        Load load = new Load(
                "L-2",
                CUSTOMER_ACME,
                "Requested",
                0f,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(loadService.getLoad("L-2")).thenReturn(load);
        when(documentService.list("L-2")).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String viewName = controller.loadDetail("L-2", model, "");

        assertEquals("load-detail", viewName);
        assertNotNull(model.getAttribute("load"));
        assertNotNull(model.getAttribute("documents"));
    }

    @Test
    @DisplayName("changeStatus() - Updates status and redirects to load detail")
    void changeStatus_updatesStatus_andRedirects() {
        LoadController controller =
                new LoadController(loadService, documentService, invoiceService, notificationService);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.changeStatus("L-5", "Delivered", redirectAttributes);

        verify(loadService).updateStatus("L-5", "Delivered");
        assertEquals("redirect:/loads/L-5", redirect);
    }

    @Test
    @DisplayName("notifyCustomer() - Skips when params missing")
    void notifyCustomer_skips_whenChannelOrRecipientMissing() {
        LoadController controller =
                new LoadController(loadService, documentService, invoiceService, notificationService);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.notifyCustomer("L-6", "", "", redirectAttributes);

        assertEquals("redirect:/loads/L-6", redirect);
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("notifyCustomer() - Sends notification when params present")
    void notifyCustomer_sends_whenParamsPresent() {
        LoadController controller =
                new LoadController(loadService, documentService, invoiceService, notificationService);

        Load load = new Load(
                "L-7",
                CUSTOMER_ACME,
                "Requested",
                0f,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(loadService.getLoad("L-7")).thenReturn(load);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.notifyCustomer("L-7", "EMAIL", "ops@acme.com", redirectAttributes);

        assertEquals("redirect:/loads/L-7", redirect);
        verify(notificationService).sendLoadConfirmed(
                ChannelType.EMAIL,
                "ops@acme.com",
                CUSTOMER_ACME,
                "L-7"
        );
    }

    @Test
    @DisplayName("uploadDocument() - Calls service and redirects back to load")
    void uploadDocument_callsService_andRedirects() {
        LoadController controller =
                new LoadController(loadService, documentService, invoiceService, notificationService);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.uploadDocument("L-8", "BOL", "https://x", redirectAttributes);

        verify(documentService).upload("L-8", "BOL", URI.create("https://x"));
        assertEquals("redirect:/loads/L-8", redirect);
    }

    @Test
    @DisplayName("createLoad() - Builds load, saves it and redirects")
    void createLoad_buildsLoad_saves_andRedirects() {
        LoadController controller =
                new LoadController(loadService, documentService, invoiceService, notificationService);

        BookingForm form = new BookingForm();
        form.setId("L-10");
        form.setReferenceNo(CUSTOMER_ACME);
        form.setRateAmount(123.45f);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.createLoad(form, redirectAttributes);

        verify(loadService).putLoad(ArgumentMatchers.any());
        assertEquals("redirect:/loads", redirect);
    }
}
