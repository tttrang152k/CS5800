package com.startup.trucking.web;

public class BookingForm {
    private String id;
    private String referenceNo;
    private String status = "Requested";
    private float rateAmount;
    private String trackingId;
    private String rateConfirmationRef;
    private String driverId;
    private String trailerId;
    private String pickupAddress;
    private String deliveryAddress;
    private String pickupDate;
    private String deliveryDate;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public float getRateAmount() { return rateAmount; }
    public void setRateAmount(float rateAmount) { this.rateAmount = rateAmount; }
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public String getRateConfirmationRef() { return rateConfirmationRef; }
    public void setRateConfirmationRef(String rateConfirmationRef) { this.rateConfirmationRef = rateConfirmationRef; }
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public String getTrailerId() { return trailerId; }
    public void setTrailerId(String trailerId) { this.trailerId = trailerId; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getPickupDate() { return pickupDate; }
    public void setPickupDate(String pickupDate) { this.pickupDate = pickupDate; }
    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
}
