package com.startup.trucking.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "loads")
public class LoadEntity {
    @Id
    private String id;

    @Column(name = "reference_no")
    private String referenceNo;

    private String status;

    @Column(name = "rate_amount")
    private BigDecimal rateAmount;

    @Column(name = "tracking_id")
    private String trackingId;

    @Column(name = "rate_confirmation_ref")
    private String rateConfirmationRef;

    @Column(name = "driver_id")
    private String driverId;

    @Column(name = "trailer_id")
    private String trailerId;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "pickup_date")
    private String pickupDate;

    @Column(name = "delivery_date")
    private String deliveryDate;

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getRateAmount() { return rateAmount; }
    public void setRateAmount(BigDecimal rateAmount) { this.rateAmount = rateAmount; }
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