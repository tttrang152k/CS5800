package com.startup.trucking.domain;

public class Load {

    private final String id;
    private final String referenceNo;
    private String status;
    private final float rateAmount;
    private final String trackingId;

    private final String rateConfirmationRef;
    private final String driverId;
    private final String trailerId;
    private final String pickupAddress;
    private final String deliveryAddress;
    private final String pickupDate;
    private final String deliveryDate;

    public Load(String id,
                String referenceNo,
                String status,
                float rateAmount,
                String trackingId,
                String rateConfirmationRef,
                String driverId,
                String trailerId,
                String pickupAddress,
                String deliveryAddress,
                String pickupDate,
                String deliveryDate) {

        this.id = id;
        this.referenceNo = referenceNo;
        this.status = status;
        this.rateAmount = rateAmount;
        this.trackingId = trackingId;
        this.rateConfirmationRef = rateConfirmationRef;
        this.driverId = driverId;
        this.trailerId = trailerId;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.pickupDate = pickupDate;
        this.deliveryDate = deliveryDate;
    }

    public void updateStatus(String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Load status must not be null or blank");
        }
        this.status = newStatus;
    }

    public String getId() {
        return id;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public String getStatus() {
        return status;
    }

    public float getRateAmount() {
        return rateAmount;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getRateConfirmationRef() {
        return rateConfirmationRef;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }
}
