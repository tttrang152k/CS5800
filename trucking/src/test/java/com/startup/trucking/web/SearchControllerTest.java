package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.service.LoadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    LoadService loads;

    @Test
    void test_searchLoads_populates_model_and_returns_view() {
        SearchController ctl = new SearchController(loads);

        String query = "ACME";
        String field = "customer";

        Load l1 = new Load("L-1", "ACME", "Requested", 100f,
                null, null, null, null, null, null, null, null);
        Load l2 = new Load("L-2", "ACME", "Delivered", 200f,
                null, null, null, null, null, null, null, null);

        when(loads.searchLoads(query, field)).thenReturn(List.of(l1, l2));

        Model model = new ConcurrentModel();
        String view = ctl.searchLoads(query, field, model);

        assertEquals("load-search", view);
        assertEquals(query, model.getAttribute("query"));
        assertEquals(field, model.getAttribute("field"));

        @SuppressWarnings("unchecked")
        List<Load> results = (List<Load>) model.getAttribute("results");
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("L-1", results.get(0).getId());
        assertEquals("L-2", results.get(1).getId());

        verify(loads).searchLoads(query, field);
    }

    @Test
    void test_searchLoads_handles_null_query() {
        SearchController ctl = new SearchController(loads);

        when(loads.searchLoads(null, "id")).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String view = ctl.searchLoads(null, "id", model);

        assertEquals("load-search", view);
        assertNull(model.getAttribute("query"));
        assertEquals("id", model.getAttribute("field"));

        @SuppressWarnings("unchecked")
        List<Load> results = (List<Load>) model.getAttribute("results");
        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(loads).searchLoads(null, "id");
    }
}
