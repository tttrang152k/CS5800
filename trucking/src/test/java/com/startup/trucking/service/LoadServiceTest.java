package com.startup.trucking.service;

import com.startup.trucking.domain.Load;
import com.startup.trucking.persistence.LoadEntity;
import com.startup.trucking.persistence.LoadRepository;
import com.startup.trucking.service.sort.LoadSortStrategyResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Load Service Unit Tests")
class LoadServiceTest {

    private static final String LOAD_ID = "L-1";
    private static final String CUSTOMER_ACME = "ACME";
    private static final String STATUS_REQUESTED = "Requested";
    private static final String STATUS_DELIVERED = "Delivered";

    @Mock
    LoadRepository loadRepository;

    @Mock
    LoadSortStrategyResolver sortResolver;

    @InjectMocks
    LoadService loadService;

    // ---------------------------------------------------------------------
    // getLoad
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("getLoad() - Existing load is mapped to domain object")
    void test_getLoad_whenLoadExists_returnsDomainLoad() {
        LoadEntity entity = createLoadEntity(LOAD_ID, CUSTOMER_ACME, STATUS_REQUESTED, "100.00");
        when(loadRepository.findById(LOAD_ID)).thenReturn(Optional.of(entity));

        Load load = loadService.getLoad(LOAD_ID);

        assertEquals(LOAD_ID, load.getId());
        assertEquals(CUSTOMER_ACME, load.getReferenceNo());
        assertEquals(STATUS_REQUESTED, load.getStatus());
        assertEquals(100.0f, load.getRateAmount(), 0.0001);
    }

    @Test
    @DisplayName("getLoad() - Missing load throws exception")
    void test_getLoad_whenMissing_throwsException() {
        when(loadRepository.findById("missing")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loadService.getLoad("missing")
        );

        assertTrue(exception.getMessage().contains("Load not found"));
    }

    // ---------------------------------------------------------------------
    // putLoad
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("putLoad() - Valid load is saved as entity")
    void test_putLoad_whenValid_savesEntity() {
        Load load = new Load(
                "L-2", CUSTOMER_ACME, STATUS_REQUESTED, 150f,
                "TRK", "RC", "EMP", "TRL",
                "PU", "DEL", "2025-01-01", "2025-01-02"
        );

        loadService.putLoad(load);

        ArgumentCaptor<LoadEntity> entityCaptor = ArgumentCaptor.forClass(LoadEntity.class);
        verify(loadRepository).save(entityCaptor.capture());

        LoadEntity saved = entityCaptor.getValue();
        assertEquals("L-2", saved.getId());
        assertEquals(CUSTOMER_ACME, saved.getReferenceNo());
        assertEquals(STATUS_REQUESTED, saved.getStatus());
        assertEquals(new BigDecimal("150.00"), saved.getRateAmount());
    }

    @Test
    @DisplayName("putLoad() - Null or blank id is rejected")
    void test_putLoad_whenNullOrBlankId_throwsException() {
        IllegalArgumentException nullLoadException = assertThrows(
                IllegalArgumentException.class,
                () -> loadService.putLoad(null)
        );
        assertTrue(nullLoadException.getMessage().contains("load.id"));

        Load badLoad = new Load(
                "", null, STATUS_REQUESTED, 0f,
                null, null, null, null,
                null, null, null, null
        );

        IllegalArgumentException blankIdException = assertThrows(
                IllegalArgumentException.class,
                () -> loadService.putLoad(badLoad)
        );
        assertTrue(blankIdException.getMessage().contains("load.id"));
    }

    // ---------------------------------------------------------------------
    // listLoads
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listLoads() - Returns all domain loads")
    void test_listLoads_returnsAllDomainLoads() {
        LoadEntity first = createLoadEntity("L-1", "First", STATUS_REQUESTED, "10.00");
        LoadEntity second = createLoadEntity("L-2", "Second", STATUS_DELIVERED, "20.00");
        when(loadRepository.findAll()).thenReturn(List.of(first, second));

        var loads = loadService.listLoads();

        assertEquals(2, loads.size());
        assertEquals("L-1", loads.iterator().next().getId());
    }

