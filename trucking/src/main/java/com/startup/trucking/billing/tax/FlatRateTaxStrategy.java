package com.startup.trucking.billing.tax;

import com.startup.trucking.domain.Load;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FlatRateTaxStrategy implements TaxStrategy {
    // configurable via application.properties: tax.flat.rate=0.0725
    private final BigDecimal rate;

    public FlatRateTaxStrategy(@Value("${tax.flat.rate:0}") BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public BigDecimal computeTax(Load load, BigDecimal subtotal) {
        return subtotal.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    @Override public String name() { return "FlatRate"; }
}