package com.startup.trucking.service.sort;

import com.startup.trucking.domain.Load;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeliveryDateSortStrategy implements LoadSortStrategy {

    @Override
    public String name() {
        return "DeliveryDateAsc";
    }

    @Override
    public List<Load> sort(List<Load> loads) {
        return loads.stream()
                .sorted(Comparator.comparing(l -> parseDate(l.getDeliveryDate())))
                .collect(Collectors.toList());
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.MAX;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            return LocalDate.MAX;
        }
    }
}
