package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeliveryDateSortStrategyTest {

    @Test
    void test_sort_sorts_by_delivery_date_ascending() {
        DeliveryDateSortStrategy strategy = new DeliveryDateSortStrategy();

        Load l1 = new Load("L-1", "A", "Requested", 0f,
                null, null, null, null, null, null,
                null, "2025-02-01");
        Load l2 = new Load("L-2", "A", "Requested", 0f,
                null, null, null, null, null, null,
                null, "2025-01-01");
        Load l3 = new Load("L-3", "A", "Requested", 0f,
                null, null, null, null, null, null,
                null, "2025-03-01");

        List<Load> sorted = strategy.sort(List.of(l1, l2, l3));

        assertEquals("L-2", sorted.get(0).getId()); // 2025-01-01
        assertEquals("L-1", sorted.get(1).getId()); // 2025-02-01
        assertEquals("L-3", sorted.get(2).getId()); // 2025-03-01
    }
}
