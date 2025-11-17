package com.startup.trucking.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    private String id;

    @Column(name="load_id", nullable = false, unique = true)
    private String loadId;

    @Column(name="issued_at")
    private OffsetDateTime issuedAt;

    private String status;             // Draft, Sent, PartiallyPaid, Paid
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;

    @Column(name="customer_ref")
    private String customerRef;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLoadId() { return loadId; }
    public void setLoadId(String loadId) { this.loadId = loadId; }
    public OffsetDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(OffsetDateTime issuedAt) { this.issuedAt = issuedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getCustomerRef() { return customerRef; }
    public void setCustomerRef(String customerRef) { this.customerRef = customerRef; }
}
