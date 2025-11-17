package com.startup.trucking.billing;

import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.Map;

@Component
public class PaymentGatewayRegistry {
    private final Map<PaymentMethod, PaymentGateway> map = new EnumMap<>(PaymentMethod.class);

    public PaymentGatewayRegistry() {
        map.put(PaymentMethod.CARD, new CardGatewayAdapter());
        map.put(PaymentMethod.ACH,  new AchGatewayAdapter());
        map.put(PaymentMethod.CASH, new CashGatewayAdapter());
        map.put(PaymentMethod.CHECK,new CashGatewayAdapter()); // treat as CASH
    }

    public PaymentGateway resolve(PaymentMethod method) {
        PaymentGateway gw = map.get(method);
        if (gw == null) throw new IllegalArgumentException("Unsupported method: " + method);
        return gw;
    }
}
