package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;

import java.util.List;

public interface LoadSortStrategy {

    String name();

    /**
     * Returns a new list of loads sorted according to this strategy.
     */
    List<Load> sort(List<Load> loads);
}
