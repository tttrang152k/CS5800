package com.startup.trucking.service;

import com.startup.trucking.billing.tax.TaxStrategyResolver;
import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("Invoice Service Unit Tests")
class InvoiceServiceTest {

    private static final String EXISTING_INVOICE_ID = "INV-000123";
    private static final String NEW_INVOICE_ID = "INV-000124";
    private static final String LOAD_ID = "L-10";
    private static final String CUSTOMER_ACME = "ACME";

    @Mock
    InvoiceRepository repo;

    @Mock
    LoadService loadService;

    @Mock
    TaxStrategyResolver taxResolver;

    InvoiceService svc;

    @BeforeEach
    void setup() {
        Invoice existing = new Invoice();
        existing.setId(EXISTING_INVOICE_ID);
        when(repo.findAll()).thenReturn(List.of(existing));

        lenient().when(repo.findById(anyString())).thenReturn(Optional.empty());
        lenient().when(taxResolver.compute(any(Load.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.ZERO);

        svc = new InvoiceService(repo, loadService, taxResolver);
    }

    // ---------------------------------------------------------------------
    // list + get
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("list() & get() - Delegate to repository")
    void test_list_and_get_delegate_to_repository() {
        when(repo.findAll()).thenReturn(List.of(new Invoice()));
        assertEquals(1, svc.list().size());

        Invoice inv = new Invoice();
        inv.setId("INV-1");
        when(repo.findById("INV-1")).thenReturn(Optional.of(inv));

        assertEquals(inv, svc.get("INV-1"));
    }

    @Test
    @DisplayName("get() - Missing invoice throws exception")
    void test_get_throws_when_missing() {
        when(repo.findById("INV-X")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.get("INV-X")
        );

        assertTrue(ex.getMessage().contains("Invoice not found"));
    }

    // ---------------------------------------------------------------------
    // createFromLoad
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("get() - Missing invoice throws exception")
    void test_createFromLoad_rejects_if_not_delivered() {
        when(repo.findByLoadId("L-11")).thenReturn(Optional.empty());

        Load notDelivered = new Load(
                "L-11", CUSTOMER_ACME, "Dispatched", 1000f,
                null, null, null, null,
                null, null, null, null
        );
        when(loadService.getLoad("L-11")).thenReturn(notDelivered);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.createFromLoad("L-11")
        );

        assertTrue(ex.getMessage().contains("Load must be Delivered"));
    }

    @Test
    @DisplayName("createFromLoad() - Rejects when invoice already exists for load")
    void test_createFromLoad_rejects_if_duplicate() {
        when(repo.findByLoadId("L-12")).thenReturn(Optional.of(new Invoice()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.createFromLoad("L-12")
        );

        assertTrue(ex.getMessage().contains("already exists"));
    }

    // ---------------------------------------------------------------------
    // createManual
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createFromLoad() - Rejects when invoice already exists for load")
    void test_createManual_creates_invoice_with_manual_amount_and_tax() {
        String manualLoadId = "L-20";
        when(repo.findByLoadId(manualLoadId)).thenReturn(Optional.empty());

        Load load = new Load(
                manualLoadId, CUSTOMER_ACME, "Delivered", 0f,
                null, null, null, null,
                null, null, null, null
        );
        float amount = 500.0f;
        BigDecimal subtotal = BigDecimal.valueOf(amount).setScale(2);
        BigDecimal tax = new BigDecimal("5.00");

        when(loadService.getLoad(manualLoadId)).thenReturn(load);
        when(taxResolver.compute(load, subtotal)).thenReturn(tax);
        when(repo.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Invoice created = svc.createManual(manualLoadId, amount);

        assertEquals(NEW_INVOICE_ID, created.getId());
        assertEquals(manualLoadId, created.getLoadId());
        assertEquals(CUSTOMER_ACME, created.getCustomerRef());
        assertEquals(subtotal, created.getSubtotal());
        assertEquals(tax, created.getTax());
        assertEquals(subtotal.add(tax), created.getTotal());
        assertEquals("Draft", created.getStatus());
        assertNotNull(created.getIssuedAt());

        verify(taxResolver).compute(load, subtotal);
        verify(repo).save(created);
    }

    // ---------------------------------------------------------------------
    // markSent
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("markSent() - Sets status to Sent and saves invoice")
    void test_markSent_sets_status_and_saves() {
        Invoice inv = new Invoice();
        inv.setId("INV-9");
        inv.setStatus("Draft");

        when(repo.findById("INV-9")).thenReturn(Optional.of(inv));

        svc.markSent("INV-9");

        assertEquals("Sent", inv.getStatus());
        verify(repo).save(inv);
    }

    // ---------------------------------------------------------------------
    // findByLoadId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("findByLoadId() - Delegates to repository")
    void test_findByLoadId_delegates() {
        svc.findByLoadId("L-1");
        verify(repo).findByLoadId("L-1");
    }
}
