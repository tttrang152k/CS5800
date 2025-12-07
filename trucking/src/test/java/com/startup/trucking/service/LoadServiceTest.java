package com.startup.trucking.service;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.LoadEntity;
import com.startup.trucking.persistence.LoadRepository;
import com.startup.trucking.service.sort.LoadSortStrategyResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {

    @Mock
    LoadRepository repo;

    @Mock
    LoadSortStrategyResolver sortResolver;

    @InjectMocks
    LoadService service;

    @Test
    void test_getLoad_found_mapsToDomain() {
        LoadEntity e = new LoadEntity();
        e.setId("L-1");
        e.setReferenceNo("ACME");
        e.setStatus("Requested");
        e.setRateAmount(new BigDecimal("100.00"));
        when(repo.findById("L-1")).thenReturn(Optional.of(e));

        Load d = service.getLoad("L-1");
        assertEquals("L-1", d.getId());
        assertEquals("ACME", d.getReferenceNo());
        assertEquals("Requested", d.getStatus());
        assertEquals(100.0f, d.getRateAmount(), 0.0001);
    }

    @Test
    void test_getLoad_notFound_throws() {
        when(repo.findById("missing")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.getLoad("missing"));
        assertTrue(ex.getMessage().contains("Load not found"));
    }

    @Test
    void test_putLoad_saves_entity() {
        Load load = new Load("L-2", "ACME", "Requested", 150f, "TRK", "RC", "EMP", "TRL",
                "PU", "DEL", "2025-01-01", "2025-01-02");

        service.putLoad(load);

        ArgumentCaptor<LoadEntity> cap = ArgumentCaptor.forClass(LoadEntity.class);
        verify(repo).save(cap.capture());
        LoadEntity saved = cap.getValue();
        assertEquals("L-2", saved.getId());
        assertEquals("ACME", saved.getReferenceNo());
        assertEquals("Requested", saved.getStatus());
        assertEquals(new BigDecimal("150.00"), saved.getRateAmount());
    }

    @Test
    void test_putLoad_rejects_null_or_blank_id() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> service.putLoad(null));
        assertTrue(ex1.getMessage().contains("load.id"));

        Load bad = new Load("", null, "Requested", 0f,
                null, null, null, null, null, null, null, null);
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> service.putLoad(bad));
        assertTrue(ex2.getMessage().contains("load.id"));
    }

    @Test
    void test_listLoads_mapsAll() {
        LoadEntity e1 = new LoadEntity(); e1.setId("L-1"); e1.setStatus("Requested");
        LoadEntity e2 = new LoadEntity(); e2.setId("L-2"); e2.setStatus("Delivered");
        when(repo.findAll()).thenReturn(List.of(e1, e2));

        var list = service.listLoads();
        assertEquals(2, list.size());
        assertEquals("L-1", list.iterator().next().getId());
    }

    @Test
    void test_updateStatus_updates_and_saves() {
        LoadEntity e = new LoadEntity();
        e.setId("L-3");
        e.setStatus("Requested");
        when(repo.findById("L-3")).thenReturn(Optional.of(e));

        service.updateStatus("L-3", "Dispatched");

        assertEquals("Dispatched", e.getStatus());
        verify(repo).save(e);
    }

    @Test
    void test_updateStatus_throws_when_missing() {
        when(repo.findById("nope")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus("nope", "Delivered"));
        assertTrue(ex.getMessage().contains("Load not found"));
    }

    @Test
    void test_listLoadsSorted_uses_sortResolver_and_returns_sorted_list() {
        LoadEntity e1 = new LoadEntity();
        e1.setId("L-1");
        e1.setReferenceNo("ACME");
        e1.setStatus("Requested");
        e1.setRateAmount(new BigDecimal("100.00"));

        LoadEntity e2 = new LoadEntity();
        e2.setId("L-2");
        e2.setReferenceNo("BETA");
        e2.setStatus("Requested");
        e2.setRateAmount(new BigDecimal("200.00"));

        when(repo.findAll()).thenReturn(List.of(e1, e2));

        Load d1 = new Load("L-1", "ACME", "Requested", 100f,
                null, null, null, null, null, null, null, null);
        Load d2 = new Load("L-2", "BETA", "Requested", 200f,
                null, null, null, null, null, null, null, null);
        
        List<Load> sorted = List.of(d2, d1);
        when(sortResolver.sort(anyList(), eq("RateAmountDesc"))).thenReturn(sorted);

        List<Load> result = service.listLoadsSorted("RateAmountDesc");

        assertSame(sorted, result);
        verify(repo).findAll();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Load>> captor = ArgumentCaptor.forClass(List.class);
        verify(sortResolver).sort(captor.capture(), eq("RateAmountDesc"));

        List<Load> passedToResolver = captor.getValue();
        assertEquals(2, passedToResolver.size());
        assertEquals("L-1", passedToResolver.get(0).getId());
        assertEquals("L-2", passedToResolver.get(1).getId());
    }

    @Test
    void test_searchLoads_returns_empty_list_when_query_blank() {
        List<Load> result1 = service.searchLoads(null, "id");
        List<Load> result2 = service.searchLoads("   ", "customer");

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        verifyNoInteractions(sortResolver);
        verifyNoInteractions(repo);
    }

    @Test
    void test_searchLoads_by_id_returns_single_load_when_found() {
        LoadEntity e = new LoadEntity();
        e.setId("L-100");
        e.setReferenceNo("ACME");
        e.setStatus("Delivered");
        e.setRateAmount(new BigDecimal("999.99"));

        when(repo.findById("L-100")).thenReturn(Optional.of(e));

        List<Load> result = service.searchLoads("L-100", "id");

        assertEquals(1, result.size());
        Load l = result.get(0);
        assertEquals("L-100", l.getId());
        assertEquals("ACME", l.getReferenceNo());
        assertEquals("Delivered", l.getStatus());
    }

    @Test
    void test_searchLoads_by_id_returns_empty_when_not_found() {
        when(repo.findById("missing")).thenReturn(Optional.empty());

        List<Load> result = service.searchLoads("missing", "id");

        assertTrue(result.isEmpty());
    }

    @Test
    void test_searchLoads_by_customer_uses_repository_and_maps_results() {
        LoadEntity e1 = new LoadEntity();
        e1.setId("L-1");
        e1.setReferenceNo("ACME Logistics");
        e1.setStatus("Requested");

        LoadEntity e2 = new LoadEntity();
        e2.setId("L-2");
        e2.setReferenceNo("Acme Corp");
        e2.setStatus("Delivered");

        when(repo.findByReferenceNoContainingIgnoreCase("acme"))
                .thenReturn(List.of(e1, e2));

        List<Load> result = service.searchLoads("acme", "customer");

        assertEquals(2, result.size());
        assertEquals("L-1", result.get(0).getId());
        assertEquals("L-2", result.get(1).getId());
        verify(repo).findByReferenceNoContainingIgnoreCase("acme");
    }

    @Test
    void test_searchLoads_by_status_uses_repository_and_maps_results() {
        LoadEntity e1 = new LoadEntity();
        e1.setId("L-1");
        e1.setReferenceNo("ACME");
        e1.setStatus("Delivered");

        LoadEntity e2 = new LoadEntity();
        e2.setId("L-2");
        e2.setReferenceNo("BETA");
        e2.setStatus("Delivered");

        when(repo.findByStatusIgnoreCase("Delivered"))
                .thenReturn(List.of(e1, e2));

        List<Load> result = service.searchLoads("Delivered", "status");

        assertEquals(2, result.size());
        assertEquals("L-1", result.get(0).getId());
        assertEquals("L-2", result.get(1).getId());
        verify(repo).findByStatusIgnoreCase("Delivered");
    }

    @Test
    void test_searchLoads_with_unknown_field_searches_all_and_deduplicates() {
        LoadEntity byId = new LoadEntity();
        byId.setId("L-1");
        byId.setReferenceNo("ACME");
        byId.setStatus("Requested");

        LoadEntity byCustomer = new LoadEntity();
        byCustomer.setId("L-1");
        byCustomer.setReferenceNo("ACME Logistics");
        byCustomer.setStatus("Requested");

        LoadEntity byStatus = new LoadEntity();
        byStatus.setId("L-2");
        byStatus.setReferenceNo("Other");
        byStatus.setStatus("Requested");

        when(repo.findById("L-1")).thenReturn(Optional.of(byId));
        when(repo.findByReferenceNoContainingIgnoreCase("L-1"))
                .thenReturn(List.of(byCustomer));
        when(repo.findByStatusIgnoreCase("L-1")).thenReturn(List.of(byStatus));

        List<Load> result = service.searchLoads("L-1", "weird-field");

        // Expect 2 unique IDs: L-1 and L-2
        assertEquals(2, result.size());
        assertEquals("L-1", result.get(0).getId());
        assertEquals("L-2", result.get(1).getId());
    }
}
