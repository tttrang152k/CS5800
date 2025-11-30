package com.startup.trucking.billing.tax;

import com.startup.trucking.domain.Load;
import java.math.BigDecimal;

public interface TaxStrategy {
    BigDecimal computeTax(Load load, BigDecimal subtotal);
    String name();
}
