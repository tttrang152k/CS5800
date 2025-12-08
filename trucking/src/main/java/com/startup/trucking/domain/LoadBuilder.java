package com.startup.trucking.domain;

public class LoadBuilder {

    private static final String DEFAULT_STATUS = "Requested";

    private String id;
    private String referenceNo;
    private String status = DEFAULT_STATUS;
    private float rateAmount;
    private String trackingId;

    private String rateConfirmationRef;
    private String driverId;
    private String trailerId;
    private String pickupAddress;
    private String deliveryAddress;
    private String pickupDate;
    private String deliveryDate;

    public LoadBuilder() { }

    public LoadBuilder setId(String id) {
        this.id = id; return this;
    }
    public LoadBuilder setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo; return this;
    }
    public LoadBuilder setStatus(String status) {
        this.status = status; return this;
    }
    public LoadBuilder setRateAmount(float rateAmount) {
        this.rateAmount = rateAmount; return this;
    }
    public LoadBuilder setTrackingId(String trackingId) {
        this.trackingId = trackingId; return this;
    }
    public LoadBuilder setRateConfirmationRef(String rateConfirmationRef) {
        this.rateConfirmationRef = rateConfirmationRef; return this;
    }
    public LoadBuilder setDriverId(String driverId) {
        this.driverId = driverId; return this;
    }
    public LoadBuilder setTrailerId(String trailerId) {
        this.trailerId = trailerId; return this;
    }
    public LoadBuilder setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress; return this;
    }
    public LoadBuilder setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress; return this;
    }
    public LoadBuilder setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate; return this;
    }
    public LoadBuilder setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate; return this;
    }

    public Load createLoad() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        return new Load(
                        id,
                        referenceNo,
                        status,
                        rateAmount,
                        trackingId,
                        rateConfirmationRef,
                        driverId,
                        trailerId,
                        pickupAddress,
                        deliveryAddress,
                        pickupDate,
                        deliveryDate
        );
    }
}
