package com.startup.trucking.web;

public class LoadRow {
    public String id;
    public String customerName;
    public String status;
    public float rateAmount;
    public String driver;
    public String pickupAddress;
    public String deliveryAddress;
    public String pickupDate;
    public String deliveryDate;
    public String documentUrl;
    public boolean invoiced;
    public String invoiceId;

    public LoadRow(String id,
                   String customerName,
                   String status,
                   float rateAmount,
                   String driver,
                   String pickupAddress,
                   String deliveryAddress,
                   String pickupDate,
                   String deliveryDate,
                   String documentUrl,
                   boolean invoiced,
                   String invoiceId) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.rateAmount = rateAmount;
        this.driver = driver;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.pickupDate = pickupDate;
        this.deliveryDate = deliveryDate;
        this.documentUrl = documentUrl;
        this.invoiced = invoiced;
        this.invoiceId = invoiceId;
    }
}
