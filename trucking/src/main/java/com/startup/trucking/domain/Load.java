package com.startup.trucking.domain;

public class Load {
    private final String id;
    private String referenceNo;
    private String status;
    private float rateAmount;
    private String trackingId;

    // collapsed details
    private String rateConfirmationRef;
    private String driverId;
    private String trailerId;
    private String pickupAddress;
    private String deliveryAddress;
    private String pickupDate;
    private String deliveryDate;

    public Load(String id, String referenceNo, String status, float rateAmount, String trackingId, String rateConfirmationRef, String driverId, String trailerId,
                String pickupAddress, String deliveryAddress, String pickupDate, String deliveryDate) {
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

    public void updateStatus(String status){
        this.status = status;
    }

    // getters
    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getDriverId() { return driverId; }
    public String getTrailerId() { return trailerId; }
    public String getReferenceNo() { return referenceNo; }
    public String getPickupAddress(){ return pickupAddress; }
    public String getDeliveryAddress(){ return deliveryAddress; }
    public String getPickupDate(){ return pickupDate; }
    public String getDeliveryDate(){ return deliveryDate; }
    public float getRateAmount(){ return rateAmount; }
    public String getTrackingId(){ return trackingId; }
    public String getRateConfirmationRef(){ return rateConfirmationRef; }
}