    // ---------------------------------------------------------------------
    // listLoadsSorted
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listLoadsSorted() - Delegates to sort resolver and returns sorted list")
    void test_listLoadsSorted_delegatesToSortResolver_andReturnsSortedList() {
        LoadEntity first = createLoadEntity("L-1", "First", STATUS_REQUESTED, "100.00");
        LoadEntity second = createLoadEntity("L-2", "Second", STATUS_REQUESTED, "200.00");
        when(loadRepository.findAll()).thenReturn(List.of(first, second));

        Load domainFirst = createDomainLoad("L-1", "First", STATUS_REQUESTED, 100f);
        Load domainSecond = createDomainLoad("L-2", "Second", STATUS_REQUESTED, 200f);

        List<Load> sorted = List.of(domainSecond, domainFirst); // pretend sorted desc
        when(sortResolver.sort(anyList(), eq("RateAmountDesc"))).thenReturn(sorted);

        List<Load> result = loadService.listLoadsSorted("RateAmountDesc");

        assertSame(sorted, result);
        verify(loadRepository).findAll();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Load>> loadsCaptor = ArgumentCaptor.forClass(List.class);
        verify(sortResolver).sort(loadsCaptor.capture(), eq("RateAmountDesc"));

        List<Load> passedToResolver = loadsCaptor.getValue();
        assertEquals(2, passedToResolver.size());
        assertEquals("L-1", passedToResolver.get(0).getId());
        assertEquals("L-2", passedToResolver.get(1).getId());
    }

    // ---------------------------------------------------------------------
    // updateStatus
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("updateStatus() - Existing load is updated and saved")
    void test_updateStatus_whenLoadExists_updatesEntityAndSaves() {
        LoadEntity entity = createLoadEntity("L-3", CUSTOMER_ACME, STATUS_REQUESTED, "50.00");
        when(loadRepository.findById("L-3")).thenReturn(Optional.of(entity));

        loadService.updateStatus("L-3", STATUS_DELIVERED);

        assertEquals(STATUS_DELIVERED, entity.getStatus());
        verify(loadRepository).save(entity);
    }

    @Test
    @DisplayName("updateStatus() - Missing load throws exception")
    void test_updateStatus_whenMissing_throwsException() {
        when(loadRepository.findById("nope")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loadService.updateStatus("nope", STATUS_DELIVERED)
        );

        assertTrue(exception.getMessage().contains("Load not found"));
    }

    // ---------------------------------------------------------------------
    // searchLoads
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("searchLoads() - Blank query returns empty list and skips repository calls")
    void test_searchLoads_whenQueryBlank_returnsEmptyAndSkipsRepository() {
        assertTrue(loadService.searchLoads(null, "id").isEmpty());
        assertTrue(loadService.searchLoads("   ", "customer").isEmpty());

        verifyNoInteractions(loadRepository);
        verifyNoInteractions(sortResolver);
    }

    @Test
    @DisplayName("searchLoads() - Field 'id' returns single result when present")
    void test_searchLoads_whenFieldId_returnsSingleResult() {
        LoadEntity entity = createLoadEntity("L-100", CUSTOMER_ACME, STATUS_DELIVERED, "999.99");
        when(loadRepository.findById("L-100")).thenReturn(Optional.of(entity));

        List<Load> result = loadService.searchLoads("L-100", "id");

        assertEquals(1, result.size());
        Load load = result.get(0);
        assertEquals("L-100", load.getId());
        assertEquals(CUSTOMER_ACME, load.getReferenceNo());
        assertEquals(STATUS_DELIVERED, load.getStatus());
    }

