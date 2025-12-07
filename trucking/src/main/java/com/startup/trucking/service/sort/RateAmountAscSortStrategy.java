package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RateAmountAscSortStrategy implements LoadSortStrategy {

    @Override
    public String name() {
        return "RateAmountAsc";
    }

    @Override
    public List<Load> sort(List<Load> loads) {
        return loads.stream()
                .sorted(Comparator.comparing(Load::getRateAmount))
                .collect(Collectors.toList());
    }
}
