package com.startup.trucking.billing.tax;

import com.startup.trucking.domain.Load;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TaxStrategyResolver {

    private final List<TaxStrategy> strategies;
    private final TaxStrategy defaultStrategy;

    public TaxStrategyResolver(List<TaxStrategy> strategies, NoTaxStrategy defaultStrategy) {
        this.strategies = strategies;
        this.defaultStrategy = defaultStrategy; // fallback
    }

    public BigDecimal compute(Load load, BigDecimal subtotal) {
        // If referenceNo ends with "-CA" and we have ByCustomerRefTaxStrategy, use it
        // Else if a FlatRate is configured (> 0), Spring’s bean is present—use it
        // Else default no-tax
        for (TaxStrategy s : strategies) {
            if ("ByCustomerStateTax".equals(s.name()) && matchesByCustomerRef(load)) {
                return s.computeTax(load, subtotal);
            }
        }
        for (TaxStrategy s : strategies) {
            if ("FlatRate".equals(s.name())) {
                BigDecimal t = s.computeTax(load, subtotal);
                if (t.signum() > 0) return t;
            }
        }
        return defaultStrategy.computeTax(load, subtotal);
    }

    private boolean matchesByCustomerRef(Load load) {
        String ref = load.getReferenceNo();
        return ref != null && ref.endsWith("-CA");
    }
}