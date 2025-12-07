package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoadSortStrategyResolverTest {

    @Test
    void test_sort_uses_default_strategy_when_sortKey_null() {
        CustomerNameSortStrategy defaultStrategy = new CustomerNameSortStrategy();
        RateAmountDescSortStrategy rateDesc = new RateAmountDescSortStrategy();

        LoadSortStrategyResolver resolver = new LoadSortStrategyResolver(
                List.of(defaultStrategy, rateDesc),
                defaultStrategy
        );

        Load l1 = new Load("L-1", "Zeta", "Requested", 0f,
                null, null, null, null, null, null, null, null);
        Load l2 = new Load("L-2", "Acme", "Requested", 0f,
                null, null, null, null, null, null, null, null);

        List<Load> input = List.of(l1, l2);

        List<Load> expected = defaultStrategy.sort(input);
        List<Load> actual = resolver.sort(input, null);

        assertEquals(expected.get(0).getId(), actual.get(0).getId());
        assertEquals(expected.get(1).getId(), actual.get(1).getId());
    }

    @Test
    void test_sort_uses_matching_strategy_when_sortKey_matches() {
        CustomerNameSortStrategy defaultStrategy = new CustomerNameSortStrategy();
        RateAmountDescSortStrategy rateDesc = new RateAmountDescSortStrategy();

        LoadSortStrategyResolver resolver = new LoadSortStrategyResolver(
                List.of(defaultStrategy, rateDesc),
                defaultStrategy
        );

        Load l1 = new Load("L-1", "A", "Requested", 100f,
                null, null, null, null, null, null, null, null);
        Load l2 = new Load("L-2", "A", "Requested", 300f,
                null, null, null, null, null, null, null, null);
        Load l3 = new Load("L-3", "A", "Requested", 200f,
                null, null, null, null, null, null, null, null);

        List<Load> input = List.of(l1, l2, l3);

        List<Load> expected = rateDesc.sort(input);
        List<Load> actual = resolver.sort(input, "RateAmountDesc");

        assertEquals(expected.get(0).getId(), actual.get(0).getId());
        assertEquals(expected.get(1).getId(), actual.get(1).getId());
        assertEquals(expected.get(2).getId(), actual.get(2).getId());
    }

    @Test
    void test_sort_falls_back_to_default_when_sortKey_unknown() {
        CustomerNameSortStrategy defaultStrategy = new CustomerNameSortStrategy();
        RateAmountAscSortStrategy rateAsc = new RateAmountAscSortStrategy();

        LoadSortStrategyResolver resolver = new LoadSortStrategyResolver(
                List.of(defaultStrategy, rateAsc),
                defaultStrategy
        );

        Load l1 = new Load("L-1", "Zeta", "Requested", 0f,
                null, null, null, null, null, null, null, null);
        Load l2 = new Load("L-2", "Acme", "Requested", 0f,
                null, null, null, null, null, null, null, null);

        List<Load> input = List.of(l1, l2);

        List<Load> expected = defaultStrategy.sort(input);
        List<Load> actual = resolver.sort(input, "UnknownKey");

        assertEquals(expected.get(0).getId(), actual.get(0).getId());
        assertEquals(expected.get(1).getId(), actual.get(1).getId());
    }
}
