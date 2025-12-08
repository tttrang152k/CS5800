package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.service.LoadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Search Controller Unit Tests")
class SearchControllerTest {

    private static final String QUERY_ACME = "ACME";
    private static final String FIELD_CUSTOMER = "customer";

    @Mock
    LoadService loadService;

    @Test
    @DisplayName("searchLoads() - Populates model and returns search view")
    void test_searchLoads_whenQueryAndFieldProvided_populatesModelAndReturnsView() {
        SearchController controller = new SearchController(loadService);

        Load first = new Load(
                "L-1", "ACME", "Requested", 100f,
                null, null, null, null,
                null, null, null, null
        );
        Load second = new Load(
                "L-2", "ACME", "Delivered", 200f,
                null, null, null, null,
                null, null, null, null
        );

        when(loadService.searchLoads(QUERY_ACME, FIELD_CUSTOMER))
                .thenReturn(List.of(first, second));

        Model model = new ConcurrentModel();
        String viewName = controller.searchLoads(QUERY_ACME, FIELD_CUSTOMER, model);

        assertEquals("load-search", viewName);
        assertEquals(QUERY_ACME, model.getAttribute("query"));
        assertEquals(FIELD_CUSTOMER, model.getAttribute("field"));

        @SuppressWarnings("unchecked")
        List<Load> results = (List<Load>) model.getAttribute("results");
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("L-1", results.get(0).getId());
        assertEquals("L-2", results.get(1).getId());

        verify(loadService).searchLoads(QUERY_ACME, FIELD_CUSTOMER);
    }

    @Test
    @DisplayName("searchLoads() - Handles null query gracefully")
    void test_searchLoads_whenQueryNull_usesServiceAndReturnsEmptyResults() {
        SearchController controller = new SearchController(loadService);
        when(loadService.searchLoads(null, "id")).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String viewName = controller.searchLoads(null, "id", model);

        assertEquals("load-search", viewName);
        assertNull(model.getAttribute("query"));
        assertEquals("id", model.getAttribute("field"));

        @SuppressWarnings("unchecked")
        List<Load> results = (List<Load>) model.getAttribute("results");
        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(loadService).searchLoads(null, "id");
    }
}
