package com.startup.trucking.billing.tax;

import com.startup.trucking.domain.Load;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NoTaxStrategy implements TaxStrategy {
    @Override public BigDecimal computeTax(Load load, BigDecimal subtotal) { return BigDecimal.ZERO; }
    @Override public String name() { return "NoTax"; }
}