    @Test
    @DisplayName("searchLoads() - Field 'id' returns empty list when not found")
    void test_searchLoads_whenFieldId_notFound_returnsEmptyList() {
        when(loadRepository.findById("missing")).thenReturn(Optional.empty());

        List<Load> result = loadService.searchLoads("missing", "id");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("searchLoads() - Field 'customer' uses repository and maps results")
    void test_searchLoads_whenFieldCustomer_usesRepositoryAndMapsResults() {
        LoadEntity first = createLoadEntity("L-1", "ACME Logistics", STATUS_REQUESTED, "10.00");
        LoadEntity second = createLoadEntity("L-2", "Acme Corp", STATUS_DELIVERED, "20.00");
        when(loadRepository.findByReferenceNoContainingIgnoreCase("acme"))
                .thenReturn(List.of(first, second));

        List<Load> result = loadService.searchLoads("acme", "customer");

        assertEquals(2, result.size());
        assertEquals("L-1", result.get(0).getId());
        assertEquals("L-2", result.get(1).getId());
        verify(loadRepository).findByReferenceNoContainingIgnoreCase("acme");
    }

    @Test
    @DisplayName("searchLoads() - Field 'status' uses repository and maps results")
    void test_searchLoads_whenFieldStatus_usesRepositoryAndMapsResults() {
        LoadEntity first = createLoadEntity("L-1", CUSTOMER_ACME, STATUS_DELIVERED, "1.00");
        LoadEntity second = createLoadEntity("L-2", "BETA", STATUS_DELIVERED, "2.00");
        when(loadRepository.findByStatusIgnoreCase(STATUS_DELIVERED))
                .thenReturn(List.of(first, second));

        List<Load> result = loadService.searchLoads(STATUS_DELIVERED, "status");

        assertEquals(2, result.size());
        assertEquals("L-1", result.get(0).getId());
        assertEquals("L-2", result.get(1).getId());
        verify(loadRepository).findByStatusIgnoreCase(STATUS_DELIVERED);
    }

    @Test
    @DisplayName("searchLoads() - Uppercase field name is normalized")
    void test_searchLoads_whenFieldUsesUppercase_isNormalizedToLowercase() {
        LoadEntity first = createLoadEntity("L-1", CUSTOMER_ACME, STATUS_DELIVERED, "1.00");
        when(loadRepository.findByStatusIgnoreCase(STATUS_DELIVERED))
                .thenReturn(List.of(first));

        List<Load> result = loadService.searchLoads(STATUS_DELIVERED, "STATUS");

        assertEquals(1, result.size());
        verify(loadRepository).findByStatusIgnoreCase(STATUS_DELIVERED);
    }

    @Test
    @DisplayName("searchLoads() - Unknown field searches across all fields and deduplicates")
    void test_searchLoads_whenFieldUnknown_searchesAllFieldsAndDeduplicates() {
        LoadEntity fromId = createLoadEntity("L-1", CUSTOMER_ACME, STATUS_REQUESTED, "1.00");
        LoadEntity fromCustomer = createLoadEntity("L-1", "ACME Logistics", STATUS_REQUESTED, "1.00");
        LoadEntity fromStatus = createLoadEntity("L-2", "Other", STATUS_REQUESTED, "1.00");

        when(loadRepository.findById("L-1")).thenReturn(Optional.of(fromId));
        when(loadRepository.findByReferenceNoContainingIgnoreCase("L-1"))
                .thenReturn(List.of(fromCustomer));
        when(loadRepository.findByStatusIgnoreCase("L-1"))
                .thenReturn(List.of(fromStatus));

        List<Load> result = loadService.searchLoads("L-1", "weird-field");

        assertEquals(2, result.size());
        assertEquals("L-1", result.get(0).getId());
        assertEquals("L-2", result.get(1).getId());
    }

    // ---------------------------------------------------------------------
    // Test helpers
    // ---------------------------------------------------------------------

    private LoadEntity createLoadEntity(String id,
                                        String referenceNo,
                                        String status,
                                        String rateAmount) {
        LoadEntity entity = new LoadEntity();
        entity.setId(id);
        entity.setReferenceNo(referenceNo);
        entity.setStatus(status);
        entity.setRateAmount(new BigDecimal(rateAmount));
        return entity;
    }

    private Load createDomainLoad(String id,
                                  String referenceNo,
                                  String status,
                                  float rateAmount) {
        return new Load(
                id, referenceNo, status, rateAmount,
                null, null, null, null,
                null, null, null, null
        );
    }
}
