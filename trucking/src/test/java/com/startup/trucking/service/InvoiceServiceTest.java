package com.startup.trucking.service;

import com.startup.trucking.billing.tax.TaxStrategyResolver;
import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.Invoice;
import com.startup.trucking.persistence.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class InvoiceServiceTest {

    @Mock InvoiceRepository repo;
    @Mock LoadService loads;
    @Mock TaxStrategyResolver taxResolver;

    InvoiceService svc;

    @BeforeEach
    void setup() {
        Invoice existing = new Invoice();
        existing.setId("INV-000123");
        when(repo.findAll()).thenReturn(List.of(existing));

        lenient().when(taxResolver.compute(any(Load.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.ZERO);

        svc = new InvoiceService(repo, loads, taxResolver);
    }

    @Test
    void test_list_and_get_delegate_to_repository() {
        when(repo.findAll()).thenReturn(List.of(new Invoice()));
        assertEquals(1, svc.list().size());

        Invoice inv = new Invoice();
        inv.setId("INV-1");
        when(repo.findById("INV-1")).thenReturn(Optional.of(inv));
        assertEquals(inv, svc.get("INV-1"));
    }

    @Test
    void test_get_throws_when_missing() {
        when(repo.findById("INV-X")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.get("INV-X"));
        assertTrue(ex.getMessage().contains("Invoice not found"));
    }

    @Test
    void test_createFromLoad_rejects_if_not_delivered() {
        when(repo.findByLoadId("L-11")).thenReturn(Optional.empty());
        Load notDelivered = new Load("L-11", "ACME", "Dispatched", 1000f, null, null, null, null,
                null, null, null, null);
        when(loads.getLoad("L-11")).thenReturn(notDelivered);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.createFromLoad("L-11"));
        assertTrue(ex.getMessage().contains("Load must be Delivered"));
    }

    @Test
    void test_createFromLoad_rejects_if_duplicate() {
        when(repo.findByLoadId("L-12")).thenReturn(Optional.of(new Invoice()));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> svc.createFromLoad("L-12"));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void test_markSent_sets_status_and_saves() {
        Invoice inv = new Invoice();
        inv.setId("INV-9");
        inv.setStatus("Draft");
        when(repo.findById("INV-9")).thenReturn(Optional.of(inv));

        svc.markSent("INV-9");

        assertEquals("Sent", inv.getStatus());
        verify(repo).save(inv);
    }

    @Test
    void test_findByLoadId_delegates() {
        svc.findByLoadId("L-1");
        verify(repo).findByLoadId("L-1");
    }
}
