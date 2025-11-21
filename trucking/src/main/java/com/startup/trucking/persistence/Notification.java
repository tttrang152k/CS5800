package com.startup.trucking.persistence;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private String id;                    // e.g., NT-123...
    private String customerRef;           // customer name or id
    private String type;                  // LoadConfirmed, InvoiceCreated, PaymentReceived
    private String channel;
    private String recipient;             // email/phone/token
    @Column(length=2000)
    private String message;
    private String refType;               // Load, Invoice, Payment
    private String refId;
    private OffsetDateTime createdAt;

    // getters/setters
    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getCustomerRef() { return customerRef; } public void setCustomerRef(String c) { this.customerRef = c; }
    public String getType() { return type; } public void setType(String t) { this.type = t; }
    public String getChannel() { return channel; } public void setChannel(String c) { this.channel = c; }
    public String getRecipient() { return recipient; } public void setRecipient(String r) { this.recipient = r; }
    public String getMessage() { return message; } public void setMessage(String m) { this.message = m; }
    public String getRefType() { return refType; } public void setRefType(String r) { this.refType = r; }
    public String getRefId() { return refId; } public void setRefId(String r) { this.refId = r; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime t) { this.createdAt = t; }
}