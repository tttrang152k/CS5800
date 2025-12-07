package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadSortStrategyResolver {

    private final List<LoadSortStrategy> strategies;
    private final LoadSortStrategy defaultStrategy;

    // Default strategy = CustomerNameAsc
    public LoadSortStrategyResolver(List<LoadSortStrategy> strategies,
                                    CustomerNameSortStrategy defaultStrategy) {
        this.strategies = strategies;
        this.defaultStrategy = defaultStrategy;
    }

    public List<Load> sort(List<Load> loads, String sortKey) {
        if (loads == null || loads.isEmpty()) {
            return loads;
        }

        if (sortKey == null || sortKey.isBlank()) {
            return defaultStrategy.sort(loads);
        }

        for (LoadSortStrategy s : strategies) {
            if (sortKey.equalsIgnoreCase(s.name())) {
                return s.sort(loads);
            }
        }

        // Fallback if sortKey doesn't match anything
        return defaultStrategy.sort(loads);
    }
}
