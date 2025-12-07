package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateAmountDescSortStrategyTest {

    @Test
    void test_sort_sorts_by_rate_amount_descending() {
        RateAmountDescSortStrategy strategy = new RateAmountDescSortStrategy();

        Load l1 = new Load("L-1", "A", "Requested", 300f,
                null, null, null, null, null, null, null, null);
        Load l2 = new Load("L-2", "A", "Requested", 100f,
                null, null, null, null, null, null, null, null);
        Load l3 = new Load("L-3", "A", "Requested", 200f,
                null, null, null, null, null, null, null, null);

        List<Load> sorted = strategy.sort(List.of(l1, l2, l3));

        assertEquals("L-1", sorted.get(0).getId()); // 300
        assertEquals("L-3", sorted.get(1).getId()); // 200
        assertEquals("L-2", sorted.get(2).getId()); // 100
    }
}
