package com.startup.trucking.persistence;

import com.startup.trucking.domain.Load;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class LoadMapper {
    private LoadMapper() {}

    public static Load toDomain(LoadEntity e) {
        if (e == null) return null;
        return new Load(
                e.getId(),
                e.getReferenceNo(),
                e.getStatus(),
                e.getRateAmount() == null ? 0f : e.getRateAmount().floatValue(),
                e.getTrackingId(),
                e.getRateConfirmationRef(),
                e.getDriverId(),
                e.getTrailerId(),
                e.getPickupAddress(),
                e.getDeliveryAddress(),
                e.getPickupDate(),
                e.getDeliveryDate()
        );
    }

    public static LoadEntity toEntity(Load l) {
        LoadEntity e = new LoadEntity();
        e.setId(l.getId());
        e.setReferenceNo(l.getReferenceNo());
        e.setStatus(l.getStatus());
        e.setRateAmount(BigDecimal
                .valueOf(l.getRateAmount())
                .setScale(2, RoundingMode.HALF_UP));
        e.setTrackingId(l.getTrackingId());
        e.setRateConfirmationRef(l.getRateConfirmationRef());
        e.setDriverId(l.getDriverId());
        e.setTrailerId(l.getTrailerId());
        e.setPickupAddress(l.getPickupAddress());
        e.setDeliveryAddress(l.getDeliveryAddress());
        e.setPickupDate(l.getPickupDate());
        e.setDeliveryDate(l.getDeliveryDate());
        return e;
    }
}