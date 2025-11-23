package com.startup.trucking.service;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.LoadEntity;
import com.startup.trucking.persistence.LoadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> service.putLoad(null));
        assertTrue(ex1.getMessage().contains("load.id"));

        Load bad = new Load("", null, "Requested", 0f, null, null, null, null, null, null, null, null);
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> service.putLoad(bad));
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
}
