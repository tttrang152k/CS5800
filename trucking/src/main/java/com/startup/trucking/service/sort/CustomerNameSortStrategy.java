package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerNameSortStrategy implements LoadSortStrategy {

    @Override
    public String name() {
        return "CustomerNameAsc";
    }

    @Override
    public List<Load> sort(List<Load> loads) {
        return loads.stream()
                .sorted(Comparator.comparing(
                        l -> safe(l.getReferenceNo()),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .collect(Collectors.toList());
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
