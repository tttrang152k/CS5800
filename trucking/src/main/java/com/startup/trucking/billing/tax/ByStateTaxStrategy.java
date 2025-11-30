package com.startup.trucking.billing.tax;

import com.startup.trucking.domain.Load;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ByStateTaxStrategy implements TaxStrategy {
    private static final Map<String, BigDecimal> RATES = Map.of(
            "CA", new BigDecimal("0.0725")
    );

    @Override
    public BigDecimal computeTax(Load load, BigDecimal subtotal) {
        if (load.getReferenceNo() == null) return BigDecimal.ZERO;
        String ref = load.getReferenceNo();
        String suffix = ref.contains("-") ? ref.substring(ref.lastIndexOf('-')+1) : "";
        BigDecimal rate = RATES.getOrDefault(suffix, BigDecimal.ZERO);
        return subtotal.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    @Override public String name() { return "ByCustomerStateTax"; }
}
