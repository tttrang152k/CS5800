package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerNameSortStrategyTest {

    @Test
    void test_sort_sorts_by_customer_name_alphabetically() {
        CustomerNameSortStrategy strategy = new CustomerNameSortStrategy();

        Load l1 = new Load("L-1", "Zeta", "Requested", 0f,
                null, null, null, null, null, null, null, null);
        Load l2 = new Load("L-2", "Acme", "Requested", 0f,
                null, null, null, null, null, null, null, null);
        Load l3 = new Load("L-3", "Beta", "Requested", 0f,
                null, null, null, null, null, null, null, null);

        List<Load> input = List.of(l1, l2, l3);
        List<Load> sorted = strategy.sort(input);

        assertEquals("L-2", sorted.get(0).getId()); // Acme
        assertEquals("L-3", sorted.get(1).getId()); // Beta
        assertEquals("L-1", sorted.get(2).getId()); // Zeta
    }
}
