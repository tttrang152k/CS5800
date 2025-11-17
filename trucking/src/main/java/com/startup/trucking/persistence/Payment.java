package com.startup.trucking.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String id;

    @Column(name = "invoice_id", nullable = false)
    private String invoiceId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String method;     // CARD, ACH, CASH, CHECK

    @Column(name = "paid_at", nullable = false)
    private OffsetDateTime paidAt;

    private String reference;
    @Column(nullable = false)
    private String status;     // Pending, Cleared, Failed

    private String provider;
    @Column(name = "auth_code")
    private String authCode;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public OffsetDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(OffsetDateTime paidAt) { this.paidAt = paidAt; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }
}
